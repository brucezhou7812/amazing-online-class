package nz.co.model;

import lombok.Data;

@Data
public class ProductRecordMessage {
    private Long messageId;
    private String serialNo;
    private Long productTaskId;
}
