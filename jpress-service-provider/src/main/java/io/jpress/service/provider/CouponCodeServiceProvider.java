package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Coupon;
import io.jpress.model.CouponCode;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponService;
import io.jpress.service.UserService;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.util.Date;

@Bean
public class CouponCodeServiceProvider extends JbootServiceBase<CouponCode> implements CouponCodeService {

    @Inject
    private CouponService couponService;


    @Inject
    private UserService userService;

    @Override
    public Page<CouponCode> paginateByCouponId(int page, int pageSize, Long couponId) {
        return userService.join(paginateByColumns(page, pageSize, Columns.create("coupon_id", couponId), "id desc"), "user_id");
    }

    @Override
    public CouponCode findByCode(String code) {
        return DAO.findFirstByColumn(Column.create("code", code));
    }

    @Override
    public boolean valid(CouponCode couponCode, BigDecimal orderTotalAmount,long usedUserId) {

        // 该优惠码被标识为：不可用
        if (couponCode == null || !couponCode.isNormal()) {
            return false;
        }

        Coupon coupon = couponService.findById(couponCode.getCouponId());
        // 该优惠券不可用
        if (coupon == null || !coupon.isNormal()) {
            return false;
        }

        //是否是只有优惠券拥有者可用
        Boolean withOwner =coupon.getWithOwner();
        if (withOwner != null && withOwner && !couponCode.getUserId().equals(usedUserId)){
            return false;
        }


        Date validTime = couponCode.getValidTime();
        int validtype = coupon.getValidType();

        //绝对时间内有效
        if (validtype == Coupon.VALID_TYPE_ABSOLUTELY_EFFECTIVE) {
            return validTime.getTime() > coupon.getValidStartTime().getTime()
                    && validTime.getTime() < coupon.getValidEndTime().getTime();
        }
        //相对时间内有效
        else if (validtype == Coupon.VALID_TYPE_RELATIVELY_EFFECTIVE) {
            return System.currentTimeMillis() < DateUtils.addDays(validTime, coupon.getValidDays()).getTime();
        }

        return false;
    }
}