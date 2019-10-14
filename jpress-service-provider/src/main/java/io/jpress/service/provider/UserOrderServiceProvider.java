package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.UserOrderItemService;
import io.jpress.service.UserOrderService;
import io.jpress.model.UserOrder;
import io.jboot.service.JbootServiceBase;

@Bean
public class UserOrderServiceProvider extends JbootServiceBase<UserOrder> implements UserOrderService {

    @Inject
    private UserOrderItemService itemService;

    @Override
    public Page<UserOrder> paginate(int page, int pageSize, String title, String ns) {
        Columns columns = Columns.create().likeAppendPercent("title", title).likeAppendPercent("ns", ns);
        Page<UserOrder> userOrderPage = DAO.paginateByColumns(page, pageSize, columns, "id desc");

        for (UserOrder order : userOrderPage.getList()){
            order.put("items",itemService.findListByOrderId(order.getId()));
        }

        return userOrderPage;
    }
}