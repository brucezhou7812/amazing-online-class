package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.RabbitMqConfig;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.CouponTaskLockStateEnum;
import nz.co.enums.CouponUseStateEnum;
import nz.co.exception.BizCodeException;
import nz.co.interceptor.LoginInterceptor;
import nz.co.mapper.CouponTaskMapper;
import nz.co.model.CouponRecordDO;
import nz.co.mapper.CouponRecordMapper;
import nz.co.model.CouponRecordMessage;
import nz.co.model.CouponTaskDO;
import nz.co.model.UserLoginModel;
import nz.co.request.LockCouponRecordRequest;
import nz.co.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.utils.JsonData;
import nz.co.vo.CouponRecordVO;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private CouponRecordVO beanProcess(CouponRecordDO couponRecordDO){
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO,couponRecordVO);
        return couponRecordVO;
    }
}
