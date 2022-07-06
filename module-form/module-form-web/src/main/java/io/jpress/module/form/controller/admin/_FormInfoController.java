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
package io.jpress.module.form.controller.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;


@RequestMapping(value = "/admin/form/formInfo", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormInfoController extends AdminControllerBase {

    @Inject
    private FormInfoService service;

    @AdminMenu(text = "表单", groupId = "form", order = 1)
    public void list() {

        String name = getPara("name");
        Columns columns = new Columns();
        columns.likeAppendPercent("name", name);
        Page<FormInfo> entries = service.paginateByColumns(getPagePara(), getPageSizePara(), columns);
        setAttr("page", entries);
        render("form/form_info_list.html");
    }


    public void edit() {
        int entryId = getParaToInt(0, 0);

        FormInfo entry = entryId > 0 ? service.findById(entryId) : null;
        setAttr("formInfo", entry);
        set("now", new Date());
        render("form/form_info_edit.html");
    }

    public void doSave() {
        FormInfo entry = getModel(FormInfo.class, "formInfo");

        if (entry.getName() == null) {
            renderFailJson("请输入名称");
            return;
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
