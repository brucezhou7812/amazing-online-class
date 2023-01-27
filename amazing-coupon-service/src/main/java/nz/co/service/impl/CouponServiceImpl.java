package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.BizCodeEnum;
import nz.co.enums.CouponCategoryEnum;
import nz.co.enums.CouponPublishEnum;
import nz.co.enums.CouponUseStateEnum;
import nz.co.exception.BizCodeException;
import nz.co.interceptor.LoginInterceptor;
import nz.co.mapper.CouponRecordMapper;
import nz.co.model.CouponDO;
import nz.co.mapper.CouponMapper;
import nz.co.model.CouponRecordDO;
import nz.co.model.UserLoginModel;
import nz.co.requst.NewUserRequest;
import nz.co.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.utils.CommonUtils;
import nz.co.vo.CouponVO;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collector;
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
public class CouponServiceImpl implements CouponService {

    @Autowired
    private CouponMapper couponMapper;
    @Autowired
    private CouponRecordMapper couponRecordMapper;
    @Autowired
    private RedissonClient redissonClient;
    @Override
    public Map<String, Object> listCouponInPage(int pages, int size) {
        Page<CouponDO> pageinfo = new Page<CouponDO>(pages,size);
        QueryWrapper<CouponDO> queryWrapper = new QueryWrapper<CouponDO>()
                .eq("category", CouponCategoryEnum.COUPON_CATEGORY_PROMOTION.getDesc())
                .eq("publish", CouponPublishEnum.COUPON_PUBLISH.getDesc())
                .orderByDesc("create_time");
        IPage<CouponDO> page = couponMapper.selectPage(pageinfo,queryWrapper);
        Map<String,Object> pageMap = new HashMap<>(3);
        pageMap.put(ConstantOnlineClass.PAGINATION_TOTAL_PAGES,page.getPages());
        pageMap.put(ConstantOnlineClass.PAGINATION_TOTAL_RECORDS,page.getTotal());
        pageMap.put(ConstantOnlineClass.PAGINATION_CURRENT_DATA,page.getRecords().stream().map(obj->{
            return transformCouponDOtoVO(obj);
        }).collect(Collectors.toList()));
        return pageMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public CouponRecordDO recevieCoupon(Long coupon_id,CouponCategoryEnum category) {
        UserLoginModel userLoginModel = LoginInterceptor.threadLocalUserLoginModel.get();
        Long userId = userLoginModel.getId();

        String lockKey = "lock:coupon:"+coupon_id;
        RLock rLock = redissonClient.getLock(lockKey);
        rLock.lock();
        log.info("Get the lock successfully.");
        try{

            QueryWrapper<CouponDO> queryWrapper = new QueryWrapper<CouponDO>()
                    .eq("id", coupon_id)
                    .eq("category", category.getDesc());
            CouponDO couponDO = couponMapper.selectOne(queryWrapper);
            checkCoupon(couponDO, userId);
            CouponRecordDO couponRecordDO = new CouponRecordDO();
            BeanUtils.copyProperties(couponDO, couponRecordDO);
            couponRecordDO.setId(null);
            couponRecordDO.setCouponId(coupon_id);
            couponRecordDO.setUserId(userId);
            couponRecordDO.setCreateTime(new Date());
            couponRecordDO.setUserName((userLoginModel.getName()));
            couponRecordDO.setUseState(CouponUseStateEnum.COUPON_NEW.getDesc());

            //decrease stock
            int row = couponMapper.reduceStock(coupon_id);
            if (row == 1) {
                couponRecordMapper.insert(couponRecordDO);
            }
            return couponRecordDO;
        }finally {
            rLock.unlock();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class,propagation = Propagation.REQUIRED)
    public List<CouponRecordDO> receiveInitCoupon(NewUserRequest newUserRequest, CouponCategoryEnum couponCategoryNewUser) {
        UserLoginModel userLoginModel = new UserLoginModel();
        Long userId = newUserRequest.getUserId();
        userLoginModel.setId(userId);
        userLoginModel.setName(newUserRequest.getName());
        LoginInterceptor.threadLocalUserLoginModel.set(userLoginModel);
        List<CouponRecordDO> result = new ArrayList<>();
        QueryWrapper<CouponDO> queryWrapper = new QueryWrapper<CouponDO>()
                .eq("category",couponCategoryNewUser.getDesc());
        List<CouponDO> couponDOList = couponMapper.selectList(queryWrapper);
        for(CouponDO couponDO:couponDOList){

            CouponRecordDO couponRecordDO = recevieCoupon(couponDO.getId(),couponCategoryNewUser);
            result.add(couponRecordDO);
        }
        return result.isEmpty()? null:result;
    }

    private void checkCoupon(CouponDO couponDO,Long userId){
        if(couponDO == null){
            throw new BizCodeException(BizCodeEnum.COUPON_NOT_EXIST);
        }
        if(!couponDO.getPublish().equals(CouponPublishEnum.COUPON_PUBLISH.getDesc())){
            throw new BizCodeException(BizCodeEnum.COUPON_NOT_EXIST);
        }
        if(couponDO.getStock() == 0){
            throw new BizCodeException(BizCodeEnum.COUPON_NOT_EXIST);
        }
        Date start = couponDO.getStartTime();
        Date end = couponDO.getEndTime();
        Long time = CommonUtils.getTimestamp();
        if(time < start.getTime() || time > end.getTime()){
            throw new BizCodeException(BizCodeEnum.COUPON_NOT_EXIST);
        }
        int userLimit = couponRecordMapper.selectCount(new QueryWrapper<CouponRecordDO>()
        .eq("user_id",userId)
        .eq("coupon_id",couponDO.getId()));
        if(userLimit >= couponDO.getUserLimit()){
            throw new BizCodeException(BizCodeEnum.COUPON_EXCEED_USER_LIMIT);
        }
    }

    private CouponVO transformCouponDOtoVO(CouponDO couponDO){
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO,couponVO);
        return couponVO;
    }
}
