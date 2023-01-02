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
package io.jpress.core.template;

import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import com.jfinal.render.RenderManager;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.JPressConfig;
import io.jpress.JPressOptions;
import io.jpress.commons.CacheObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TemplateManager {

    private Template currentTemplate;
    private CacheObject currentTemplateId = new CacheObject("template", "id");
    private static final TemplateManager me = new TemplateManager();

    private TemplateManager() {
    }


    public static TemplateManager me() {
        return me;
    }


    public void init() {
        String templateId = JPressOptions.get("web_template");
        TemplateManager.me().initDefaultTemplate(templateId);
    }



    /**
     * 获取所有已经成功安装的模板
     * @return 模板列表
     */
    public List<Template> getInstalledTemplates() {
        String basePath = PathKit.getWebRootPath() + "/templates";

        List<File> templateFolderList = new ArrayList<>();
        scanTemplateFloders(new File(basePath), templateFolderList);

        List<Template> templatelist = null;
        if (templateFolderList.size() > 0) {
            templatelist = new ArrayList<>();
            for (File templateFolder : templateFolderList) {
                templatelist.add(new Template(templateFolder));
            }
        }
        return templatelist;
    }


    /**
     * 扫码 templateDir 目录下的所有模板，填充到 list 对象里
     * @param templateDir
     * @param list
     */
    private void scanTemplateFloders(File templateDir, List<File> list) {
        if (templateDir.isDirectory()) {
            File configFile = new File(templateDir, "template.properties");
            if (configFile.exists() && configFile.isFile()) {
                list.add(templateDir);
            } else {
                File[] files = templateDir.listFiles();
                if (null != files && files.length > 0) {
                    for (File f : files) {
                        if (f.isDirectory()) {
                            scanTemplateFloders(f, list);
                        }
                    }
                }
            }
        }
    }


    /**
     * 根据模板的 id 获取某个模板
     * @param id
     * @return
     */
    public Template getTemplateById(String id) {
        List<Template> templates = getInstalledTemplates();
        if (templates == null || templates.isEmpty()) {
            return null;
        }
        for (Template template : templates) {
            if (id.equals(template.getId())) {
                return template;
            }
        }
        return null;
    }


    /**
     * 设置当前网站的默认模板
     * @param templateId
     */
    private void initDefaultTemplate(String templateId) {
        if (StrUtil.isBlank(templateId)) {
            setCurrentTemplate(JPressConfig.me.getDefaultTemplate());
            return;
        }

        Template template = getTemplateById(templateId);
        if (template == null) {
            LogKit.warn("can not find tempalte " + templateId);
            setCurrentTemplate(JPressConfig.me.getDefaultTemplate());
        } else {
            setCurrentTemplate(templateId);
        }
    }


    /**
     * 获取网站的当前模板
     * 如果当前网站开启了 "模板预览" 功能，则优先通过 url 获取 "预览模板"
     * @return 当前的模板
     */
    public Template getCurrentTemplate() {
        Template previewTemplate = getPreviewTemplate();
        if (previewTemplate != null) {
            return previewTemplate;
        }

        String templateId = currentTemplateId.get();
        if (currentTemplate != null && currentTemplate.getId().equals(templateId)) {
            return currentTemplate;
        } else if (StrUtil.isNotBlank(templateId)) {
            setCurrentTemplate(templateId);
        } else {
            initDefaultTemplate(JPressOptions.get("web_template"));
        }

        return currentTemplate;
    }


    /**
     * 获取预览的模板
     * @return
     */
    public Template getPreviewTemplate() {
        if (!JPressOptions.isTemplatePreviewEnable()) {
            return null;
        }

        Controller controller = JbootControllerContext.get();
        if (controller == null) {
            return null;
        }

        String tId = controller.getPara("template");
        if (StrUtil.isBlank(tId)) {
            return null;
        }

        return getTemplateById(tId);
    }


    /**
     * 设置网站当前的默认模板
     * @param templateId
     */
    public void setCurrentTemplate(String templateId) {
        Template template = getTemplateById(templateId);
        if (template == null) {
            throw new NullPointerException("Template \"" + templateId +"\" is not exist.");
        }
        this.currentTemplateId.set(templateId);
        this.currentTemplate = template;
    }


    /**
     * 由于 JFinal 会进行模板缓存，当后台动态编辑模板内容的时候
     * 需要调用此方法清除模板缓存，才能 "实时生效"
     */
    public void clearCache() {
        RenderManager.me().getEngine().removeAllTemplateCache();
    }

}
