package nz.co.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
@Data
public class ProductOrderItemVO {

    private Long id;

    /**
     * 订单号
     */
    private Long productOrderId;

    private String outTradeNo;

    /**
     * 产品id
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productTitle;

    /**
     * 商品图片
     */
    private String productImg;

    /**
     * 购买数量
     */
    private Integer buyNum;

    private Date createTime;

    /**
     * 购物项商品总价格
     */
    private BigDecimal totalFee;

    /**
     * 购物项商品单价
     */
    private BigDecimal fee;

}
