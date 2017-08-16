
package io.jpress.service;

import io.jpress.model.Attachment;

public interface AttachmentService  {



    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Attachment findById(Object id);


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
    public boolean delete(Attachment model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Attachment model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Attachment model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Attachment model);
}
