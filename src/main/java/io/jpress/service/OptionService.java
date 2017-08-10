package io.jpress.service;

/**
 * Created by michael on 2017/8/10.
 */
public interface OptionService {

    public String findValueByKey(String key);

    public boolean saveOrUpdateOptionByKeyAndValue(String key, String value);
}
