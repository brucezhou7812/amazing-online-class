package nz.co.exception;

import lombok.Data;
import nz.co.enums.BizCodeEnum;

@Data
public class BizCodeException extends RuntimeException {
    private int code;
    private String message;
    public BizCodeException(int code,String message){
        super(message);
        this.code = code;
        this.message = message;
    }
    public BizCodeException(BizCodeEnum bizCodeEnum){
        super(bizCodeEnum.getMessage());
        this.code = bizCodeEnum.getCode();
        this.message = bizCodeEnum.getMessage();
    }

    public BizCodeException() {

    }
}
