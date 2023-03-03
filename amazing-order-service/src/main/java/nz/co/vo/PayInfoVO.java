package nz.co.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayInfoVO {
    private String serailNo;
    private BigDecimal payFee;
    private String payType;
    private String clientType;
    private String title;
    private String description;
    private long timeoutMills;
}
