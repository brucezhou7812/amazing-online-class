package nz.co.service;


import nz.co.request.LockProductsRequest;
import nz.co.vo.ProductVO;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
public interface ProductService {

    Map<String, Object> listPageByPage(int page, int size);

    ProductVO listProductDetailById(Long product_id);

    List<ProductVO> listProductsBatch(List<String> productIds);

    int lockStock(LockProductsRequest lockProductsRequest);
}
