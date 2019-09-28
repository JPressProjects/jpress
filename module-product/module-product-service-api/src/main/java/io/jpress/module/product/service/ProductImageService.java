package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.product.model.ProductImage;

import java.util.List;

public interface ProductImageService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public ProductImage findById(Object id);


    /**
     * find all model
     *
     * @return all <ProductImage
     */
    public List<ProductImage> findAll();


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
    public boolean delete(ProductImage model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(ProductImage model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(ProductImage model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(ProductImage model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<ProductImage> paginate(int page, int pageSize);


}