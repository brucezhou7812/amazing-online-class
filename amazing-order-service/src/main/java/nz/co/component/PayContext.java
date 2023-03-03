package nz.co.component;

import nz.co.vo.PayInfoVO;

public class PayContext {
    private PayService pay;
    private PayInfoVO payInfoVO;
    public PayContext(PayService payService,PayInfoVO payInfoVO){
        pay = payService;
        this.payInfoVO = payInfoVO;
    }
    public String executePay(){
        return pay.pay(payInfoVO);
    }
    public String executeRefund(){
        return pay.refund(payInfoVO);
    }
    public String executeQueryPayStatus(){
        return pay.queryPayStatus(payInfoVO);
    }
}
