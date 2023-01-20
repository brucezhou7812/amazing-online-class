package nz.co.controller;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import nz.co.enums.BizCodeEnum;
import nz.co.service.ProductService;
import nz.co.utils.JsonData;
import nz.co.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
@RestController
@Api(tags="Product Service")
@RequestMapping("/api/product/v1")
public class ProductController {
    @Autowired
    private ProductService productService;
    @GetMapping("list_product_by_page")
    @ApiOperation("list products page by page")
    public JsonData listProductPageByPage(@ApiParam(value="current page")@RequestParam(value="page",required = true) int page,
                                          @ApiParam(value="records in each page")@RequestParam(value="size",required = true) int size){
        Map<String,Object> mapResult = productService.listPageByPage(page,size);
        return mapResult == null ?JsonData.buildResult(BizCodeEnum.PRODUCT_NOT_EXIST):JsonData.buildSuccess(mapResult);
    }

    @GetMapping("product_detail/{product_id}")
    @ApiOperation("list product detail")
    public JsonData productDetail(@ApiParam("product id")@PathVariable Long product_id){
        ProductVO productVO = productService.listProductDetailById(product_id);
        return productVO == null?JsonData.buildResult(BizCodeEnum.PRODUCT_NOT_EXIST):JsonData.buildSuccess(productVO);
    }

}

