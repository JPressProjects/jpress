/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.service.provider;

import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Columns;
import io.jpress.SiteContext;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.Option;
import io.jpress.service.OptionService;


@Bean
public class OptionServiceProvider extends JPressServiceBase<Option> implements OptionService {

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
    @CacheEvict(name = "option", key = "#(key)")
    public Object saveOrUpdate(String key, String value) {
        Option option = DAO.findFirstByColumn("key", key);

        if (option == null) {
            option = new Option();
            option.setKey(key);
            option.setSiteId(SiteContext.getSiteId());
        }

        option.setValue(value);

        return saveOrUpdate(option);
    }


    @Override
    @CacheEvict(name = "option", key = "#(key)")
    public Object saveOrUpdate(String key, String value, Long siteId) {
        Option option = DAO.findFirstByColumns(Columns.create("key", key).eq("site_id",siteId));

        if (option == null) {
            option = new Option();
            option.setKey(key);
            option.setSiteId(siteId);
        }

        option.setValue(value);

        return saveOrUpdate(option);
    }


    @Override
    public boolean deleteById(Object id) {
        Option option = findById(id);
        if (option == null) {
            return true;
        }
        return delete(option);
    }

    @Override
    public boolean deleteByKey(String key) {
        Option option = DAO.findFirstByColumn("key", key);
        if (option == null) {
            return true;
        }

        return deleteById(option.getId());
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