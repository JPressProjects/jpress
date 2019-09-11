package io.jpress.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.MemberUser;

import java.util.List;

public interface MemberUserService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public MemberUser findById(Object id);


    /**
     * find all model
     *
     * @return all <MemberUser
     */
    public List<MemberUser> findAll();


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
    public boolean delete(MemberUser model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(MemberUser model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(MemberUser model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(MemberUser model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<MemberUser> paginate(int page, int pageSize);


}