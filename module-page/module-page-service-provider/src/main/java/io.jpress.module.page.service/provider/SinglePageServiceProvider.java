package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.aop.annotation.Bean;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.module.page.model.SinglePage;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class SinglePageServiceProvider extends JbootServiceBase<SinglePage> implements SinglePageService {

    @Override
    public boolean doChangeStatus(long id, String status) {
        SinglePage page = findById(id);
        page.setStatus(status);
        return page.update();
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from single_page where status = ?", status);
    }
}