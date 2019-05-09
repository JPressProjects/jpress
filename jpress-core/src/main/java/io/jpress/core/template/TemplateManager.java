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
package io.jpress.core.template;

import com.jfinal.kit.LogKit;
import com.jfinal.kit.PathKit;
import io.jboot.utils.StrUtil;
import io.jpress.JPressConfig;
import io.jpress.JPressOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TemplateManager {

    private Template currentTemplate;

    private static final TemplateManager me = new TemplateManager();

    private TemplateManager() {

    }


    public static TemplateManager me() {
        return me;
    }

    public void init() {
        String templateId = JPressOptions.get("web_template");
        TemplateManager.me().setCurrentTemplate(templateId);
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
                        if (f.isDirectory())
                            scanTemplateFloders(f, list);
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
            if (id.equals(template.getId())) return template;
        }
        return null;
    }

    public Template getCurrentTemplate() {
        return currentTemplate;
    }

    public void setCurrentTemplate(String templateId) {
        if (StrUtil.isBlank(templateId)) {
            initDefaultTemplate();
            return;
        }

        Template template = getTemplateById(templateId);
        if (template == null) {
            LogKit.warn("can not find tempalte " + templateId);
            initDefaultTemplate();
        } else {
            setCurrentTemplate(template);
        }
    }


    private void initDefaultTemplate() {
        setCurrentTemplate(getTemplateById(JPressConfig.me.getDefaultTemplate()));
    }


    public void setCurrentTemplate(Template currentTemplate) {
        this.currentTemplate = currentTemplate;
    }
}
