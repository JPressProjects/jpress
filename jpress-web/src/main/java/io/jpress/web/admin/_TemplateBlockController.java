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
package io.jpress.web.admin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.db.model.Columns;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.JPressConsts;
import io.jpress.SiteContext;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.BlockContainerDef;
import io.jpress.core.template.BlockManager;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.TemplateBlockOption;
import io.jpress.service.TemplateBlockOptionService;
import io.jpress.web.base.AdminControllerBase;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/template/block", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _TemplateBlockController extends AdminControllerBase {

    @Inject
    private TemplateBlockOptionService blockOptionService;


    @AdminMenu(text = "板块", groupId = JPressConsts.SYSTEM_MENU_TEMPLATE, order = 7)
    public void index() {
        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        setAttr("template", currentTemplate);

        render("template/block_design.html");
    }


    /**
     * 获取编辑定义的组件
     */
    public void components() {
        renderJson(Ret.ok("components", BlockManager.me().getAllComponents()));
    }


    /**
     * 获取当前模板配置的数据
     */
    public void datas() {
        Template currentTemplate = TemplateManager.me().getCurrentTemplate();

        List<BlockContainerDef> containerDefs = currentTemplate.getContainerDefs();
        if (containerDefs == null || containerDefs.isEmpty()) {
            renderJson(Ret.ok());
            return;
        }

        JSONArray baseDatas = new JSONArray();
        for (BlockContainerDef containerDef : containerDefs) {
            baseDatas.add(containerDef.toBsFormData());
        }


        //把系统配置的内容，设置到 baseDatas 里去
        TemplateBlockOption templateBlockOption = blockOptionService.findFirstByColumns(Columns.create("template_id", currentTemplate.getId()));
        if (templateBlockOption != null) {
            JSONArray savedOptions = JSON.parseArray(templateBlockOption.getOptions());
            if (savedOptions != null && !savedOptions.isEmpty()) {
                for (int i = 0; i < savedOptions.size(); i++) {
                    JSONObject savedData = savedOptions.getJSONObject(i);
                    for (int j = 0; j < baseDatas.size(); j++) {
                        if (savedData.getString("id").equals(baseDatas.getJSONObject(j).getString("id"))) {
                            baseDatas.set(j, savedData);
                        }
                    }
                }
            }
        }

        renderJson(Ret.ok("datas", baseDatas));
    }


    /**
     * 保存数据
     */
    public void save() {
        Template currentTemplate = TemplateManager.me().getCurrentTemplate();
        String rawData = getRawData();
        TemplateBlockOption templateBlockOption = blockOptionService.findById(currentTemplate.getId(), SiteContext.getSiteId());
        boolean needSave = false;
        if (templateBlockOption == null) {
            templateBlockOption = new TemplateBlockOption();
            templateBlockOption.setTemplateId(currentTemplate.getId());
            templateBlockOption.setSiteId(SiteContext.getSiteId());
            needSave = true;
        }

        templateBlockOption.setOptions(rawData);
        if (needSave) {
            blockOptionService.save(templateBlockOption);
        } else {
            blockOptionService.update(templateBlockOption);
        }

        renderOkJson();
    }


    /**
     * 模板渲染
     *
     * @param componentData
     */
    public void render(@JsonBody JSONObject componentData) {
        String html = BlockManager.me().renderComponentDataToHtml(componentData, null, true);
        renderHtml(html);
    }


}
