
package io.jpress.service;

import io.jpress.model.Category;

public interface CategoryService  {



    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Category findById(Object id);


    /**
     * 根据ID删除model
     *
     * @param id
     * @return
     */
    public boolean deleteById(Object id);

    /**
     * 删除
     *
     * @param model
     * @return
     */
    public boolean delete(Category model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Category model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Category model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Category model);
}
