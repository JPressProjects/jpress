
package io.jpress.service;

import io.jpress.model.Option;

public interface OptionService {


    /**
     * 根据ID查找model
     *
     * @param id
     * @return
     */
    public Option findById(Object id);


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
    public boolean delete(Option model);


    /**
     * 保存到数据库
     *
     * @param model
     * @return
     */
    public boolean save(Option model);

    /**
     * 保存或更新
     *
     * @param model
     * @return
     */
    public boolean saveOrUpdate(Option model);

    /**
     * 更新 model
     *
     * @param model
     * @return
     */
    public boolean update(Option model);


    /**
     * 根据 key 查找value值
     *
     * @param key
     * @return
     */
    public String findValue(String key);

    /**
     * 根据 key 查找value的值，返回boolean类型
     *
     * @param key
     * @return
     */
    public Boolean findValueAsBool(String key);

    /**
     * 根据 key 和 value 进行保存 或 更新为option
     *
     * @param key
     * @param value
     * @return
     */
    public boolean saveOrUpdateOptionByKeyAndValue(String key, String value);
}
