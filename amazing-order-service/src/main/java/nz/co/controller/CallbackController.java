package nz.co.controller;

import com.alibaba.fastjson.serializer.JSONLibDataFormatSerializer;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.AlipayConfig;
import nz.co.enums.OrderPayTypeEnum;
import nz.co.service.ProductOrderService;
import nz.co.utils.JsonData;
import org.checkerframework.checker.units.qual.K;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/order/callback/v1")
@Api(tags="Alipay callback controller")
@Slf4j
public class CallbackController {
    @Autowired
    private ProductOrderService productOrderService;

    @PostMapping("alipay")
    public String alipayCallback(HttpServletRequest request, HttpServletResponse response){
        Map<String,String> params = convertRequestToMap(request);
        log.info("The notification from Alipay: " +params);
        boolean sign = false;
        try {
            sign = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUB_KEY,AlipayConfig.CHARSET,AlipayConfig.SIGN_TYPE);
            if(sign){
                JsonData jsonData = productOrderService.handleOrderCallback(OrderPayTypeEnum.ALIPAY,params);
                if(jsonData.getCode() == 0)
                    return "success";
            }
        } catch (AlipayApiException e) {
            log.error("Signature validation exception: "+e+" params: "+params);
        }

        return "failure";
    }

    private Map<String,String> convertRequestToMap(HttpServletRequest request){
        Map<String,String> paramsMap = new HashMap<>();
        Set<Map.Entry<String, String[]>> entrySet = request.getParameterMap().entrySet();
        for(Map.Entry<String, String[]> entry:entrySet){
            String name = entry.getKey();
            String[] values = entry.getValue();
            String value = "";
            if(values.length == 1){
                value = values[0];
            }else{
                value="";
            }
            paramsMap.put(name,value);
        }
        return paramsMap;
    }
}
