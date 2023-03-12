package nz.co.component;

import lombok.extern.slf4j.Slf4j;
import nz.co.enums.OrderPayTypeEnum;
import nz.co.vo.PayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

@Slf4j
public class PayTool {
    private PayService payService;
    private PayContext payContext;

    public PayTool(PayInfoVO payInfoVO){
        if(OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payInfoVO.getPayType())){
            payService = new AliPayService();
        }else if(OrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payInfoVO.getPayType())){
            payService = new WeChatPayService();
        }else{
            payService = new AliPayService();
        }
        payContext = new PayContext(payService,payInfoVO);
    }

    public String pay(){
        return this.payContext.executePay();
    }
    public String refund(){
        return this.payContext.executeRefund();
    }
    public String queryPayStatus(){
        return this.payContext.executeQueryPayStatus();
    }
}
