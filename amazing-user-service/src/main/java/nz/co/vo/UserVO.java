package nz.co.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserVO {
    private Long id;

    /**
     * nick name
     */
    private String name;


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
     * email
     */
    private String mail;

}
