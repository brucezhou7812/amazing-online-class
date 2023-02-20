package nz.co.model;


import lombok.Data;

@Data
public class OrderItemRequest {
    private Long userId;
    private String serailNo;
    private Long productId;
    private String productTitle;
    private Integer buyNum;
}
