package nz.co.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.TableId;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bruce Zhou
 * @since 2023-02-10
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cart_task")
public class CartTaskDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * product id
     */
    private Long productId;

    /**
     * amount of goods
     */
    private Integer buyNum;

    /**
     * product name
     */
    private String productName;

    /**
     * LOCKED,FINISHED,CANCELLED
     */
    private String lockState;

    /**
     * the serial number of the order
     */
    private String serialNum;

    /**
     * create time
     */
    private Date createTime;

    /**
     * user id
     */
    private Long userId;


}
