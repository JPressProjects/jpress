package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponService;
import io.jpress.model.Coupon;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.CouponUsedRecordService;

@Bean
public class CouponServiceProvider extends JbootServiceBase<Coupon> implements CouponService {

    @Inject
    private CouponCodeService couponCodeService;


    @Inject
    private CouponUsedRecordService usedRecordService;

    @Override
    public void doSyncTakeCount(long couponId) {
        long count = couponCodeService.queryCountByCouponId(couponId);
        Coupon coupon = findById(couponId);
        coupon.setTakeCount(count);
        update(coupon);
    }

    @Override
    public void doSyncUsedCount(long couponId) {
        long count = usedRecordService.queryCountByCouponId(couponId);
        Coupon coupon = findById(couponId);
        coupon.setUsedCount(count);
        update(coupon);
    }
}