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
import io.jpress.commons.utils.HttpProxy;
import io.jpress.core.bsformbuilder.BsFormOption;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.form.model.FormDict;
import io.jpress.module.form.model.FormDictItem;
import io.jpress.module.form.service.FormDictItemService;
import io.jpress.module.form.service.FormDictService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RequestMapping(value = "/admin/form/formDict", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormDictController extends AdminControllerBase {

    @Inject
    private FormDictService dictService;

    @Inject
    private FormDictItemService itemService;

    @AdminMenu(text = "数据字典", groupId = "form", order = 2)
    public void list() {

        String name = getPara("name");
        Columns columns = new Columns();
        columns.likeAppendPercent("name", name);
        Page<FormDict> entries = dictService.paginateByColumns(getPagePara(), getPageSizePara(), columns);
        setAttr("page", entries);

        render("form/form_dict_list.html");
    }

    public void edit() {
        int entryId = getParaToInt(0, 0);

        FormDict entry = entryId > 0 ? dictService.findById(entryId) : null;
        setAttr("formDict", entry);
        set("now", new Date());
        render("form/form_dict_edit.html");
    }

    public void doSave() {
        FormDict entry = getModel(FormDict.class, "formDict");

        if (entry.getName() == null) {
            renderFailJson("字典名称不能为空");
            return;
        }

        dictService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }


    public void doDel() {
        Long id = getIdPara();
        render(dictService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        dictService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


    /**
     * 获取所有字典列表
     */
    public void queryList() {
        List<FormDict> dicts = dictService.findAll();
        if (dicts != null) {
            dicts.forEach(dict -> dict.keep("id", "name"));
        }
        renderJson(Ret.ok().set("dicts", dicts));
    }

    /**
     * 获取字典内容
     */
    public void queryOptions() {
        FormDict formDict = dictService.findById(getParaToLong());
        if (formDict == null) {
            renderFailJson();
            return;
        }

        //静态数据
        if (formDict.isStaticData()) {
            List<FormDictItem> dictItemList = itemService.findListByColumns(Columns.create("dict_id", formDict.getId()));
            List<BsFormOption> bsFormOptions = dictItemList.stream().map(item -> new BsFormOption(item.getText(), item.getValue())).collect(Collectors.toList());
            renderJson(Ret.ok().set("options", bsFormOptions));
        }
        //动态数据
        else {
            String url = formDict.getImportText();
            HttpProxy proxy = new HttpProxy();
            proxy.start(url, getResponse());
            renderNull();
        }
    }
}
