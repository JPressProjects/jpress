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
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.PaymentRecord;
import io.jpress.service.PaymentRecordService;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserService;
import io.jpress.web.base.AdminControllerBase;
import io.jpress.web.commons.express.ExpressQuerierFactory;

import java.util.Date;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/finance", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FinanceController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_FinanceController.class);

    @Inject
    private PaymentRecordService paymentService;

    @Inject
    private UserOrderItemService orderItemService;

    @Inject
    private UserService userService;


    @AdminMenu(text = "支付记录", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 3)
    public void paylist() {
        Columns columns = Columns.create();
        columns.likeAppendPercent("trx_no", getPara("ns"));
        columns.likeAppendPercent("product_title", getPara("pt"));
        columns.eq("status", getPara("status"));

        Page<PaymentRecord> page = paymentService.paginate(getPagePara(), 20, columns);
        setAttr("page", page);

        long successCount = paymentService.findCountByColumns(Columns.create("status", PaymentRecord.STATUS_PAY_SUCCESS));
        long prepayCount = paymentService.findCountByColumns(Columns.create("status", PaymentRecord.STATUS_PAY_PRE));
        long failCount = paymentService.findCountByColumns(Columns.create("status", PaymentRecord.STATUS_PAY_FAILURE));

        setAttr("successCount", successCount);
        setAttr("prepayCount", prepayCount);
        setAttr("failCount", failCount);
        setAttr("totalCount", successCount + prepayCount + failCount);

        render("finance/paylist.html");
    }

    public void payUpdate() {
        PaymentRecord payment = paymentService.findById(getPara());
        render404If(payment == null);

        setAttr("payment", payment);
        render("finance/layer_payupdate.html");
    }

    public void doPayUpdate() {
        PaymentRecord payment = getModel(PaymentRecord.class, "payment");
        payment.keep("id", "pay_status", "pay_success_amount", "pay_success_time", "pay_success_proof", "pay_success_remarks");

        PaymentRecord dbPayment = paymentService.findById(payment.getId());
        if (dbPayment.getPayAmount().compareTo(payment.getPaySuccessAmount()) != 0) {
            renderFailJson("入账失败，入账金额和应付款金额不一致。");
            return;
        }

        if (dbPayment.isPaySuccess()) {
            renderFailJson("该支付已经支付成功。");
            return;
        }

        payment.setStatus(PaymentRecord.STATUS_PAY_SUCCESS);
        payment.setPayCompleteTime(new Date());

        if (paymentService.update(payment)) {
            paymentService.notifySuccess(payment.getId());
        }

        renderOkJson();
    }

    /**
     * 支付记录详情
     */
    public void payDetail() {
        PaymentRecord payment = paymentService.findById(getPara());
        render404If(payment == null);

        setAttr("payment", payment);
        render("finance/layer_paydetail.html");
    }


    @AdminMenu(text = "基础设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 9)
    public void setting() {
        setAttr("querierNames", ExpressQuerierFactory.getQuerierNames());
        render("finance/setting_base.html");
    }

    @AdminMenu(text = "收款设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 19)
    public void setting_pay() {
        setAttr("querierNames", ExpressQuerierFactory.getQuerierNames());
        render("finance/setting_pay.html");
    }

    @AdminMenu(text = "通知设置", groupId = JPressConsts.SYSTEM_MENU_ORDER, order = 29)
    public void setting_notify() {
        setAttr("querierNames", ExpressQuerierFactory.getQuerierNames());
        render("finance/setting_notify.html");
    }



}
