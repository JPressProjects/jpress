package io.jpress.module.route.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.route.model.TRoute;

import java.util.List;

public interface TRouteService  {

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
     * change the route status
     *
     * @param id
     * @param status
     * @return
     */
    public boolean doChangeStatus(long id, String status);

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
}