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

import com.jfinal.kit.PathKit;
import com.jfinal.kit.Prop;
import io.jboot.Jboot;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.*;

public class Template {

    private String id;
    private String title;
    private String description;
    private String author;
    private String authorWebsite;
    private String version;
    private int versionCode;
    private String updateUrl;
    private String screenshot;

    private String relativePath;

    //模板文件列表
    private Set<String> htmls = new HashSet<>();

    //板块文件列表
    private Set<String> blocks = new HashSet<>();

    //模板支持的 flag 配置
    private List<String> flags = new ArrayList<>();


    public Template() {

    }

    public Template(File templateFolder) {

        File propFile = new File(templateFolder, "template.properties");
        Prop prop = new Prop(propFile, "utf-8");

        this.id = prop.get("id");
        this.title = prop.get("title");
        this.description = prop.get("description");
        this.author = prop.get("author");
        this.authorWebsite = prop.get("authorWebsite");
        this.version = prop.get("version");
        this.updateUrl = prop.get("updateUrl");

        this.relativePath = FileUtil.removeRootPath(templateFolder.getAbsolutePath()).replace("\\", "/");

        String vcode = prop.get("versionCode");
        this.versionCode = StrUtil.isBlank(vcode) ? 1 : Integer.valueOf(vcode);
        this.screenshot = relativePath + "/screenshot.png";

        refresh();
    }


    public void refresh() {

        this.htmls.clear();
        this.flags.clear();

        File path = getAbsolutePathFile();
        Prop prop = new Prop(new File(path, "template.properties"), "utf-8");

        String[] templateFiles = path
                .list((dir, name) -> name.endsWith(".html") && !name.startsWith("block"));

        if (templateFiles != null && templateFiles.length > 0) {
            this.htmls.addAll(Arrays.asList(templateFiles));
        }

        String[] sectionFiles = path
                .list((dir, name) -> name.endsWith(".html") && name.startsWith("block"));

        if (sectionFiles != null && sectionFiles.length > 0) {
            this.blocks.addAll(Arrays.asList(sectionFiles));
        }

        String flagStrings = prop.get("flags");
        if (StrUtil.isNotBlank(flagStrings)) {
            this.flags.addAll(StrUtil.splitToSet(flagStrings, ","));
        }
    }


    private static final String TEMPLATE_SEPARATOR = "_";
    private static final String TEMPLATE_H5_SUFFIX = "_h5.html";

    /**
     * 找出可以用来渲染的 html 模板
     *
     * @param template
     * @return
     */
    public String matchView(String template, boolean isMobileBrowser) {

        if (isMobileBrowser) {
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
        if (isMobileBrowser) {
            String h5Template = matchH5Template(template);
            if (h5Template != null) {
                return h5Template;
            }
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


    private List<BlockContainerDef> containerDefs;

    /**
     * 获取模板支持设计的板块容器
     *
     * @return
     */
    public synchronized List<BlockContainerDef> getContainerDefs() {
        if (Jboot.isDevMode()) {
            return readContainerDefs();
        }

        if (containerDefs == null) {
            containerDefs = readContainerDefs();
        }

        return containerDefs;
    }


    private List<BlockContainerDef> readContainerDefs() {
        Set<BlockContainerDef> allContainerDefs = new HashSet<>();
        for (String htmlFile : htmls) {
            allContainerDefs.addAll(TemplateUtil.readContainerDefs(new File(getAbsolutePathFile(), htmlFile)));
        }
        return new ArrayList<>(allContainerDefs);
    }

    private List<HtmlBlock> blockHtmls;

    /**
     * 获取版本自带的 板块 信息
     *
     * @return
     */
    public List<HtmlBlock> getHtmlBlocks() {
        if (Jboot.isDevMode()) {
            return readHtmlBlockHtml();
        }

        if (blockHtmls == null) {
            blockHtmls = readHtmlBlockHtml();
        }

        return blockHtmls;
    }


    private List<HtmlBlock> readHtmlBlockHtml() {
        List<HtmlBlock> blockInfos = new ArrayList<>();
        for (String blockFileName : blocks) {
            HtmlBlock blockHtml = new HtmlBlock();

            //remove "block_"  and ".html"
            blockHtml.setId(blockFileName.substring(6, blockFileName.length() - 5));

            //fill blockHtml attrs
            TemplateUtil.readAndFillHtmlBlock(new File(getAbsolutePathFile(), blockFileName), blockHtml);

            blockInfos.add(blockHtml);
        }
        return blockInfos;
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

    public String getScreenshot() {
        return screenshot;
    }

    public void setScreenshot(String screenshot) {
        this.screenshot = screenshot;
    }

    public String getRelativePath() {
        return relativePath;
    }

    public void setRelativePath(String relativePath) {
        this.relativePath = relativePath;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }


    public String buildRelativePath(String html) {
        return relativePath + "/" + html;
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

    /**
     * 卸载模板
     */
    public void uninstall() {
        StringBuilder newFileName = new StringBuilder(PathKit.getWebRootPath());
        newFileName.append(File.separator);
        newFileName.append("templates");
        newFileName.append(File.separator);
        newFileName.append("dockers");
        File templateRootPath = new File(newFileName.toString());

        File delPath = findInstallPath(templateRootPath, getAbsolutePathFile());
        FileUtils.deleteQuietly(delPath);
    }


    private File findInstallPath(File templateRootPath, File file) {
        File parent = file.getParentFile();
        if (parent.getAbsolutePath().equals(templateRootPath.getAbsolutePath())
                || parent.getAbsolutePath().equals(templateRootPath.getParentFile().getAbsolutePath())) {
            return file;
        }

        return findInstallPath(templateRootPath, file.getParentFile());
    }

    public File getAbsolutePathFile() {
        return new File(PathKit.getWebRootPath(), relativePath);
    }


    public void addNewHtml(String htmlFileName) {
        htmls.add(htmlFileName);
    }

    public void deleteHtml(String htmlFileName) {
        htmls.remove(htmlFileName);
    }

    public Set<String> getHtmls() {
        return htmls;
    }
}
