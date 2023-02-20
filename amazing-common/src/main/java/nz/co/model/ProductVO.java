package nz.co.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-01-20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ProductVO{

    @JsonProperty("id")
    private Long id;

    /**
     * the product title to show
     */
    @JsonProperty("title")
    private String title;

    /**
     * the image of the product
     */
    @JsonProperty(value="cover_img")
    private String coverImg;

    /**
     * the detail description of the product
     */
    @JsonProperty(value="detail")
    private String detail;

    /**
     * the price of the product
     */
    @JsonProperty(value="price")
    private BigDecimal price;

    /**
     * creating time
     */
    @JsonProperty(value="create_time")
    private Date createTime;

    /**
     * the old price of the product
     */
    @JsonProperty(value="old_price")
    private BigDecimal oldPrice;


    /**
     * the stock of the product
     */
    @JsonProperty(value="stock")
    private Integer stock;


}
