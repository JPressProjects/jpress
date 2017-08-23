
package io.jpress.service;

import io.jpress.model.Metadata;

public interface MetadataService  {



    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Metadata findById(Object id);


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
    public boolean delete(Metadata model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Metadata model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Metadata model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Metadata model);
}
