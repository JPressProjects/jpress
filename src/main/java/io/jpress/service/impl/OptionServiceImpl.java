package io.jpress.service.impl;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventListener;
import io.jboot.event.annotation.EventConfig;
import io.jpress.model.Option;
import io.jpress.service.OptionService;

/**
 * Created by michael on 2017/8/10.
 */
@Bean
@EventConfig(action = {Option.ACTION_ADD, Option.ACTION_DELETE, Option.ACTION_UPDATE})
public class OptionServiceImpl implements OptionService, JbootEventListener {

    private static final Option DAO = new Option();

    /**
     * 根据 key 查询 option 的value值
     *
     * @param key
     * @return
     */
    @Override
    @Cacheable(name = "option", key = "#(key)", unless = "key == null")
    public String findValueByKey(String key) {
        Option option = DAO.findFirstByColumn("option_key", key);
        return option == null ? null : option.getOptionValue();
    }

    @Override
    public boolean saveOrUpdateOptionByKeyAndValue(String key, String value) {
        Option option = DAO.findFirstByColumn("option_key", key);
        if (null == option) {
            option = new Option();
        }

        option.setOptionKey(key);
        option.setOptionValue(value);
        return option.saveOrUpdate();
    }


    /**
     * 这个方法是实现J bootEventListener
     * 同时配置 EventConfig 监听了 Option.ACTION_ADD, Option.ACTION_DELETE, Option.ACTION_UPDATE 这三个action
     * 也就是说：当有option被添加，删除，修改的时候，onEvent方法会被调用。
     * <p>
     * 此方法被调用的时候，通过 @CacheEvict 注解，删除缓存。
     *
     * @param event
     */
    @Override
    @CacheEvict(name = "option", key = "#(event.data.option_key)")
    public void onEvent(JbootEvent event) {

//        也可以通过 @CacheEvict 来实现删除缓存
//        Option option = event.getData();
//        Jboot.me().getCache().remove("option", option.getOptionKey());

    }
}
