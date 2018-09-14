/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Template {

    private String id;
    private String title;
    private String description;
    private String author;
    private String authorWebsite;
    private String version;
    private int versionCode;
    private String updateUrl;
    private String folder;
    private String screenshot;

    private List<String> htmls = new ArrayList<>();

    public Template() {

    }

    public Template(String propertiesFilePath) {

        File propertiesFile = new File(propertiesFilePath);
        Prop prop = new Prop(propertiesFile, "utf-8");

        this.folder = propertiesFile.getParentFile().getName();

        this.id = prop.get("id");
        this.title = prop.get("title");
        this.description = prop.get("description");
        this.author = prop.get("author");
        this.authorWebsite = prop.get("authorWebsite");
        this.version = prop.get("version");
        this.versionCode = prop.getInt("versionCode", 0);
        this.updateUrl = prop.get("updateUrl");
        this.screenshot = "/templates/" + folder + "/" + prop.get("screenshot", "screenshot.png");

        String[] files = propertiesFile.list((dir, name) -> name.endsWith(".html"));
        htmls.addAll(Arrays.asList(files));
    }


    private static final String FILE_SEPARATOR = "_";

    /**
     * 找出可以用来渲染的 html 模板
     *
     * @param template
     * @return
     */
    public String matchTemplateFile(String template) {
        do {
            if (htmls.contains(template)) {
                return template;
            }
            template = template.substring(0, template.lastIndexOf(FILE_SEPARATOR)) + ".html";
        } while (template.contains(FILE_SEPARATOR));

        return null;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getAuthorWebsite() {
        return authorWebsite;
    }

    public void setAuthorWebsite(String authorWebsite) {
        this.authorWebsite = authorWebsite;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public String getUpdateUrl() {
        return updateUrl;
    }

    public void setUpdateUrl(String updateUrl) {
        this.updateUrl = updateUrl;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }


    public String getAbsolutePath() {
        return PathKit.getWebRootPath() + "/templates/" + folder;
    }

    public String getWebAbsolutePath() {
        return "/templates/" + folder;
    }

    /**
     * 获得某个模块下支持的样式
     * 一般用于在后台设置
     *
     * @param prefix
     * @return
     */
    public List<String> getSupportStyles(String prefix) {

        if (prefix == null) {
            throw new IllegalArgumentException("prefix must not be null");
        }

        List<String> styles = new ArrayList<>();
        for (String html : htmls) {
            if (html.startsWith(prefix)) {
                styles.add(html.substring(prefix.length(), html.length()));
            }
        }

        return styles;
    }
}
