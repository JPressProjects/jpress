package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jpress.model.Coupon;
import io.jpress.service.CouponCodeService;
import io.jpress.model.CouponCode;
import io.jboot.service.JbootServiceBase;
import io.jpress.service.CouponService;

import java.util.Date;

@Bean
public class CouponCodeServiceProvider extends JbootServiceBase<CouponCode> implements CouponCodeService {

    @Inject
    private CouponService couponService;

    @Override
    public CouponCode findByCode(String code) {
        return DAO.findFirstByColumn(Column.create("code", code));
    }

    @Override
    public boolean valid(CouponCode couponCode) {
        Coupon coupon = couponService.findById(couponCode.getCouponId());
        if (coupon == null || !coupon.isNormal()) {
            return false;
        }

        Date date = couponCode.getCreated();
        int validtype = coupon.getValidType();

        //绝对时间内有效
        if (validtype == Coupon.VALID_TYPE_ABSOLUTELY_EFFECTIVE) {

        }
        //相对时间内有效
        else if (validtype == Coupon.VALID_TYPE_RELATIVELY_EFFECTIVE) {

        }

        return false;
    }
}