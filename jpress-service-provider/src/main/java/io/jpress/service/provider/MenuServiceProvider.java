/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.Jboot;
import io.jboot.aop.annotation.Bean;
import io.jboot.core.cache.annotation.CacheEvict;
import io.jboot.db.model.Column;
import io.jboot.service.JbootServiceBase;
import io.jpress.model.Menu;
import io.jpress.service.MenuService;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

@Bean
@Singleton
public class MenuServiceProvider extends JbootServiceBase<Menu> implements MenuService {

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
    public boolean saveOrUpdate(Menu model) {
        return super.saveOrUpdate(model);
    }

    @Override
    @CacheEvict(name = "menu", key = "*")
    public boolean deleteById(Object id) {
        return super.deleteById(id);
    }

    @Override
    public List<Menu> findListByType(String type) {
        List<Menu> menus = Jboot.me().getCache().get("menu",
                "type:" + type,
                () -> DAO.findListByColumn(Column.create("type", type), "order_number asc, id desc"));

        if (menus == null || menus.isEmpty()) {
            return null;
        }
        List<Menu> newList = new ArrayList<>();
        for (Menu menu : menus) {
            newList.add(menu.copy());
        }
        return newList;
    }
}