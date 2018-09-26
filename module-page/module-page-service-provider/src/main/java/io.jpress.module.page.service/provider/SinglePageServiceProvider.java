package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.StringUtils;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.module.page.model.SinglePage;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;
import java.util.List;

@Bean
@Singleton
public class SinglePageServiceProvider extends JbootServiceBase<SinglePage> implements SinglePageService {

    @Override
    public Page<SinglePage> _paginateByStatus(int page, int pagesize, String title, String status) {

        Columns columns = Columns.create("status", status);
        if (StringUtils.isNotBlank(title)) {
            columns.like("title", "%" + title + "%");
        }

        return DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");
    }

    @Override
    public Page<SinglePage> _paginateWithoutTrash(int page, int pagesize, String title) {

        Columns columns = Columns.create(Column.create("status", SinglePage.STATUS_TRASH, Column.LOGIC_NOT_EQUALS));
        if (StringUtils.isNotBlank(title)) {
            columns.like("title", "%" + title + "%");
        }

        return DAO.paginateByColumns(page,
                pagesize,
                columns,
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

    @Override
    public List<SinglePage> findListByFlag(String flag) {
        return DAO.findListByColumn(Column.create("flag", flag));
    }
}