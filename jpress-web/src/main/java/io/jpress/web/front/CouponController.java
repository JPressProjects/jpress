package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.model.CouponCode;
import io.jpress.model.User;
import io.jpress.model.UserAddress;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;
import java.util.ArrayList;
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
        int action = getParaToInt("action",0);
        List<CouponCode> renderList = new ArrayList<>();
        User loginedUser = getLoginedUser();
        if (action == 0){
            //未过期的，未使用的,正常状态的
            renderList = couponCodeService.findAvailableList(loginedUser.getId());
        }
        if (action == 1){
            //已经过期的
            renderList = couponCodeService.findExpire(loginedUser.getId());
        }
        if (action == 2){
            //已经使用的
            renderList = couponCodeService.findUsed(loginedUser.getId());
        }
        setAttr("couponCodeList",renderList);
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
