package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nz.co.enums.BizCodeEnum;
import nz.co.service.BannerService;
import nz.co.utils.JsonData;
import nz.co.vo.BannerVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
@RestController
@Api(tags="Product Service: Banner")
@RequestMapping("/api/banner/v1")
public class BannerController {
    @Autowired
    private BannerService bannerService;
    @GetMapping("list")
    @ApiOperation("list all of the banners")
    public JsonData list(){
        List<BannerVO> bannerVOList = bannerService.list();
        return (bannerVOList==null)||(bannerVOList.isEmpty()) ?
                JsonData.buildResult(BizCodeEnum.BANNER_NOT_EXIST):JsonData.buildSuccess(bannerVOList);
    }
}

