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

import com.jfinal.plugin.activerecord.Model;
import io.jboot.aop.annotation.Bean;
import io.jboot.components.cache.annotation.CacheEvict;
import io.jboot.components.cache.annotation.Cacheable;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.ModelUtil;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;

import java.util.List;

@Bean
public class MenuServiceProvider extends JPressServiceBase<Menu> implements MenuService {

    @Override
    public int sync(List<Menu> menus) {

        if (menus == null || menus.isEmpty()) {
            return 0;
        }

        int syncCounter = 0;
        for (Menu menu : menus) {

            Menu dbMenu = DAO.findFirstByColumn("url", menu.getUrl());

            if (dbMenu == null) {
                menu.save();
                syncCounter++;
            }
        }
        return syncCounter;
    }

    @Override
    @CacheEvict(name = "menu", key = "*")
    public void shouldUpdateCache(int action, Model model, Object id) {
        super.shouldUpdateCache(action, model, id);
    }

    @Override
    public List<Menu> findListByType(String type) {
        return ModelUtil.copy(findListByTypeInDb(type));
    }

    @Override
    public List<Menu> findListByParentId(Object id) {
        return DAO.findListByColumn(Column.create("pid", id), "order_number asc, id desc");
    }

    @Override
    public List<Menu> findListByRelatives(String table, Object id) {
        Columns columns = Columns.create("relative_id", id).eq("relative_table", table);
        return DAO.findListByColumns(columns);
    }

    @Override
    public Menu findFirstByRelatives(String table, Object id) {
        Columns columns = Columns.create("relative_id", id).eq("relative_table", table);
        return DAO.findFirstByColumns(columns);
    }

    @Cacheable(name = "menu", key = "type:#(type)")
    public List<Menu> findListByTypeInDb(String type) {
        return DAO.findListByColumn(Column.create("type", type), "order_number asc, id desc");
    }
}