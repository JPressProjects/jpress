
package io.jpress.service;

import io.jpress.model.Mapping;

public interface MappingService  {



    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Mapping findById(Object id);


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
    public boolean delete(Mapping model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Mapping model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Mapping model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Mapping model);
}
