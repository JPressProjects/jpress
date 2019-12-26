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
import io.jpress.model.CouponUsedRecord;
import io.jpress.model.Member;
import io.jpress.service.*;
import org.apache.commons.lang.time.DateUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

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
        return findCountByColumns(Columns.create("coupon_id",couponId));
    }

    /**&
     * 支付时，找出可用的优惠券
     * @param couponCode
     * @param currentUserId
     * @param orderTotalAmount 总价格
     * @return
     */
    private boolean checkCouponCode(CouponCode couponCode,long currentUserId,BigDecimal orderTotalAmount){

        //该优惠码不存在或已失效
        if (couponCode == null || !couponCode.isNormal()) {
            return false;
        }
        Coupon coupon = couponService.findById(couponCode.getCouponId());
        // 该优惠券不可用
        if (coupon == null || !coupon.isNormal()) {
            return false;
        }
        couponCode.put("coupon",coupon);
        //是否是只有优惠券拥有者可用
        Boolean withOwner = coupon.getWithOwner();
        if (withOwner) {
            if (!couponCode.getUserId().equals(currentUserId)){
                return false;
            }
        }
        //是不是会员可用
        if (coupon.getWithMember()){
            if (!memberService.isMember(currentUserId)){
                return false;
            }
        }
        if (checkCouponCodeDiscount(orderTotalAmount, coupon)) {
            return false;
        }

        if (!isCouponCodeUnExpire(coupon,couponCode)){
            return false;
        }


        return true;
    }
    /**
     * 检查使用的优惠券价格是否有效；满减券生效
     * @return
     */
    private boolean checkCouponCodeDiscount(BigDecimal orderTotalAmount, Coupon coupon) {
        //如果是满减券，检查价格是否有效
        int couponType = coupon.getType();
        if (couponType == Coupon.TYPE_FULL_DISCOUNT){
            BigDecimal withAmount = coupon.getWithAmount();
            if (withAmount == null || withAmount.compareTo(BigDecimal.ZERO) < 0) {
                return true;
            }

            if (orderTotalAmount.compareTo(coupon.getWithAmount()) < 0) {
                return true;
            }

        }
        return false;
    }



    /**
     * 检查优惠券有效期是否正常
     * @param coupon
     * @param couponCode
     * @return
     */
    private boolean isCouponCodeUnExpire(Coupon coupon, CouponCode couponCode){
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
    public List<CouponCode> findAvailableByUserId(Long userid, BigDecimal orderTotalAmount){
        List<CouponCode> couponCodes = findListByColumns(Columns.create().add("user_id", userid),"created desc");
        List<CouponCode> removeList = new ArrayList<>();
        for(CouponCode code:couponCodes){
            if (!checkCouponCode(code,userid,orderTotalAmount)){
                removeList.add(code);
            }
        }
        couponCodes.removeAll(removeList);
        return couponCodes;
    }

    /**
     * 获取有效的优惠券码列表,个人中心中使用
     * @param userid
     * @return
     */
    @Override
    public List<CouponCode> findAvailableList(long userid){
        List<CouponCode> couponCodes = findListByColumns(Columns.create().add("user_id", userid).add("status",CouponCode.STATUS_NORMAL),"created desc");
        List<CouponCode> finalList = new ArrayList<>();
        for(CouponCode couponCode:couponCodes){
            //没过期，normal状态的
            Coupon coupon = couponService.findById(couponCode.getCouponId());
            if (coupon!=null){
                boolean couponCodeUnExpire = isCouponCodeUnExpire(coupon, couponCode);
                if (couponCodeUnExpire && coupon.isNormal()){
                    couponCode.put("coupon",coupon);
                    finalList.add(couponCode);
                }
            }
        }
        return finalList;
    }

    /**
     * 获取过期的优惠券码，个人中心中使用
     */
    @Override
    public List<CouponCode> findExpire(long userid){

        List<CouponCode> couponCodes = findListByColumns(Columns.create().add("user_id", userid).add("status",CouponCode.STATUS_NORMAL),"created desc");
        List<CouponCode> finalList = new ArrayList<>();
        for(CouponCode couponCode:couponCodes){
            //没过期，normal状态的
            Coupon coupon = couponService.findById(couponCode.getCouponId());
            if (coupon!=null){
                boolean couponCodeUnExpire = isCouponCodeUnExpire(coupon, couponCode);
                if (!couponCodeUnExpire && coupon.isNormal()){
                    couponCode.put("coupon",coupon);
                    finalList.add(couponCode);
                }
            }
        }
        return finalList;
    }

    /**
     * 获取不可用的优惠券
     * @param userid
     * @return
     */
    @Override
    public List<CouponCode> findUsed(long userid){
        List<CouponCode> couponCodes = findListByColumns(Columns.create().add("user_id", userid).add("status", CouponCode.STATUS_USED),"created desc");

        List<CouponCode> finalList = new ArrayList<>();
        for(CouponCode couponCode:couponCodes){
            //没过期，normal状态的
            Coupon coupon = couponService.findById(couponCode.getCouponId());
            if (coupon!=null){
                if (coupon.isNormal()){
                    couponCode.put("coupon",coupon);
                    finalList.add(couponCode);
                }
            }
        }
        return finalList;
    }

}
