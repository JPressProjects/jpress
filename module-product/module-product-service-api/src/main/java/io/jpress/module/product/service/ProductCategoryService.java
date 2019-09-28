package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.module.product.model.ProductCategory;

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


    public Page<ProductCategory> paginateByType(int page, int pagesize, String type);


    public List<ProductCategory> findListByProductId(long productId);

    public List<ProductCategory> findCategoryListByProductId(long productId);
    public List<ProductCategory> findTagListByProductId(long productId);
    public List<ProductCategory> findListByProductId(long productId, String type);

    public List<ProductCategory> findListByType(String type);

    public List<ProductCategory> findOrCreateByTagString(String[] tags);


    public ProductCategory findFirstByTypeAndSlug(String type, String slug);



    public Long[] findCategoryIdsByProductId(long articleId);


    public void doUpdateProductCount(long categoryId);


}