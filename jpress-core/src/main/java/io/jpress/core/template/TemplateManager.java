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
    private TemplateNotifier notifier;

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


    public List<Template> getInstalledTemplates() {
        String basePath = PathKit.getWebRootPath() + "/templates";

        List<File> templateFolderList = new ArrayList<File>();
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


    private void scanTemplateFloders(File file, List<File> list) {
        if (file.isDirectory()) {
            File configFile = new File(file, "template.properties");
            if (configFile.exists() && configFile.isFile()) {
                list.add(file);
            } else {
                File[] files = file.listFiles();
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
     *
     * @return
     */
    public Template getPreviewTemplate() {

        if (!JPressConfig.me.isTemplatePreviewEnable()) {
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


    public void setCurrentTemplate(String templateId) {
        Template template = getTemplateById(templateId);
        if (template == null) {
            throw new NullPointerException("template is null");
        }
        this.currentTemplateId.set(templateId);
        this.currentTemplate = template;
    }


    public void clearCache() {
        clearCache(true);
    }

    public void clearCache(boolean withNotify) {
        RenderManager.me().getEngine().removeAllTemplateCache();
        if (withNotify && notifier != null) {
            notifier.notifyClearCache();
        }
    }


    public TemplateNotifier getNotifier() {
        return notifier;
    }

    public void setNotifier(TemplateNotifier notifier) {
        this.notifier = notifier;
    }
}
