package nz.co.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import nz.co.model.BannerDO;
import nz.co.mapper.BannerMapper;
import nz.co.service.BannerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import nz.co.vo.BannerVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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
public class BannerServiceImpl implements BannerService {
    @Autowired
    private BannerMapper bannerMapper;
    @Override
    public List<BannerVO> list() {
        QueryWrapper<BannerDO> queryWrapper = new QueryWrapper<BannerDO>()
                .orderByDesc("weight");
        List<BannerDO> bannerDOList = bannerMapper.selectList(queryWrapper);
        if(bannerDOList!=null || !bannerDOList.isEmpty()) {
            List<BannerVO> bannerVOList = bannerDOList.stream().map(obj->{
                BannerVO bannerVO = new BannerVO();
                BeanUtils.copyProperties(obj,bannerVO);
                return bannerVO;
            }).collect(Collectors.toList());
            return bannerVOList;
        }else {
            return null;
        }
    }
}
