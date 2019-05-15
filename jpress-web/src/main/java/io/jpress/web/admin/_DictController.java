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
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Dict;
import io.jpress.model.DictType;
import io.jpress.service.DictService;
import io.jpress.service.DictTypeService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;


@RequestMapping(value = "/admin/dict", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _DictController extends AdminControllerBase {

    @Inject
    private DictService service;

    @Inject
    private DictTypeService dictTypeService;

    @AdminMenu(text = "字典管理", groupId = JPressConsts.SYSTEM_MENU_SYSTEM, order = 80)
    public void index() {
        Page<Dict> entries=service.paginate(getPagePara(), 10);
        dictTypeService.join(entries, "type", new String[] {"dictType"} );
        setAttr("page", entries);
        render("dict/dict_list.html");
    }

   
    public void edit() {
        int entryId = getParaToInt(0, 0);
        Dict entry = entryId > 0 ? service.findById(entryId) : null;
        setAttr("dict", entry);

        List<DictType> typeList = dictTypeService.findAll();
        setAttr("typeList", typeList);
        render("dict/dict_edit.html");
    }
   
    public void doSave() {
        Dict entry = getModel(Dict.class,"dict");
        service.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }

    public void doDel() {
        Long id = getIdPara();
        render(service.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    public void doDelByIds() {
        String ids = getPara("ids");
        render(service.deleteByIds(ids) ? Ret.ok() : Ret.fail());
    }

}