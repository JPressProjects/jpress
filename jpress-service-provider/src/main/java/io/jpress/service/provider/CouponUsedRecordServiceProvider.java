package io.jpress.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.CouponUsedRecord;
import io.jpress.service.CouponCodeService;
import io.jpress.service.CouponUsedRecordService;
import io.jpress.service.UserOrderService;

@Bean
public class CouponUsedRecordServiceProvider extends JbootServiceBase<CouponUsedRecord> implements CouponUsedRecordService {

    @Inject
    private CouponCodeService couponCodeService;

    @Inject
    private UserOrderService orderService;

    @Override
    public Page<CouponUsedRecord> paginate(int page, int pageSize, Columns columns) {
        return doJoin(paginateByColumns(page, pageSize, columns, "id desc"));
    }

    @Override
    public long queryCountByCouponId(long couponId) {
        return findCountByColumns(Columns.create("coupon_id", couponId));
    }

    private Page<CouponUsedRecord> doJoin(Page<CouponUsedRecord> page) {
        couponCodeService.join(page, "code_id");
        orderService.join(page, "used_order_id");
        return page;
    }
}