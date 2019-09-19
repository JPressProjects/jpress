package io.jpress.module.product.service;

import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jpress.module.product.model.Product;

import java.util.List;

public interface ProductService  {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Product findById(Object id);


    /**
     * find all model
     *
     * @return all <Product
     */
    public List<Product> findAll();


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
    public boolean delete(Product model);


    /**
     * save model to database
     *
     * @param model
     * @return  id value if save success
     */
    public Object save(Product model);


    /**
     * save or update model
     *
     * @param model
     * @return id value if saveOrUpdate success
     */
    public Object saveOrUpdate(Product model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Product model);


    /**
     * paginate query
     *
     * @param page
     * @param pageSize
     * @return
     */
    public Page<Product> paginate(int page, int pageSize);


    public boolean doChangeStatus(long id, String status);


    public Page<Product> paginateInNormal(int page, int pagesize);

    public Page<Product> paginateInNormal(int page, int pagesize, String orderBy);

    public Page<Product> paginateByCategoryIdInNormal(int page, int pagesize, long categoryId, String orderBy);

    public List<Product> findListByColumns(Columns columns, String orderBy, Integer count);

    public Product findFirstBySlug(String slug);
}