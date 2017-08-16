
package io.jpress.service;

import io.jpress.model.Content;

public interface ContentService  {



    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Content findById(Object id);


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
    public boolean delete(Content model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Content model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Content model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Content model);
}
