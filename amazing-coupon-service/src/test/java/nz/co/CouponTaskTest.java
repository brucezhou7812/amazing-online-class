package nz.co;

import nz.co.enums.CouponTaskLockStateEnum;
import nz.co.mapper.CouponTaskMapper;
import nz.co.model.CouponTaskDO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@SpringBootTest(classes = CouponApplication.class)
@RunWith(SpringRunner.class)
public class CouponTaskTest {
    @Autowired
    private CouponTaskMapper couponTaskMapper;
    @Test
    public void testCouponTaskDoInsert(){
        CouponTaskDO couponTaskDO = new CouponTaskDO();
        couponTaskDO.setSerialNum("113r353q");
        couponTaskDO.setCouponRecordId(110L);
        couponTaskDO.setCreateTime(new Date());
        couponTaskDO.setLockState(CouponTaskLockStateEnum.LOCKED.name());
        List<CouponTaskDO> list = new ArrayList<>();
        list.add(couponTaskDO);
        couponTaskMapper.insertBatch(list);
    }
}
