package nz.co.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author Bruce Zhou
 * @since 2022-12-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("address")
public class AddressDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * user id
     */
    private Long userId;

    /**
     * 0:false,1:true
     */
    private Integer defaultStatus;

    /**
     * the name of the pepole who receives the goods
     */
    private String receiveName;

    /**
     * phone number
     */
    private String phone;

    private String province;

    private String city;

    private String region;

    private String detailAddress;

    private Date createTime;


}
