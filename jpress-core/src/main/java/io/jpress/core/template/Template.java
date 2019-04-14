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

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

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
    private List<String> flags = new ArrayList<>();

    public Template() {

    }

    public Template(File templateFolder) {

        File propFile = new File(templateFolder, "template.properties");
        Prop prop = new Prop(propFile, "utf-8");

        this.folder = buildFolder(templateFolder);

        this.id = prop.get("id");
        this.title = prop.get("title");
        this.description = prop.get("description");
        this.author = prop.get("author");
        this.authorWebsite = prop.get("authorWebsite");
        this.version = prop.get("version");
        this.updateUrl = prop.get("updateUrl");


        String vcode = prop.get("versionCode");
        this.versionCode = StrUtil.isBlank(vcode) ? 1 : Integer.valueOf(vcode);
        this.screenshot = getWebAbsolutePath() + "/screenshot.png";

        String[] files = propFile
                .getParentFile()
                .list((dir, name) -> name.endsWith(".html"));

        if (files != null && files.length > 0) {
            this.htmls.addAll(Arrays.asList(files));
        }

        String flagStrings = prop.get("flags");
        if (StrUtil.isNotBlank(flagStrings)) {
            String[] strings = flagStrings.split(",");
            for (String s : strings) {
                if (StrUtil.isBlank(s)) {
                    continue;
                }
                this.flags.add(s.trim());
            }
        }
    }

    private static String buildFolder(File templateFolder) {
        String basePath = PathKit.getWebRootPath()
                .concat(File.separator)
                .concat("templates")
                .concat(File.separator);
        return FileUtil.removePrefix(templateFolder.getAbsolutePath(), basePath);
    }


    private static final String TEMPLATE_SEPARATOR = "_";
    private static final String TEMPLATE_H5_SUFFIX = "_h5.html";

    /**
     * 找出可以用来渲染的 html 模板
     *
     * @param template
     * @return
     */
    public String matchTemplateFile(String template, boolean isMoblieBrowser) {

        if (isMoblieBrowser) {
            int indexOf = template.indexOf(".");
            template = template.substring(0, indexOf) + TEMPLATE_H5_SUFFIX;
        }

        if (htmls.contains(template)) {
            return template;
        }

        int lastIndex = template.lastIndexOf(TEMPLATE_SEPARATOR);
        if (lastIndex <= 0) {
            return null;
        }

        //手机浏览器，优先去找_h5的模板进行渲染
        if (isMoblieBrowser) {
            String h5Template = matchH5Template(template);
            if (h5Template != null) return h5Template;
        }

        while (lastIndex > 0) {
            template = template.substring(0, lastIndex) + ".html";
            if (htmls.contains(template)) {
                return template;
            }
            lastIndex = template.lastIndexOf(TEMPLATE_SEPARATOR);
        }

        return htmls.contains(template) ? template : null;
    }


    /**
     * 只匹配 h5 的模板 ，如果匹配不到 h5 ，返回 null
     * <p>
     * 例如：
     * 需要 aa_bb_cc_dd_h5.html
     * 寻找的顺序是：aa_bb_cc_h5.html  ->   aa_bb_h5.html  ->   aa_h5.html
     *
     * @param template
     * @return
     */
    private String matchH5Template(String template) {

        while (StringUtils.countMatches(template, '_') > 1) {

            int sLastIndex = StringUtils.lastOrdinalIndexOf(template, "_", 2);
            template = template.substring(0, sLastIndex) + "_h5.html";

            if (htmls.contains(template)) {
                return template;
            }
        }

        return htmls.contains(template) ? template : null;
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

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public String getAbsolutePath() {
        StringBuilder path = new StringBuilder(PathKit.getWebRootPath())
                .append(File.separator)
                .append("templates")
                .append(File.separator)
                .append(folder);
        return path.toString();
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
            //xxx_h5.html 不算独立样式
            if (html.startsWith(prefix) && !html.contains("_h5.")) {
                styles.add(html.substring(prefix.length(), html.length() - 5));
            }
        }

        return styles;
    }

    public void uninstall() {
        FileUtils.deleteQuietly(new File(getAbsolutePath()));
    }
}
