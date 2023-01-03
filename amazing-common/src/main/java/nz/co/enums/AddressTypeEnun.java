package nz.co.enums;


import lombok.Getter;


public enum AddressTypeEnun {
    COMMON_ADDRESS(0),
    DEFAULT_ADDRESS(1);
    private AddressTypeEnun(int code){
        this.code = code;
    }
    @Getter
    private int code;
}
