/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.service.JbootServiceBase;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.seoping.SeoManager;

import java.util.List;

@Bean
public class SinglePageServiceProvider extends JbootServiceBase<SinglePage> implements SinglePageService {

    @Override
    public void deleteCacheById(Object id) {
        DAO.deleteIdCacheById(id);
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        return Db.update("delete from single_page where id in  " + SqlUtils.buildInSqlPara(ids)) > 0;
    }

    @Override
    public Page<SinglePage> _paginateByStatus(int page, int pagesize, String title, String status) {

        Columns columns = Columns.create("status", status);
        if (StrUtil.isNotBlank(title)) {
            columns.like("title", "%" + title + "%");
        }

        return DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");
    }

    @Override
    public Page<SinglePage> _paginateWithoutTrash(int page, int pagesize, String title) {

        Columns columns = Columns.create(Column.create("status", SinglePage.STATUS_TRASH, Column.LOGIC_NOT_EQUALS));
        if (StrUtil.isNotBlank(title)) {
            columns.like("title", "%" + title + "%");
        }

        return DAO.paginateByColumns(page,
                pagesize,
                columns,
                "id desc");
    }

    @Override
    public boolean doChangeStatus(long id, String status) {
        SinglePage page = findById(id);
        page.setStatus(status);
        return update(page);
    }

    @Override
    public boolean update(SinglePage model) {
        if (model.isNormal()) {
            SeoManager.me().ping(model.toPingData());
            SeoManager.me().baiduUpdate(model.getUrl());
        }
        return super.update(model);
    }

    @Override
    public Object save(SinglePage model) {
        if (model.isNormal()) {
            SeoManager.me().ping(model.toPingData());
            SeoManager.me().baiduPush(model.getUrl());
        }
        return super.save(model);
    }

    @Override
    public int findCountByStatus(String status) {
        return Db.queryInt("select count(*) from single_page where status = ?", status);
    }

    @Override
    public SinglePage findFirstBySlug(String slug) {
        return DAO.findFirstByColumn(Column.create("slug", slug));
    }

    @Override
    public List<SinglePage> findListByFlag(String flag) {
        return DAO.findListByColumn(Column.create("flag", flag));
    }

    @Override
    public void doIncViewCount(long id) {
        PageViewsCountUpdateTask.recordCount(id);
    }
}