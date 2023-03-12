package nz.co.component;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayApiException;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.alipay.api.response.AlipayTradeWapPayResponse;
import lombok.extern.slf4j.Slf4j;
import nz.co.config.AlipayConfig;
import nz.co.config.PayUrlConfig;
import nz.co.enums.BizCodeEnum;
import nz.co.exception.BizCodeException;
import nz.co.utils.CommonUtils;
import nz.co.vo.PayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@Slf4j
public class AliPayService implements PayService {
    @Autowired
    private PayUrlConfig payUrlConfig;
  
    @Override
    public String pay(PayInfoVO payInfoVO) {
        HashMap<String,String> content = new HashMap<>();
        content.put("out_trade_no",payInfoVO.getSerailNo());
        content.put("product_code","FAST_INSTANCE_TRADE_PAY");
        content.put("total_amount",payInfoVO.getPayFee().toString());
        content.put("subject",payInfoVO.getTitle());
        double timeout = Math.floor(payInfoVO.getTimeoutMills()/(1000*60));
        if(timeout<1)
            throw new BizCodeException(BizCodeEnum.ORDER_TIMEOUT);
        content.put("timeout_express",Double.valueOf(timeout).intValue()+"m");
        content.put("body",payInfoVO.getDescription());
        String form = null;
        try {
            if (payInfoVO.getClientType().equalsIgnoreCase("H5")) {
                AlipayTradeWapPayRequest request = new AlipayTradeWapPayRequest();
                request.setBizContent(JSON.toJSONString(content));
                PayUrlConfig config = new PayUrlConfig();
                request.setNotifyUrl(config.getAlipayCallbackUrl());
                request.setReturnUrl(config.getAlipaySuccessReturnUrl());
                AlipayTradeWapPayResponse res = AlipayConfig.getInstance().pageExecute(request);
                if (res.isSuccess()) {
                    log.info("Success to invoke alipay sdk. ");
                    form = res.getBody();

                } else {
                    log.info("Failed to invoke alipay sdk. ");

                }
            } else if(payInfoVO.getClientType().equalsIgnoreCase("PC")) {
                AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
                request.setBizContent(JSON.toJSONString(content));
                request.setNotifyUrl(payUrlConfig.getAlipayCallbackUrl());
                request.setReturnUrl(payUrlConfig.getAlipaySuccessReturnUrl());
                AlipayTradePagePayResponse res = AlipayConfig.getInstance().pageExecute(request);
                if (res.isSuccess()) {
                    log.info("Success to invoke alipay sdk. ");
                    form = res.getBody();

                } else {
                    log.info("Failed to invoke alipay sdk. ");

                }
            }
        }catch(AlipayApiException e){
            log.error("Alipay exception "+e);
        }

        return form;
    }

    @Override
    public String refund(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String queryPayStatus(PayInfoVO payInfoVO) {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        HashMap<String,String> content = new HashMap<>();
        content.put("out_trade_no",payInfoVO.getSerailNo());
        request.setBizContent(JSON.toJSONString(content));
        AlipayTradeQueryResponse response = null;
        try {
            response = AlipayConfig.getInstance().execute(request);

        } catch (AlipayApiException e) {
            e.printStackTrace();
        }
        if(response.isSuccess()){
            log.info("Succeed to query the status of the order.");
            String result = response.getBody();
            return result;
        } else {
            log.error("Fail to query the status of the order.");
            return "";
        }

    }
}
