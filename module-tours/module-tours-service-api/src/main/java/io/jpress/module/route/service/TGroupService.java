package io.jpress.module.route.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.route.model.TGroup;
import io.jpress.module.route.model.TRoute;

import java.util.List;

public interface TGroupService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public TGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <TGroup
     */
    public List<TGroup> findAll();


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
    public boolean delete(TGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(TGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(TGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(TGroup model);

    /**
     * update route's group
     *
     * @author Eric
     * @date  2019-03-10 11:06
     * @param route         线路
     * @param groups        团期信息
     * @param calendarStr    日历信息
     * @return void
     * @desc    
     */
    public void doUpdateGroups(TRoute route, Integer[] groups, String calendarStr);

    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<TGroup> paginate(int page, int pageSize);


}