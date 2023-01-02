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
package io.jpress.module.form.controller.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.module.form.model.FormDatasource;
import io.jpress.module.form.model.FormDatasourceItem;
import io.jpress.module.form.service.FormDatasourceItemService;
import io.jpress.module.form.service.FormDatasourceService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;


@RequestMapping(value = "/admin/form/datasource/item", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormDatasourceItemController extends AdminControllerBase {

    @Inject
    private FormDatasourceItemService service;

    @Inject
    private FormDatasourceService formDatasourceService;


    public void index() {
        Long id = getParaToLong();

        if (id == null) {
            renderError(404);
            return;
        }

        setAttr("dictId",id);

        Columns columns = new Columns();
        columns.eq("datasource_id", id);
        columns.likeAppendPercent("value",getPara("value"));
        Page<FormDatasourceItem> entries = service.paginateByColumns(getPagePara(), getPageSizePara(), columns);
        setAttr("page", entries);

        render("form/form_datasource_item_list.html");
    }


    public void edit() {
        int entryId = getParaToInt(0, 0);

        FormDatasourceItem entry = entryId > 0 ? service.findById(entryId) : null;

        Long dictId = getParaToLong("dictId");

        setAttr("datasourceItem", entry);

        if (entry != null) {
            FormDatasource datasource = formDatasourceService.findById(entry.getDatasourceId());
            setAttr("datasource", datasource);
        }else {
            FormDatasource datasource = formDatasourceService.findById(dictId);
            setAttr("datasource", datasource);
        }

        List<FormDatasourceItem> datasourceItemList = service.findAll();
        setAttr("datasourceItemList", datasourceItemList);

        render("form/form_datasource_item_edit.html");
    }


    public void doSave() {
        FormDatasourceItem entry = getModel(FormDatasourceItem.class, "datasourceItem");

        if (entry.getPid() == null) {
            entry.setPid(0L);
        }

        service.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }



    public void doDel() {
        Long id = getIdPara();
        render(service.deleteById(id) ? Ret.ok() : Ret.fail());
    }


    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        service.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }

}
