package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.core.cache.annotation.Cacheable;
import io.jpress.service.OptionService;
import io.jpress.model.Option;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class OptionServiceProvider extends JbootServiceBase<Option> implements OptionService {

    @Override
    @Cacheable(name = "option", key = "#(key)", nullCacheEnable = true)
    public String findByKey(String key) {
        Option option = DAO.findFirstByColumn("key", key);
        return option == null ? null : option.getValue();
    }

    @Override
    public Boolean findAsBoolByKey(String key) {
        String value = findByKey(key);
        return value == null ? null : Boolean.valueOf(value);
    }

    @Override
    public Integer findAsIntegerByKey(String key) {
        String value = findByKey(key);
        return value == null ? null : Integer.valueOf(value);
    }

    @Override
    public Float findAsFloatByKey(String key) {
        String value = findByKey(key);
        return value == null ? null : Float.valueOf(value);
    }

    @Override
    public boolean saveOrUpdate(String key, String value) {
        Option option = DAO.findFirstByColumn("key", key);
        if (option == null) option = new Option();

        option.setValue(value);

        return saveOrUpdate(option);
    }

    @Override
    public boolean deleteById(Object id) {
        Option option = findById(id);
        if (option == null) return true;
        return delete(option);
    }

    @Override
    @CacheEvict(name = "option", key = "#(model.key)")
    public boolean update(Option model) {
        return super.update(model);
    }


    @Override
    @CacheEvict(name = "option", key = "#(model.key)")
    public boolean delete(Option model) {
        return super.delete(model);
    }
}