package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.service.JbootServiceJoiner;
import io.jpress.model.MemberGroup;

import java.util.List;

public interface MemberGroupService extends JbootServiceJoiner {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MemberGroup findById(Object id);


    /**
     * find all model
     *
     * @return all <MemberGroup
     */
    public List<MemberGroup> findAll();


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
    public boolean delete(MemberGroup model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(MemberGroup model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(MemberGroup model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MemberGroup model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<MemberGroup> paginate(int page, int pageSize);


    public List<MemberGroup> findUcenterList();

    public List<MemberGroup> findNormalList();


}