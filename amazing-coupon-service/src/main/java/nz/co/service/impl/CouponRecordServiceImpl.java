package nz.co.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import nz.co.interceptor.LoginInterceptor;
import nz.co.model.CouponRecordDO;
import nz.co.mapper.CouponRecordMapper;
import nz.co.model.UserLoginModel;
import nz.co.service.CouponRecordService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.vo.CouponRecordVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
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
public class CouponRecordServiceImpl implements CouponRecordService {
    @Autowired
    private CouponRecordMapper couponRecordMapper;
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

    private CouponRecordVO beanProcess(CouponRecordDO couponRecordDO){
        CouponRecordVO couponRecordVO = new CouponRecordVO();
        BeanUtils.copyProperties(couponRecordDO,couponRecordVO);
        return couponRecordVO;
    }
}
