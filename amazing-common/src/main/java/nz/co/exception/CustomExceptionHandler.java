package nz.co.exception;

import lombok.extern.slf4j.Slf4j;
import nz.co.utils.JsonData;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;


//@ControllerAdvice
//@Slf4j
//public class CustomExceptionHandler{
//    @ExceptionHandler(value=Exception.class)
//    @ResponseBody
//    JsonData customExceptionHandler(Exception e){
//        if(e instanceof BizCodeException){
//            BizCodeException exception = (BizCodeException)e;
//            log.error("Service error {}",e);
//            return JsonData.buildCodeAndMsg(exception.getCode(), exception.getMessage());
//        }else{
//            log.error("Global error {}",e);
//            return JsonData.buildCodeAndMsg(-1,"Global error.");
//        }
//    }
//}
