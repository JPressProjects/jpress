package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Coupon;
import io.jpress.model.CouponCode;
import io.jpress.model.Member;
import io.jpress.service.*;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Bean
public class CouponCodeServiceProvider extends JbootServiceBase<CouponCode> implements CouponCodeService {

    @Inject
    private CouponService couponService;


    @Inject
    private UserService userService;

    @Inject
    private MemberService memberService;

    @Inject
    private CouponUsedRecordService couponUsedRecordService;

    @Override
    public Page<CouponCode> paginateByCouponId(int page, int pageSize, Long couponId) {
        return userService.join(paginateByColumns(page, pageSize, Columns.create("coupon_id", couponId), "id desc"), "user_id");
    }

    @Override
    public CouponCode findByCode(String code) {
        return DAO.findFirstByColumn(Column.create("code", code));
    }

    @Override
    public Ret valid(CouponCode couponCode, BigDecimal orderTotalAmount, long usedUserId) {

        // 该优惠码被标识为：不可用
        if (couponCode == null || !couponCode.isNormal()) {
            return Ret.fail().set("message", "该优惠码不存在或已失效");
        }

        Coupon coupon = couponService.findById(couponCode.getCouponId());

        couponCode.put("coupon",coupon);//绑定一个优惠券对象，否则选择优惠券无法展示前端无法展示

        // 该优惠券不可用
        if (coupon == null || !coupon.isNormal()) {
            return Ret.fail().set("message", "该优惠码不存在或已失效");
        }

        //是否是只有优惠券拥有者可用
        Boolean withOwner = coupon.getWithOwner();
        if (withOwner != null && withOwner && !couponCode.getUserId().equals(usedUserId)) {
            return Ret.fail().set("message", "该优惠码只能自己使用");
        }

        //是不是会员可用
        Boolean withMember = coupon.getWithMember();
        if (withMember != null && withMember) {
            List<Member> members = memberService.findListByUserId(usedUserId);
            if (members == null || members.isEmpty()) {
                return Ret.fail().set("message", "该优惠码只能会员使用");
            }

            boolean valid = false;
            for (Member member : members) {
                if (member.isNormal()) {
                    valid = true;
                    break;
                }
            }

            if (!valid) {
                return Ret.fail().set("message", "该优惠码只能会员使用");
            }
        }

        Integer couponType = coupon.getType();
        //满减券
        if (couponType != null && couponType == Coupon.TYPE_FULL_DISCOUNT) {
            BigDecimal withAmount = coupon.getWithAmount();
            if (withAmount == null || withAmount.compareTo(BigDecimal.ZERO) < 0) {
                return Ret.fail().set("message", "该优惠券配置错误，请联系管理员");
            }
            if (orderTotalAmount.compareTo(coupon.getWithAmount()) < 0) {
                return Ret.fail().set("message", "订单总价格必须购买超过 " + new DecimalFormat("0.00").format(coupon.getWithAmount()) + " 元才能使用该优惠码。");
            }
        }


        Date validTime = couponCode.getValidTime();
        int validtype = coupon.getValidType();

        //绝对时间内有效
        if (validtype == Coupon.VALID_TYPE_ABSOLUTELY_EFFECTIVE) {
            boolean timeValide = validTime.getTime() > coupon.getValidStartTime().getTime()
                    && validTime.getTime() < coupon.getValidEndTime().getTime();

            if (!timeValide) {
                return Ret.fail().set("message", "该优惠码已经过期");
            }
        }
        //相对时间内有效
        else if (validtype == Coupon.VALID_TYPE_RELATIVELY_EFFECTIVE) {
            boolean timeValide = System.currentTimeMillis() < DateUtils.addDays(validTime, coupon.getValidDays()).getTime();
            if (!timeValide) {
                return Ret.fail().set("message", "该优惠码已经过期");
            }
        }

        return Ret.ok();
    }

    @Override
    public long queryCountByCouponId(long couponId) {
        return findCountByColumns(Columns.create("coupon_id", couponId));
    }


    /**
     * 检查优惠券有效期是否正常
     *
     * @param coupon
     * @param couponCode
     * @return
     */
    private boolean isCouponCodeUnExpire(Coupon coupon, CouponCode couponCode) {
        //检查优惠券时效
        Date validTime = couponCode.getValidTime();
        int validtype = coupon.getValidType();

        //绝对时间内有效
        if (validtype == Coupon.VALID_TYPE_ABSOLUTELY_EFFECTIVE) {
            boolean timeValide = validTime.getTime() > coupon.getValidStartTime().getTime()
                    && validTime.getTime() < coupon.getValidEndTime().getTime();

            if (!timeValide) {
                return false;
            }
        }
        //相对时间内有效
        else if (validtype == Coupon.VALID_TYPE_RELATIVELY_EFFECTIVE) {
            boolean timeValide = System.currentTimeMillis() < DateUtils.addDays(validTime, coupon.getValidDays()).getTime();
            if (!timeValide) {
                return false;
            }
        }
        return true;
    }


    /**
     * 支付时可选择；获取用户可用的券码
     */
    @Override
    public List<CouponCode> findAvailableByUserId(long userid, BigDecimal orderTotalAmount) {
        List<CouponCode> couponCodes = findListByColumns(Columns.create().add("user_id", userid),"id desc");
        if (couponCodes == null || couponCodes.isEmpty()) {
            return null;
        }
        return couponCodes.stream()
                .filter(couponCode -> valid(couponCode, orderTotalAmount, userid).isOk())
                .collect(Collectors.toList());
    }

    /**
     * 获取有效的优惠券码列表,个人中心中使用
     *
     * @param userid
     * @return
     */
    @Override
    public List<CouponCode> findAvailableByUserId(long userid) {
        List<CouponCode> couponCodes = findListByColumns(Columns.create().add("user_id", userid).add("status", CouponCode.STATUS_NORMAL),"id desc");
        List<CouponCode> finalList = new ArrayList<>();
        for (CouponCode couponCode : couponCodes) {
            //没过期，normal状态的
            Coupon coupon = couponService.findById(couponCode.getCouponId());
            if (coupon != null) {
                if (isCouponCodeUnExpire(coupon, couponCode) && coupon.isNormal()) {
                    couponCode.put("coupon", coupon);
                    finalList.add(couponCode);
                }
            }
        }
        return finalList;
    }


}
