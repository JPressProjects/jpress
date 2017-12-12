
package io.jpress.service.impl;

import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.Cacheable;
import io.jboot.event.JbootEvent;
import io.jboot.event.JbootEventListener;
import io.jboot.event.annotation.EventConfig;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StringUtils;
import io.jpress.model.Option;
import io.jpress.service.OptionService;

/**
 * OptionService 的实现类
 * <p>
 * 通过 去实现JbootEventListener 监听器，并通过 @EventConfig 让监听器监听感兴趣的事件
 */
@EventConfig(action = {Option.ACTION_ADD, Option.ACTION_DELETE, Option.ACTION_UPDATE})
public class OptionServiceImpl extends JbootServiceBase<Option> implements OptionService, JbootEventListener {


    @Cacheable(name = "option", key = "#(key)", unless = "key == null")
    @Override
    public String findValue(String key) {
        Option option = DAO.findFirstByColumn("option_key", key);
        return option == null ? null : option.getValue();
    }

    public Boolean findValueAsBool(String key) {
        String value = findValue(key);
        if (StringUtils.isNotBlank(value)) {
            try {
                return Boolean.parseBoolean(value);
            } catch (Exception e) {
            }
        }
        return null;
    }


    @Override
    public boolean saveOrUpdateOptionByKeyAndValue(String key, String value) {
        Option option = DAO.findFirstByColumn("option_key", key);

        if (null == option) {
            option = new Option();
        }

        option.setKey(key);
        option.setValue(value);

        return option.saveOrUpdate();
    }

    /**
     * 这个方法是实现 JbootEventListener
     * 同时配置 EventConfig 监听了 Option.ACTION_ADD, Option.ACTION_DELETE, Option.ACTION_UPDATE 这三个action
     * 也就是说：当有option被添加，删除，修改的时候，onEvent方法会被调用。
     * <p>
     * 此方法被调用的时候，通过 @CacheEvict 注解，删除缓存。
     *
     * @param event
     */
    @CacheEvict(name = "option", key = "#(event.data.option_key)")
    @Override
    public void onEvent(JbootEvent event) {
//        也可以通过 @CacheEvict 来实现删除缓存
//        Option option = event.getData();
//        Jboot.me().getCache().remove("option", option.getOptionKey());
    }
}
