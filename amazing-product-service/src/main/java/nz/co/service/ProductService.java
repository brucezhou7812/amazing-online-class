package nz.co.service;


import nz.co.model.ProductRecordMessage;
import nz.co.model.LockProductsRequest;
import nz.co.model.ProductVO;

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

    boolean releaseStock(ProductRecordMessage recordMessage);

    int updateStock(Long productId,Integer buyNum);
}
