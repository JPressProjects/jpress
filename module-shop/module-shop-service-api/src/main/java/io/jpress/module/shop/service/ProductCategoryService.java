package io.jpress.module.shop.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.shop.model.ProductCategory;

import java.util.List;

public interface ProductCategoryService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductCategory findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductCategory
     */
    public List<ProductCategory> findAll();


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
    public boolean delete(ProductCategory model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(ProductCategory model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ProductCategory model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductCategory model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductCategory> paginate(int page, int pageSize);


}