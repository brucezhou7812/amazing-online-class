package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.CouponTaskLockStateEnum;
import nz.co.enums.CouponUseStateEnum;
import nz.co.enums.OrderStateEnum;
import nz.co.exception.BizCodeException;
import nz.co.feign.OrderFeignService;
import nz.co.interceptor.LoginInterceptor;
import nz.co.mapper.CouponTaskMapper;
import nz.co.model.*;
import nz.co.mapper.CouponRecordMapper;
import nz.co.request.LockCouponRecordRequest;
import nz.co.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.service.CouponTaskService;
import nz.co.utils.JsonData;
import nz.co.vo.CouponRecordVO;
import nz.co.vo.CouponTaskVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-05
 */
@Service
@Slf4j
public class CouponRecordServiceImpl implements CouponRecordService {
    @Autowired
    private CouponRecordMapper couponRecordMapper;
    @Autowired
    private CouponTaskMapper couponTaskMapper;
    @Autowired
    private OrderFeignService orderFeignService;
    @Autowired
    private CouponTaskService couponTaskService;
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private RabbitMqConfig rabbitMqConfig;
    @Override
    public Map<String, Object> page(int page, int size) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        Page<CouponRecordDO> pageinfo = new Page<CouponRecordDO>(page,size);
        IPage<CouponRecordDO> ipage = couponRecordMapper.selectPage(pageinfo,new QueryWrapper<CouponRecordDO>()
                .eq("user_id",userLoginModel.getId())
                .orderByDesc("create_time"));
        Map<String,Object> pageMap = new HashMap<String,Object>(3);
        pageMap.put("totalPages",ipage.getPages());
        pageMap.put("totalRecords",ipage.getTotal());
        pageMap.put("currentPage",ipage.getRecords().stream().map(obj->beanProcess(obj)).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    public CouponRecordVO findRecordById(int record_id) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        QueryWrapper<CouponRecordDO> queryWrapper = new QueryWrapper<CouponRecordDO>()
                .eq("user_id",userLoginModel.getId())
                .eq("id",record_id);
        CouponRecordDO couponRecordDO = couponRecordMapper.selectOne(queryWrapper);

        return couponRecordDO==null? null:beanProcess(couponRecordDO);
    }

    @Override
    public JsonData lockCouponRecordBatch(LockCouponRecordRequest lockCouponRecordRequest) {
        String serialNum = lockCouponRecordRequest.getSerialNum();
        List<Long> couponRecordIds = lockCouponRecordRequest.getCouponRecordIds();
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        if(userLoginModel == null) return null;
        Long userId = userLoginModel.getId();
        int updateRows = couponRecordMapper.updateUserStateBatch(couponRecordIds, CouponUseStateEnum.COUPON_USED.getDesc(), userId);
        List<CouponTaskDO> couponTaskDOs = couponRecordIds.stream().map(obj->{
            CouponTaskDO couponTask = new CouponTaskDO();
            couponTask.setCouponRecordId(obj);
            couponTask.setCreateTime(new Date());
            couponTask.setSerialNum(serialNum);
            couponTask.setLockState(CouponTaskLockStateEnum.LOCKED.name());
            return couponTask;
        }).collect(Collectors.toList());
        int insertRows = couponTaskMapper.insertBatch(couponTaskDOs);
        log.info("Success to update use_state in the table of coupon_record batch : "+updateRows);
        log.info("Success to insert coupon task :"+insertRows);
        if(updateRows == couponRecordIds.size() && updateRows == insertRows){
            couponTaskDOs.stream().forEach(new Consumer<CouponTaskDO>(){
                @Override
                public void accept(CouponTaskDO couponTask) {
                    CouponRecordMessage couponRecordMessage = new CouponRecordMessage();
                    couponRecordMessage.setCouponTaskId(couponTask.getId());
                    couponRecordMessage.setSerialNum(serialNum);
                    rabbitTemplate.convertAndSend(rabbitMqConfig.getCouponEventExchange(),rabbitMqConfig.getCouponReleaseDelayRoutingKey(),couponRecordMessage);
                    log.info("coupon record message send successfully: "+couponRecordMessage.toString());
                }
            });
            return JsonData.buildSuccess();
        }else{
            throw new BizCodeException(BizCodeEnum.COUPON_LOCK_FAIL);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public boolean releaseCouponRecord(CouponRecordMessage recordMessage) {
        String serialNo = recordMessage.getSerialNum();
        Long couponTaskId = recordMessage.getCouponTaskId();
        CouponTaskDO couponTaskDO = couponTaskService.queryById(couponTaskId);
        if(couponTaskDO == null){
            log.warn("Coupon Task Record does not exist or internal error exist: "+recordMessage);
            return true;
        }
        if(CouponTaskLockStateEnum.LOCKED.name().equalsIgnoreCase(couponTaskDO.getLockState())){
            JsonData orderJsonData = orderFeignService.queryOrderStateBySerialNo(serialNo);
            if(orderJsonData.getCode() == 0){

                String state = (String)orderJsonData.getData();;
                if(OrderStateEnum.PAY.name().equalsIgnoreCase(state)){
                    couponTaskDO.setLockState(CouponTaskLockStateEnum.FINISHED.name());
                    couponTaskService.updateLockState(couponTaskDO);
                    log.info("The order has been paid,set coupon task to FINISHED :" +recordMessage);
                    return true;
                }if(OrderStateEnum.NEW.name().equalsIgnoreCase(state)){
                    //couponTaskDO.setLockState(CouponTaskLockStateEnum.CANCELLED.name());
                    //couponTaskService.updateLockState(couponTaskDO);
                    log.info("The order has not been paid,return the message to queue :" +recordMessage);
                    return false;
                }
            }

            log.warn("The order does not exist or cancelled,update coupon task to CANCELLED and restore coupon state to NEW:"+recordMessage);
            couponTaskDO.setLockState(CouponTaskLockStateEnum.CANCELLED.name());
            couponTaskService.updateLockState(couponTaskDO);
            updateUseState(couponTaskDO.getCouponRecordId(),CouponUseStateEnum.COUPON_NEW.getDesc());



        }else{
            log.warn("The state of Coupon Task Record is not LOCKED: "+recordMessage);
            return true;
        }

        return false;
    }
    public int updateUseState(Long couponRecordId,String useState){
        return couponRecordMapper.updateUseState(couponRecordId,useState);
    }

    private CouponRecordVO beanProcess(CouponRecordDO couponRecordDO){
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO,couponRecordVO);
        return couponRecordVO;
    }
}
