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

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Inject;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.SiteContext;
import io.jpress.commons.utils.HttpProxy;
import io.jpress.core.bsformbuilder.BsFormDatasource;
import io.jpress.core.bsformbuilder.BsFormOption;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.form.model.FormDatasource;
import io.jpress.module.form.model.FormDatasourceItem;
import io.jpress.module.form.service.FormDatasourceItemService;
import io.jpress.module.form.service.FormDatasourceService;
import io.jpress.web.base.AdminControllerBase;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@RequestMapping(value = "/admin/form/datasource", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _FormDatasourceController extends AdminControllerBase {

    @Inject
    private FormDatasourceService formDatasourceService;

    @Inject
    private FormDatasourceItemService itemService;

    @AdminMenu(text = "数据源", groupId = "form", order = 2)
    public void list() {

        String name = getPara("name");
        Columns columns = new Columns();
        columns.likeAppendPercent("name", name);
        Page<FormDatasource> entries = formDatasourceService.paginateByColumns(getPagePara(), getPageSizePara(), columns,"id desc");
        setAttr("page", entries);

        render("form/form_datasource_list.html");
    }

    public void edit() {
        int entryId = getParaToInt(0, 0);

        FormDatasource entry = entryId > 0 ? formDatasourceService.findById(entryId) : null;
        setAttr("datasource", entry);
        render("form/form_datasource_edit.html");
    }

    public void doSave() {
        FormDatasource entry = getModel(FormDatasource.class, "datasource");

        if (entry.getName() == null) {
            renderFailJson("名称不能为空");
            return;
        }

        if (entry.getId() == null) {
            entry.setWithCascade(false);
            entry.setCreated(new Date());
        }

        formDatasourceService.saveOrUpdate(entry);

        //查询是否该 字典 对应的 item
        FormDatasourceItem datasourceItem = itemService.findFirstByColumns(Columns.create().eq("datasource_id", entry.getId()));

        //如果查询不为空 那么 删除然后新建
        if (datasourceItem != null) {
            itemService.deleteByDictId(entry.getId());
        }

        boolean saveOk = saveItem(entry);

        if (!saveOk) {
            renderFailJson("数据源 数据导入失败 请检查数据");
            return;
        }

        renderJson(Ret.ok().set("id", entry.getId()));
    }

    //保存字典对应的 item 信息 对于json 格式 有很严格的要求
    public boolean saveItem(@NotNull FormDatasource entry) {

        //内容 放入字典中 item
        if (entry.getImportType() != null && entry.getImportText() != null) {

            switch (entry.getImportType()) {

                //等于 0 时 为一行一个内容
                case 0:

                    String importText = entry.getImportText();

                    String[] split = importText.split("\n");

                    for (String text : split) {

                        FormDatasourceItem formDatasourceItem = new FormDatasourceItem();

                        //如果是中文的分号 替换为英文的分号
                        if (text.contains("：")) {
                            text = text.replace("：", ":");
                        }

                        String[] keyAndValue = text.split(":");

                        formDatasourceItem.setDatasourceId(entry.getId());
                        formDatasourceItem.setPid(0L);
                        formDatasourceItem.setSiteId(SiteContext.getSiteId());

                        //防止 数组下标越界
                        try {
                            formDatasourceItem.setValue(keyAndValue[0]);
                            formDatasourceItem.setText(keyAndValue[1]);
                        } catch (Exception e) {
                            LogKit.error("Dict Item error....." + e);
                            return false;
                        }

                        itemService.saveOrUpdate(formDatasourceItem);
                    }

                    break;

                //等于1 时 为 json 对象
                case 1:

                    String importTexts = StrUtil.unEscapeHtml(entry.getImportText());

                    //JSON 数组格式
                    if (importTexts.contains("[")) {

                        List<FormDatasourceItem> formDatasourceItems = JSON.parseArray(importTexts, FormDatasourceItem.class);

                        for (FormDatasourceItem formDatasourceItem : formDatasourceItems) {

                            formDatasourceItem.setDatasourceId(entry.getId());
                            formDatasourceItem.setPid(0L);
                            formDatasourceItem.setSiteId(SiteContext.getSiteId());

                            itemService.saveOrUpdate(formDatasourceItem);
                        }

                    //JSON 对象
                    } else {
                        FormDatasourceItem formDatasourceItem = JSON.parseObject(importTexts, FormDatasourceItem.class);

                        formDatasourceItem.setDatasourceId(entry.getId());
                        formDatasourceItem.setPid(0L);
                        formDatasourceItem.setSiteId(SiteContext.getSiteId());

                        itemService.saveOrUpdate(formDatasourceItem);
                    }
                    break;
            }

        }

        return true;

    }


    public void doDel() {
        Long id = getIdPara();
        render(formDatasourceService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        formDatasourceService.batchDeleteByIds(getParaSet("ids").toArray());
        renderOkJson();
    }


    /**
     * 获取所有字典列表
     */
    public void queryDatasources() {
        List<FormDatasource> dicts = formDatasourceService.findAll();
        if (dicts == null) {
            renderJson(Ret.fail("没有任何数据源"));
            return;
        }

        List<BsFormDatasource> datasources = new ArrayList<>();
        dicts.forEach(dict -> datasources.add(new BsFormDatasource(
                dict.getName(),
                dict.getId().toString(),
                "/admin/form/datasource/queryOptions/" + dict.getId())));
        renderJson(Ret.ok().set("datasources", datasources));
    }

    /**
     * 获取字典内容
     */
    public void queryOptions() {
        FormDatasource datasource = formDatasourceService.findById(getParaToLong());
        if (datasource == null) {
            renderFailJson();
            return;
        }

        //静态数据
        if (datasource.isStaticData()) {
            List<FormDatasourceItem> dictItemList = itemService.findListByColumns(Columns.create("datasource_id", datasource.getId()));
            List<BsFormOption> bsFormOptions = dictItemList.stream().map(item -> new BsFormOption(item.getText(), item.getValue())).collect(Collectors.toList());
            renderJson(Ret.ok().set("options", bsFormOptions));
        }
        //动态数据
        else {
            String url = datasource.getImportText();
            HttpProxy proxy = new HttpProxy();
            proxy.start(url, getResponse());
            renderNull();
        }
    }
}
