package io.jpress.module.route.service.provider;

import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TRouteCategoryService;
import io.jpress.module.route.service.TRouteService;
import io.jpress.service.UserService;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Bean
public class TRouteCategoryServiceProvider extends JbootServiceBase<TRoute> implements TRouteCategoryService {

    @Inject
    ArticleCategoryService categoryService;

    /**
     * find category by routeId
     *
     * @param routeId
     * @return
     */
    @Override
    @Cacheable(name = "routeCategory", key = "categoryIds:#(routeId)")
    public Long[] findCategoryIdsByRouteId(long routeId) {
        List<Record> records = Db.find("select * from t_route_category_mapping where route_id = ?", routeId);
        if (records == null || records.isEmpty()) {
            return null;
        }

        return ArrayUtils.toObject(records.stream().mapToLong(record -> record.get("category_id")).toArray());
    }

    @Override
    @Cacheable(name = "routeCategory", key = "categoryList:#(routeId)")
    public List<ArticleCategory> findListByRouteId(long routeId) {
        List<Record> mappings = Db.find("select * from t_route_category_mapping where route_id = ?", routeId);
        if (mappings == null || mappings.isEmpty()) {
            return null;
        }

        return mappings
            .stream()
            .map(record -> categoryService.findById(record.get("category_id")))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Override
    public List<ArticleCategory> findListByRouteId(long routeId, String type) {
        List<ArticleCategory> categoryList = findListByRouteId(routeId);
        if (categoryList == null || categoryList.isEmpty()) {
            return null;
        }
        return categoryList
                .stream()
                .filter(category -> type.equals(category.getType()))
                .collect(Collectors.toList());
    }
}