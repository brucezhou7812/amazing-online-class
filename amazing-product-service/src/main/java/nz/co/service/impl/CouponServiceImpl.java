package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import nz.co.constant.ConstantOnlineClass;
import nz.co.enums.CouponCategoryEnum;
import nz.co.enums.CouponPublishEnum;
import nz.co.model.CouponDO;
import nz.co.mapper.CouponMapper;
import nz.co.service.CouponService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.vo.CouponVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
    private CouponVO transformCouponDOtoVO(CouponDO couponDO){
        CouponVO couponVO = new CouponVO();
        BeanUtils.copyProperties(couponDO,couponVO);
        return couponVO;
    }
}
