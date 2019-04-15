package io.jpress.module.route.service.provider;

import com.google.common.collect.Lists;
import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.search.ArticleSearcher;
import io.jpress.module.article.service.search.ArticleSearcherFactory;
import io.jpress.module.route.model.TGroup;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TGroupService;
import io.jpress.module.route.service.TRouteService;
import io.jpress.module.route.service.task.RouteViewsCountUpdateTask;
import io.jpress.service.UserService;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Bean
public class TRouteServiceProvider extends JbootServiceBase<TRoute> implements TRouteService {

    @Inject
    private UserService userService;
    @Inject
    private TGroupService groupService;

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
    @Cacheable(name = "routes")
    public Page<TRoute> paginateInNormal(int page, int pagesize) {
        return paginateInNormal(page, pagesize,null);
    }

    @Override
    @Cacheable(name = "routes")
    public Page<TRoute> paginateInNormal(int page, int pagesize, String orderBy) {
        orderBy = StrUtil.obtainDefaultIfBlank(orderBy,DEFAULT_ORDER_BY);
        Columns columns = new Columns();
        columns.add("status", TRoute.STATUS_NORMAL);
        Page<TRoute> dataPage = DAO.paginateByColumns(page, pagesize, columns, orderBy);
        return joinUserPage(dataPage);
    }


    @Override
    @Cacheable(name = "routes")
    public Page<TRoute> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy) {

        StringBuilder sqlBuilder = new StringBuilder("from t_route r ");
        sqlBuilder.append(" left join t_route_category_mapping m on r.id = m.`route_id` ");

        Columns columns = new Columns();
        columns.add("m.category_id", categoryId);
        columns.add("r.status", TRoute.STATUS_NORMAL);

        sqlBuilder.append(SqlUtils.toWhereSql(columns));

        orderBy = StrUtil.obtainDefaultIfBlank(orderBy,DEFAULT_ORDER_BY);
        sqlBuilder.append(" ORDER BY ").append(orderBy);

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
    @CacheEvict(name = "routes",key = "*")
    public void doUpdateExpiredRouteStatus() {

        Columns columns = Columns.create();
        columns.add("status", TRoute.STATUS_NORMAL);
        String now = DateTime.now().toString("yyyy-MM-dd");
        columns.lt("departure_date", now);

        List<TRoute> expiredList = DAO.findListByColumns(columns);
        Db.tx(() -> {
            List<TRoute> list = Lists.newArrayList();
            for (TRoute route : expiredList) {
                if (route.getGroupId() != null && route.getGroupId() > 0) {
                    // update current group status
                    TGroup curGroup = groupService.findById(route.getGroupId());
                    curGroup.setStatus(TGroup.EXPIRED_STATUS);
                    curGroup.update();

                    TGroup nextGroup = groupService.findNextById(route.getGroupId());
                    if (nextGroup != null) {
                        route.setModified(new Date());
                        route.setGroupId(nextGroup.getId());
                        route.setDepartureDate(nextGroup.getLeaveDate());
                        list.add(route);

                        nextGroup.setStatus(TGroup.ENROLLING_STATUS);
                        nextGroup.update();
                        continue;
                    }
                }

                route.setModified(new Date());
                route.setStatus(TRoute.STATUS_TRASH);
                list.add(route);
            }

            Db.batchUpdate(list, list.size());
            return true;
        });
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        TRoute route = findById(id);
        route.setStatus(status);
        return route.update();
    }

    @Override
    @CacheEvict(name = "routes",key = "*")
    public void deleteCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }

    @Override
    public void doIncRouteViewCount(long routeId) {
        RouteViewsCountUpdateTask.recordCount(routeId);
    }

    @Override
    @Cacheable(name = "routes",key = "#(columns.cacheKey)-#(orderBy)-#(count)")
    public List<TRoute> findListByColumns(Columns columns, String orderBy, Integer count) {
        return DAO.findListByColumns(columns, orderBy, count);
    }

    @Override
    @Cacheable(name = "routes",key = "findListByCategoryId:#(categoryId)-#(hasThumbnail)-#(orderBy)-#(count)")
    public List<TRoute> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count) {

        StringBuilder from = new StringBuilder("select * from t_route r ");
        from.append(" left join t_route_category_mapping m on r.id = m.`route_id` ");
        from.append(" where m.category_id = ? ");
        from.append(" and r.status = ? ");


        if (hasThumbnail != null) {
            if (hasThumbnail == true) {
                from.append(" and r.thumbnail is not null");
            } else {
                from.append(" and r.thumbnail is null");
            }
        }

        from.append(" group by r.id ");

        if (orderBy != null) {
            from.append(" order by " + orderBy);
        }

        if (count != null) {
            from.append(" limit " + count);
        }

        return DAO.find(from.toString(), categoryId, TRoute.STATUS_NORMAL);
    }

    @Override
    public Page<TRoute> search(String keyword, String code, Long categoryId, int page, int pagesize) {
        return _paginateByBaseColumns(page
                ,pagesize
                ,keyword
                ,code
                ,categoryId
                ,Columns.create().ne("r.status", TRoute.STATUS_TRASH));
    }

    @Override
    public List<TRoute> searchByTitleInWechat(String keyword, Integer count) {

        if (StrUtil.isBlank(keyword)) {
            return null;
        }
        keyword = "%" + keyword + "%";

        if (count == null) {
            count = 8;
        }

        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("SELECT * FROM `t_route` AS t1");
        sqlBuilder.append(" JOIN (SELECT ROUND(RAND() * (SELECT MAX(id) FROM `t_route`)) AS id ) AS t2");
        sqlBuilder.append(" WHERE t1.id >= t2.id AND t1.status = ? AND t1.title LIKE ?");
        sqlBuilder.append(" ORDER BY t1.id ASC LIMIT ?");

        String sql = "SELECT * FROM `t_route` WHERE status = ? AND title LIKE ? ORDER BY id DESC LIMIT ?";

        return DAO.find(sql, TRoute.STATUS_NORMAL, keyword, count);
    }

}