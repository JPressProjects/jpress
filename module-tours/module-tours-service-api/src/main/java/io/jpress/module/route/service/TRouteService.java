package io.jpress.module.route.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.module.route.model.TRoute;

import java.util.List;

public interface TRouteService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public TRoute findById(Object id);


    /**
     * find all model
     *
     * @return all <TRoute
     */
    public List<TRoute> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(TRoute model);

    /**
     * 删除多个id
     *
     * @param ids
     * @return
     */
    public boolean deleteByIds(Object... ids);

    /**
     * 删除单个线路的缓存
     *
     * @param id
     * @return
     */
    public void deleteCacheById(Object id);

    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(TRoute model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(TRoute model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(TRoute model);

    /**
     * generate route code
     *
     * @return
     */
    public Long findMaxRouteCode();

    public void doUpdateCategorys(long routeId, Long[] categoryIds);

    /**
     * update the route view count
     *
     * @param routeId
     * @return
     */
    public void doIncRouteViewCount(long routeId);

    /**
     * change the route status
     *
     * @param id
     * @param status
     * @return
     */
    public boolean doChangeStatus(long id, String status);

    /**
     * update all expired routes status
     *
     * @return
     */
    public void doUpdateExpiredRouteStatus();

    public int findCountByStatus(String status);

    public TRoute findFirstBySlug(String slug);

    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<TRoute> paginate(int page, int pageSize);

    public Page<TRoute> _paginateByStatus(int page, int pagesize, String title, String code, Long categoryId, String status);

    public Page<TRoute> _paginateWithoutTrash(int page, int pagesize, String title, String code, Long categoryId);

    /**
     * find routes by conditions
     *
     * @param columns
     * @param orderBy
     * @param count
     * @return java.util.List
     */
    public List<TRoute> findListByColumns(Columns columns, String orderBy, Integer count);

    /**
     * find routes by categoryId
     *
     * @param categoryId
     * @param hasThumbnail
     * @param orderBy
     * @param count
     * @return java.util.List
     */
    public List<TRoute> findListByCategoryId(long categoryId, Boolean hasThumbnail, String orderBy, Integer count);
}