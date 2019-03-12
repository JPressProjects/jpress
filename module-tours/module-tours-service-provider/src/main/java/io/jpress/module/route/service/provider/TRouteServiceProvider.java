package io.jpress.module.route.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteService;
import io.jpress.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Bean
public class TRouteServiceProvider extends JbootServiceBase<TRoute> implements TRouteService {

    @Inject
    private UserService userService;

    private static final String DEFAULT_ORDER_BY = "order_list desc,id desc";

    @Override
    public boolean deleteByIds(Object... ids) {
        for (Object id : ids) {
            deleteById(id);
        }
        return true;
    }

    @Override
    public boolean deleteById(Object id) {
        Jboot.getCache().remove("routeCategory", "categoryList:" + id);
        Jboot.getCache().remove("routeCategory", "categoryIds:" + id);
        return super.deleteById(id);
    }

    @Override
    public Page<TRoute> _paginateByStatus(int page, int pagesize, String title, String code, Long categoryId, String status) {

        return _paginateByBaseColumns(page
                ,pagesize
                ,title
                ,code
                ,categoryId
                ,Columns.create("r.status", status));

    }

    @Override
    public Page<TRoute> _paginateWithoutTrash(int page, int pagesize, String title, String code, Long categoryId) {

        return _paginateByBaseColumns(page
                ,pagesize
                ,title
                ,code
                ,categoryId
                ,Columns.create().ne("r.status", TRoute.STATUS_TRASH));
    }

    public Page<TRoute> _paginateByBaseColumns(int page, int pagesize, String title, String code, Long categoryId, Columns baseColumns) {


        StringBuilder sqlBuilder = new StringBuilder("from t_route r ");
        if (categoryId != null) {
            sqlBuilder.append(" left join t_route_category_mapping m on r.id = m.`route_id` ");
        }

        Columns columns = baseColumns;
        columns.add("r.code", code);
        columns.add("m.category_id", categoryId);
        columns.likeAppendPercent("r.title", title);

        sqlBuilder.append(SqlUtils.toWhereSql(columns));
        sqlBuilder.append(" order by ").append(DEFAULT_ORDER_BY);

        Page<TRoute> dataPage = DAO.paginate(page, pagesize, "select * ", sqlBuilder.toString(), columns.getValueArray());
        return joinUserPage(dataPage);
    }

    @Override
    public Long findMaxRouteCode() {
        Long code = Db.queryLong("select max(code) from t_route");
        if (code == null) {
            return 80000001L;
        }
        return code + 1L;
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from t_route where status = ?", status);
    }

    @Override
    public TRoute findFirstBySlug(String slug) {
        return DAO.findFirstByColumn(Column.create("slug", slug));
    }

    private Page<TRoute> joinUserPage(Page<TRoute> page) {
        userService.join(page, "user_id");
        return page;
    }

    @Override
    @CacheEvict(name = "routes",key = "*")
    public void doUpdateCategorys(long routeId, Long[] categoryIds) {

        Db.tx(() -> {
            Db.update("delete from t_route_category_mapping where route_id = ?", routeId);

            if (categoryIds != null && categoryIds.length > 0) {
                List<Record> records = new ArrayList<>();
                for (long categoryId : categoryIds) {
                    Record record = new Record();
                    record.set("route_id", routeId);
                    record.set("category_id", categoryId);
                    records.add(record);
                }
                Db.batchSave("t_route_category_mapping", records, records.size());
            }

            return true;
        });
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        TRoute route = findById(id);
        route.setStatus(status);
        return route.update();
    }

}