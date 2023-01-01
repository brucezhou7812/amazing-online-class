package nz.co.service;

import nz.co.enums.SendCodeEnum;
import nz.co.utils.JsonData;

public interface NotifyService {
    JsonData sendCode(SendCodeEnum sendCodeEnum,String to);
    boolean checkCode(SendCodeEnum sendCodeEnum,String to,String code);
}
