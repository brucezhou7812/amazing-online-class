package nz.co.component;

import nz.co.vo.PayInfoVO;

public interface PayService {
    default String pay(PayInfoVO payInfoVO) {
        return null;
    }

    default String refund(PayInfoVO payInfoVO) {
        return null;
    }

    default String queryPayStatus(PayInfoVO payInfoVO) {
        return null;
    }
}
