package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.*;
import nz.co.exception.BizCodeException;
import nz.co.feign.OrderFeignService;
import nz.co.model.*;
import nz.co.mapper.ProductMapper;
import nz.co.service.ProductService;
import nz.co.service.ProductTaskService;
import nz.co.utils.JsonData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
@Service
@Slf4j
public class ProductServiceImpl  implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private ProductTaskService productTaskService;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private OrderFeignService orderFeignService;

    @Override
    public Map<String, Object> listPageByPage(int page, int size) {
        Page<ProductDO> pageInfo = new Page<ProductDO>(page, size);
        IPage<ProductDO> ipage = productMapper.selectPage(pageInfo, new QueryWrapper<ProductDO>()
                .orderByDesc("create_time"));
        if (ipage == null)
            return null;
        long total_records = ipage.getTotal();
        long total_pages = ipage.getPages();

        List<ProductVO> current_page = ipage.getRecords().stream().map(obj -> {
            ProductVO productVO = beanProcess(obj);
            return productVO;
        }).collect(Collectors.toList());
        Map<String, Object> mapPage = new HashMap<>(3);
        mapPage.put(ConstantOnlineClass.PAGINATION_TOTAL_RECORDS, total_records);
        mapPage.put(ConstantOnlineClass.PAGINATION_TOTAL_PAGES, total_pages);
        mapPage.put(ConstantOnlineClass.PAGINATION_CURRENT_DATA, current_page);
        return mapPage;
    }

    @Override
    public ProductVO listProductDetailById(Long product_id) {
        ProductDO productDO = productMapper.selectById(product_id);
        return beanProcess(productDO);
    }

    public ProductVO listProductDetailBySerialNo(String serialNo) {
        ProductDO productDO = productMapper.selectOne(new QueryWrapper<ProductDO>()
                .eq("serial_no", serialNo));
        return beanProcess(productDO);
    }

    @Override
    public List<ProductVO> listProductsBatch(List<String> productIds) {
        QueryWrapper<ProductDO> queryWrapper = new QueryWrapper<ProductDO>()
                .in("id", productIds);
        List<ProductDO> products = productMapper.selectList(queryWrapper);
        List<ProductVO> productVOS = products.stream().map(obj -> {
            return beanProcess(obj);
        }).collect(Collectors.toList());
        return productVOS;
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public int lockStock(LockProductsRequest lockProductsRequest) {
        String serialNo = lockProductsRequest.getSerialNo();
        List<OrderItemRequest> orderItems = lockProductsRequest.getOrderItems();
        List<String> productIds = orderItems.stream().map(obj -> {
            Long productId = obj.getProductId();
            return Long.toString(productId);
        }).collect(Collectors.toList());
        List<ProductVO> productVOS = this.listProductsBatch(productIds);
        Map<Long, ProductVO> productVOMap = productVOS.stream().collect(Collectors.toMap(ProductVO::getId, Function.identity()));
        for (OrderItemRequest orderItem : orderItems) {
            Long id = orderItem.getProductId();
            Integer buyNum = orderItem.getBuyNum();
            int rows = productMapper.lockStock(id, buyNum);
            if (rows != 1) throw new BizCodeException(BizCodeEnum.ORDER_LOCK_STOCK_FAILED);
            ProductTaskDO productTaskDO = new ProductTaskDO();
            productTaskDO.setBuyNum(buyNum);
            productTaskDO.setCreateTime(new Date());
            productTaskDO.setProductId(id);
            productTaskDO.setSerialNum(serialNo);
            productTaskDO.setProductName(productVOMap.get(id).getDetail());
            productTaskDO.setLockState(ProductTaskLockStateEnum.LOCKED.name());
            productTaskService.insert(productTaskDO);
            ProductRecordMessage productRecordMessage = new ProductRecordMessage();
            productRecordMessage.setProductTaskId(productTaskDO.getId());
            productRecordMessage.setSerialNo(serialNo);
            rabbitTemplate.convertAndSend(rabbitMqConfig.getStockEventExchange(), rabbitMqConfig.getStockReleaseDelayRoutingKey(), productRecordMessage);
            log.info("Send ProductRecordMessage to RabbitMq: " + productRecordMessage);
        }
        return 0;
    }

    @Override
    public boolean releaseStock(ProductRecordMessage recordMessage) {
        Long taskId = recordMessage.getProductTaskId();
        String serialNo = recordMessage.getSerialNo();
        ProductTaskDO productTaskDO = productTaskService.queryTaskById(taskId);
        if (productTaskDO == null) {
            log.warn("Product task does not exist: " + recordMessage);
            return true;
        }
        String state = productTaskDO.getLockState();
        if (ProductTaskLockStateEnum.LOCKED.name().equalsIgnoreCase(state)) {
            JsonData<String> jsonData = orderFeignService.queryOrderStateBySerialNo(serialNo);
            if (jsonData.getCode() == 0) {
                String orderState = jsonData.getData();
                if (OrderStateEnum.PAY.name().equalsIgnoreCase(orderState)) {
                    productTaskDO.setLockState(ProductTaskLockStateEnum.FINISHED.name());
                    productTaskService.update(productTaskDO);
                    log.info("The order has been paid,set product task state to FINISHED :" + recordMessage);
                    return true;
                } else if (OrderStateEnum.NEW.name().equalsIgnoreCase(orderState)) {
                    log.info("The order has not been paid,return the message to queue :" + recordMessage);
                    return false;
                }
            }


        }
        log.warn("The order does not exist or cancelled,update product task to CANCELLED and restore product stock: " + recordMessage);
        productTaskDO.setLockState(ProductTaskLockStateEnum.CANCELLED.name());
        productTaskService.update(productTaskDO);
        updateStock(productTaskDO.getProductId(), productTaskDO.getBuyNum());
        return false;
    }


    @Override
    public int updateStock(Long productId,Integer buyNum){
        return productMapper.updateStock(productId,buyNum);
    }

    private ProductVO beanProcess(ProductDO productDO){
        if(productDO == null)
            return null;
        ProductVO productVO = new ProductVO();
        BeanUtils.copyProperties(productDO,productVO);
        productVO.setStock(productDO.getStock()-productDO.getLockStock());
        return productVO;
    }
}
