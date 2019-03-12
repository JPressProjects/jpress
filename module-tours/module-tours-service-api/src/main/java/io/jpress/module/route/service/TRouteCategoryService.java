package io.jpress.module.route.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.route.model.TRoute;

import java.util.List;

public interface TRouteCategoryService {

    /**
     * find categoryIds by routeId
     *
     * @param routeId
     * @return
     */
    public Long[] findCategoryIdsByRouteId(long routeId);

    /**
     * find category list by routeId
     *
     * @param routeId
     * @return
     */
    public List<ArticleCategory> findListByRouteId(long routeId);

    /**
     * find category list by routeId and type
     *
     * @param routeId
     * @param type
     * @return
     */
    public List<ArticleCategory> findListByRouteId(long routeId, String type);

}