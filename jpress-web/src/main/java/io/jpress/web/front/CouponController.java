package io.jpress.web.front;

import com.jfinal.aop.Inject;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.model.CouponCode;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author haicuan139 (haicuan139@163.com)
 * @Date: 2019/12/24
 */
@RequestMapping(value = "/ucenter/coupon", viewPath = "/WEB-INF/views/ucenter/coupon")
public class CouponController extends UcenterControllerBase {

    @Inject
    private CouponService couponService;

    @Inject
    private CouponCodeService couponCodeService;

    /**
     * 用户优惠券列表
     */
    public void index() {
        List<CouponCode> renderList = couponCodeService.findAvailableByUserId(getLoginedUser().getId());
        setAttr("couponCodeList", renderList);
        render("coupon_code_list.html");
    }


    /**
     * 选择优惠券的弹出层
     */
    public void layer() {
        String price = getPara("price");
        List<CouponCode> couponCodes = couponCodeService.findAvailableByUserId(getLoginedUser().getId(), new BigDecimal(price));
        setAttr("couponCodeList", couponCodes);
        render("coupon_code_layer.html");
    }


}
