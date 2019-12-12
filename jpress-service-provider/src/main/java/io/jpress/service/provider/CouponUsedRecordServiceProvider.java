package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.CouponUsedRecordService;
import io.jpress.model.CouponUsedRecord;
import io.jboot.service.JbootServiceBase;

@Bean
public class CouponUsedRecordServiceProvider extends JbootServiceBase<CouponUsedRecord> implements CouponUsedRecordService {

    @Override
    public Page<CouponUsedRecord> paginate(int page, int pageSize, Columns columns) {
        return paginateByColumns(page, pageSize, columns, "id desc");
    }

    @Override
    public long queryCountByCouponId(long couponId) {
        return findCountByColumns(Columns.create("coupon_id",couponId));
    }
}