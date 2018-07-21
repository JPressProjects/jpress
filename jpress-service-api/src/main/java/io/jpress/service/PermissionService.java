package io.jpress.service;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.model.Permission;

import java.util.List;

public interface PermissionService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Permission findById(Object id);


    /**
     * find all model
     *
     * @return all <Permission
     */
    public List<Permission> findAll();


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
    public boolean delete(Permission model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public boolean save(Permission model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public boolean saveOrUpdate(Permission model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Permission model);


    public void join(Page<? extends Model> page, String joinOnField);

    public void join(Page<? extends Model> page, String joinOnField, String[] attrs);

    public void join(Page<? extends Model> page, String joinOnField, String joinName);

    public void join(Page<? extends Model> page, String joinOnField, String joinName, String[] attrs);

    public void join(List<? extends Model> models, String joinOnField);

    public void join(List<? extends Model> models, String joinOnField, String[] attrs);

    public void join(List<? extends Model> models, String joinOnField, String joinName);

    public void join(List<? extends Model> models, String joinOnField, String joinName, String[] attrs);

    public void join(Model model, String joinOnField);

    public void join(Model model, String joinOnField, String[] attrs);

    public void join(Model model, String joinOnField, String joinName);

    public void join(Model model, String joinOnField, String joinName, String[] attrs);

    public void keep(Model model, String... attrs);

    public void keep(List<? extends Model> models, String... attrs);


    public int sync(List<Permission> permissions);

    public boolean hasPermission(long userId, String actionKey);

    public Page<Permission> page(int size, int count);

    public Page<Permission> page(int size, int count, int type);

}