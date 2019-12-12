/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Coupon;
import io.jpress.model.CouponCode;
import io.jpress.model.CouponUsedRecord;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponService;
import io.jpress.service.CouponUsedRecordService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/finance/coupon", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _CouponController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_CouponController.class);

    @Inject
    private CouponService couponService;

    @Inject
    private CouponUsedRecordService couponUsedRecordService;

    @Inject
    private CouponCodeService couponCodeService;

    @AdminMenu(text = "优惠券", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 8)
    public void index() {
        Page<Coupon> page = couponService.paginate(getPagePara(), 10);
        setAttr("page", page);
        render("finance/coupon.html");
    }


    public void edit() {
        Coupon coupon = couponService.findById(getPara());
        setAttr("coupon", coupon);
        render("finance/coupon_edit.html");
    }

    @EmptyValidate({
            @Form(name = "coupon.amount", message = "优惠券金额不能为空"),
            @Form(name = "coupon.quota", message = "优惠券发行数量不能为空"),
    })
    public void doSave() {
        Coupon coupon = getModel(Coupon.class);
        couponService.saveOrUpdate(coupon);
        renderOkJson();
    }

    public void doDel() {
        couponService.deleteById(getIdPara());
        renderOkJson();
    }

    @EmptyValidate({
            @Form(name = "ids", message = "删除数据不能为空"),
    })
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        couponService.batchDeleteByIds(idsSet.toArray());
        renderOkJson();
    }

    public void takes() {
        Coupon coupon = couponService.findById(getPara());
        setAttr("coupon", coupon);

        Page<CouponCode> page = couponCodeService.paginateByCouponId(getPagePara(), 10, getParaToLong());
        setAttr("page", page);

        render("finance/coupon_takes.html");
    }

    public void takeEdit() {
        Coupon coupon = couponService.findById(getPara("cid"));
        setAttr("coupon", coupon);

        setAttr("code",couponCodeService.findById(getPara()));
        render("finance/coupon_take_edit.html");
    }


    public void doCodeSave() {
        Coupon coupon = couponService.findById(getPara("couponId"));
        if (coupon == null) {
            renderFailJson("该优惠券不存在或已经被删除。");
            return;
        }


        CouponCode code = new CouponCode();
        code.setId(getParaToLong("codeId"));
        code.setCouponId(coupon.getId());
        code.setTitle(coupon.getTitle());
        code.setCode(StrUtil.uuid());
        code.setUserId(getLong("userId"));
        code.setStatus(getParaToInt("status"));
        code.setValidTime(new Date());
        code.setCreated(new Date());

        couponCodeService.saveOrUpdate(code);
        couponService.doSyncTakeCount(coupon.getId());

        renderOkJson();
    }

    public void doCodeDel() {
        CouponCode code = couponCodeService.findByCode(getPara());
        if (code != null){
            couponCodeService.deleteById(getPara());
            couponService.doSyncTakeCount(code.getCouponId());
        }


        renderOkJson();
    }

    public void doCodeDelByIds() {
        Set<String> ids = getParaSet("ids");
        for (String id : ids) {
            CouponCode code = couponCodeService.findByCode(id);
            if (code != null){
                couponCodeService.deleteById(getPara());
                couponService.doSyncTakeCount(code.getCouponId());
            }
        }
        renderOkJson();
    }


    public void useds() {

        Columns columns = Columns.create();
        columns.add("code_id", getPara("codeid"));
        columns.add("code_user_id", getPara("cuid"));
        columns.add("coupon_id", getPara("coid"));

        Page<CouponUsedRecord> page = couponUsedRecordService.paginate(getPagePara(), 10, columns);
        setAttr("page", page);
        render("finance/coupon_useds.html");
    }


}
