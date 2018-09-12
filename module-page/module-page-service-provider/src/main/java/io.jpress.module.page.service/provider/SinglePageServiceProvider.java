package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.module.page.model.SinglePage;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class SinglePageServiceProvider extends JbootServiceBase<SinglePage> implements SinglePageService {

    @Override
    public Page<SinglePage> paginateByStatus(int page, int pagesize, String status) {
        return DAO.paginateByColumn(page,
                pagesize,
                Column.create("status", status),
                "id desc");
    }

    @Override
    public Page<SinglePage> paginateWithoutTrash(int page, int pagesize) {
        return DAO.paginateByColumn(page,
                pagesize,
                Column.create("status", SinglePage.STATUS_TRASH, Column.LOGIC_NOT_EQUALS),
                "id desc");
    }

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

    @Override
    public SinglePage findFirstBySlug(String slug) {
        return DAO.findFirstByColumn(Column.create("slug", slug));
    }
}