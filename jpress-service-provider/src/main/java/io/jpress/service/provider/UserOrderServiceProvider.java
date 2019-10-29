package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;
import io.jpress.model.UserOrder;
import io.jboot.service.JbootServiceBase;
import org.apache.commons.lang.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

@Bean
public class UserOrderServiceProvider extends JbootServiceBase<UserOrder> implements UserOrderService {

    @Inject
    private UserOrderItemService itemService;

    @Override
    public UserOrder findById(Object id, Object userId) {
        UserOrder userOrder =  findById(id);
        if (userId != null && userOrder != null){
            if (!userId.equals(userOrder.getBuyerId())){
                return  null;
            }
        }
        return userOrder;
    }

    @Override
    public Page<UserOrder> paginate(int page, int pageSize, String title, String ns) {
        Columns columns = Columns.create().likeAppendPercent("title", title).likeAppendPercent("ns", ns);
        Page<UserOrder> userOrderPage = DAO.paginateByColumns(page, pageSize, columns, "id desc");

        for (UserOrder order : userOrderPage.getList()) {
            order.put("items", itemService.findListByOrderId(order.getId()));
        }

        return userOrderPage;
    }

    @Override
    public Page<UserOrder> paginateByUserId(int page, int pageSize, long userid, String title, String ns) {
        Columns columns = Columns.create("buyer_id", userid).likeAppendPercent("title", title).likeAppendPercent("ns", ns);
        Page<UserOrder> userOrderPage = DAO.paginateByColumns(page, pageSize, columns, "id desc");

        for (UserOrder order : userOrderPage.getList()) {
            order.put("items", itemService.findListByOrderId(order.getId()));
        }

        return userOrderPage;
    }

    @Override
    public UserOrder findByPaymentId(Long id) {
        return DAO.findFirstByColumn("payment_id",id);
    }

    @Override
    public int queryTotayCount() {
        return Db.queryInt("select count(*) from user_order where created > ?", DateUtils.truncate(new Date(), Calendar.DATE));
    }

    @Override
    public int queryMonthCount() {
        return Db.queryInt("select count(*) from user_order where created > ?", DateUtils.truncate(new Date(), Calendar.MONTH));
    }

    @Override
    public int queryMonthUserCount() {
        String sql = "select count(distinct buyer_id) from user_order where created > ?";
        return Db.queryInt(sql, DateUtils.truncate(new Date(), Calendar.MONTH));
    }
}