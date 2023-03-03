package nz.co.component;

import lombok.extern.slf4j.Slf4j;
import nz.co.enums.OrderPayTypeEnum;
import nz.co.vo.PayInfoVO;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
public class PayTool {
    @Autowired
    private AliPayService aliPayService;
    @Autowired
    private WeChatPayService weChatPayService;
    private PayService payService;
    private PayContext payContext;

    public PayTool(PayInfoVO payInfoVO){
        if(OrderPayTypeEnum.ALIPAY.name().equalsIgnoreCase(payInfoVO.getPayType())){
            payService = aliPayService;
        }else if(OrderPayTypeEnum.WECHAT.name().equalsIgnoreCase(payInfoVO.getPayType())){
            payService = weChatPayService;
        }else{
            payService = aliPayService;
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
