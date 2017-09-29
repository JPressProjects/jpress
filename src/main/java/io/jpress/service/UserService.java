
package io.jpress.service;

import com.jfinal.kit.Ret;
import io.jpress.model.User;

public interface UserService {


    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public User findById(Object id);


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
    public boolean delete(User model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(User model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(User model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(User model);


    /**
     * 进行登录
     *
     * @param loginName
     * @param password
     * @return
     */
    public Ret doLogin(String loginName, String password);


    /**
     * 根据用户登录名，查询用户信息
     *
     * @param username
     * @return
     */
    public User findByUserName(String username);
}
