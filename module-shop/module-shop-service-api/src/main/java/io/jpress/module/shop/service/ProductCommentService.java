package io.jpress.module.shop.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.shop.model.ProductComment;

import java.util.List;

public interface ProductCommentService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductComment findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductComment
     */
    public List<ProductComment> findAll();


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
    public boolean delete(ProductComment model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(ProductComment model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ProductComment model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductComment model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductComment> paginate(int page, int pageSize);


}