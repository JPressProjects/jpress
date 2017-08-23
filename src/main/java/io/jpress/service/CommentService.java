
package io.jpress.service;

import io.jpress.model.Comment;

public interface CommentService  {



    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Comment findById(Object id);


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
    public boolean delete(Comment model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Comment model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Comment model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Comment model);
}
