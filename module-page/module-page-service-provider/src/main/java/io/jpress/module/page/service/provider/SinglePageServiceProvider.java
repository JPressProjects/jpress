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
package io.jpress.module.page.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jpress.commons.service.JPressServiceBase;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.web.seoping.SeoManager;

import java.util.List;

@Bean
public class SinglePageServiceProvider extends JPressServiceBase<SinglePage> implements SinglePageService {


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
        boolean isSuccess = super.update(model);
        if (isSuccess && model.isNormal()) {
            SeoManager.me().ping(model.toPingData());
            SeoManager.me().baiduUpdate(model.getUrl());
        }
        return isSuccess;
    }

    @Override
    public Object save(SinglePage model) {
        Object ret = super.save(model);
        if (ret != null && model.isNormal()) {
            SeoManager.me().ping(model.toPingData());
            SeoManager.me().baiduPush(model.getUrl());
        }
        return ret;
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
    public List<SinglePage> findListByCategoryId(long categoryId) {
        return DAO.findListByColumn(Column.create("category_id", categoryId));
    }

    @Override
    public void doIncViewCount(long id) {
        PageViewsCountUpdateTask.recordCount(id);
    }

    @Override
//    @Cacheable(name = "pages")
    public Page<SinglePage> paginateInNormal(int page, int pageSize, String orderBy) {
        Columns columns = new Columns();
        columns.eq("status", SinglePage.STATUS_NORMAL);
        Page<SinglePage> dataPage = DAO.paginateByColumns(page, pageSize, columns, orderBy);
        return dataPage;
    }

    @Override
//    @Cacheable(name = "pages")
    public Page<SinglePage> paginateByCategoryIdInNormal(int page, int pageSize, Long categoryId, String orderBy) {

        Columns columns = new Columns();
        columns.eq("category_id", categoryId);
        columns.eq("status", SinglePage.STATUS_NORMAL);

        Page<SinglePage> dataPage = DAO.paginateByColumns(page, pageSize, columns, orderBy);
        return dataPage;
    }

    /**
     * 根据 columns 查询
     * @param page
     * @param pageSize
     * @param columns
     * @return
     */
    @Override
    public Page<SinglePage> _paginateWithoutTrashAndColumns(int page, int pageSize, Columns columns) {

        return DAO.paginateByColumns(page,
                pageSize,
                columns.ne("status", SinglePage.STATUS_TRASH),
                "id desc");
    }

    /**
     * 根据 columns 查询
     * @param page
     * @param pageSize
     * @param columns
     * @return
     */
    @Override
    public Page<SinglePage> _paginateByColumns(int page, int pageSize, Columns columns) {

        return DAO.paginateByColumns(page, pageSize, columns, "id desc");
    }


}