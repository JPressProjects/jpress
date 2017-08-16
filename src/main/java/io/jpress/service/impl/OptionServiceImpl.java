
package io.jpress.service.impl;

import io.jpress.service.OptionService;
import io.jpress.model.Option;
import io.jboot.db.service.JbootServiceBase;

public class OptionServiceImpl extends JbootServiceBase<Option> implements OptionService {

    @Override
    public String findValueByKey(String key) {
        return null;
    }

    @Override
    public boolean saveOrUpdateOptionByKeyAndValue(String key, String value) {
        return false;
    }
}
