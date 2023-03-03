package nz.co.component;

import nz.co.vo.PayInfoVO;
import org.springframework.stereotype.Service;

@Service
public class WeChatPayService implements PayService {
    @Override
    public String pay(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String refund(PayInfoVO payInfoVO) {
        return null;
    }

    @Override
    public String queryPayStatus(PayInfoVO payInfoVO) {
        return null;
    }
}
