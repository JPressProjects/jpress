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
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.form.model.FormInfo;
import io.jpress.module.form.service.FormDataService;
import io.jpress.module.form.service.FormInfoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Date;


@RequestMapping(value = "/admin/form", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormInfoController extends AdminControllerBase {

    @Inject
    private FormInfoService formInfoService;

    @Inject
    private FormDataService formDataService;


    @AdminMenu(text = "表单", groupId = "form", order = 1)
    public void list() {

        //设置分页数量
        setPaginateSizeSpacing(8);

        String name = getPara("name");

        Columns columns = new Columns();
        columns.likeAppendPercent("name", name);
        Page<FormInfo> entries = formInfoService.paginateByColumnsWithInfo(getPagePara(), getPageSizePara(), columns,"id desc");
        setAttr("page", entries);

        render("form/form_info_list.html");
    }


    public void edit() {
        int entryId = getParaToInt(0, 0);

        FormInfo entry = entryId > 0 ? formInfoService.findById(entryId) : null;
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

        if (entry.getId() == null) {
            entry.setStatus(FormInfo.FORMINFO_STATUS_INIT);

            entry.setUuid(StrUtil.uuid());
        }

        formInfoService.saveOrUpdate(entry);
        renderJson(Ret.ok().set("id", entry.getId()));
    }


    /**
     * 发布
     */
    public void doPublish() {

        int entryId = getParaToInt(0, 0);

        FormInfo formInfo = entryId > 0 ? formInfoService.findById(entryId) : null;

        if (formInfo == null) {
            renderError(404);
            return;
        }


        Ret ret = formInfo.checkAllFields();
        if (ret.isFail()) {
            renderJson(ret);
            return;
        }

        formInfo.setStatus(FormInfo.FORMINFO_STATUS_PUBLISHED);
        formInfo.setVersion(formInfo.getVersion() == null ? 1 : formInfo.getVersion() + 1);


        //改变表单状态 status
        formInfoService.update(formInfo);


        //发布表单
        formInfoService.publish(formInfo);

        renderOkJson();
    }


    /**
     * 下架
     */
    public void doUnPublish() {

        int entryId = getParaToInt(0, 0);

        FormInfo entry = entryId > 0 ? formInfoService.findById(entryId) : null;

        if (entry == null) {
            renderError(404);
            return;
        }

        entry.setStatus(FormInfo.FORMINFO_STATUS_INIT);

        //改变表单状态 status
        formInfoService.update(entry);

        renderOkJson();
    }


    public void doDel() {
        Long id = getIdPara();

        //如果表单不存在 那么删除失败
        FormInfo formInfo = formInfoService.findById(id);
        if(formInfo == null){
            renderFailJson("删除失败");
            return;
        }

        //删除表数据
        formDataService.deleteTable(formInfo.getCurrentTableName());

        render(formInfoService.deleteById(id) ? Ret.ok() : Ret.fail());
    }


    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        formInfoService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }

    /**
     * 选择表单
     */
    public void browse() {

        Columns columns = new Columns();
        columns.eq("status",FormInfo.FORMINFO_STATUS_PUBLISHED);
        Page<FormInfo> page = formInfoService.paginateByColumns(getPagePara(), 8, columns);
        setAttr("page",page);

        render("form/form_browse.html");
    }

}
