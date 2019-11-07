package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.service.MemberPriceService;
import io.jpress.model.MemberPrice;
import io.jboot.service.JbootServiceBase;

@Bean
public class MemberPriceServiceProvider extends JbootServiceBase<MemberPrice> implements MemberPriceService {

    @Override
    public MemberPrice findByPorductAndGroup(String productTableName, Object productId, Object groupId) {
        return findFirstByColumns(Columns.create("product_table",productTableName).eq("product_id",productId).eq("group_id",groupId));
    }
}