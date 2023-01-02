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

import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.model.Utm;
import io.jpress.service.UtmService;
import io.jpress.service.task.UtmBatchSaveTask;

@Bean
public class UtmServiceProvider extends JPressServiceBase<Utm> implements UtmService {

    @Override
    public void doRecord(Utm utm) {
        UtmBatchSaveTask.record(utm);
    }

    @Override
    public Page<Utm> _paginateByUserId(int page, int pagesize, long userId) {
        return DAO.paginateByColumn(page, pagesize, Column.create("user_id", userId), "created desc");
    }
}