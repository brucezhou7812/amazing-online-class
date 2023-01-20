package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nz.co.constant.ConstantOnlineClass;
import nz.co.model.ProductDO;
import nz.co.mapper.ProductMapper;
import nz.co.service.ProductService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class ProductServiceImpl  implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Override
    public Map<String, Object> listPageByPage(int page, int size) {
        Page<ProductDO> pageInfo = new Page<ProductDO>(page,size);
        IPage<ProductDO> ipage = productMapper.selectPage(pageInfo,new QueryWrapper<ProductDO>()
                                    .orderByDesc("create_time"));
        if(ipage == null)
            return null;
        long total_records = ipage.getTotal();
        long total_pages = ipage.getPages();

        List<ProductVO> current_page = ipage.getRecords().stream().map(obj->{
            ProductVO productVO = beanProcess(obj);
            return productVO;
        }).collect(Collectors.toList());
        Map<String,Object> mapPage = new HashMap<>(3);
        mapPage.put(ConstantOnlineClass.PAGINATION_TOTAL_RECORDS,total_records);
        mapPage.put(ConstantOnlineClass.PAGINATION_TOTAL_PAGES,total_pages);
        mapPage.put(ConstantOnlineClass.PAGINATION_CURRENT_DATA,current_page);
        return mapPage;
    }

    @Override
    public ProductVO listProductDetailById(Long product_id) {
        ProductDO productDO = productMapper.selectById(product_id);
        return beanProcess(productDO);
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
