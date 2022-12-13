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
@TableName("user")
public class UserDO implements Serializable {

    private static final long serialVersionUID = 1L;

      @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * nick name
     */
    private String name;

    /**
     * password
     */
    private String pwd;

    /**
     * portait
     */
    private String headImg;

    /**
     * signature
     */
    private String slogan;

    /**
     * 1 male, 0 female
     */
    private Integer sex;

    /**
     * points
     */
    private Integer points;

    /**
     * create time 
     */
    private Date createTime;

    /**
     * email
     */
    private String mail;

    /**
     * private key
     */
    private String secret;


}
