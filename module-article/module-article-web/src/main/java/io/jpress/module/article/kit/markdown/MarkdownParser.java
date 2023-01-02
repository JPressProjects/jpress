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
package io.jpress.module.article.kit.markdown;

import com.jfinal.log.Log;
import io.jboot.utils.FileUtil;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.MarkdownUtils;
import io.jpress.module.article.model.Article;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ryan Wang（i@ryanc.cc）
 * @version V1.0
 * @Package io.jpress.module.article.kit.markdown
 */
public class MarkdownParser {

    private static final Log log = Log.getLog(MarkdownParser.class);
    private Article article = new Article();

    /**
     * markdown文本
     */
    private String markdown = "";

    /**
     * markdown文档的元数据
     */
    private Map<String, List<String>> datas = new HashMap<>();

    /**
     * 单个节点的值
     */
    private List<String> elementValue = new ArrayList<>();

    /**
     * 日期格式化
     */
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取文章
     *
     * @return Article
     */
    public Article getArticle() throws ParseException {
        article.setContent(markdown);
        article.setEditMode(JPressConsts.EDIT_MODE_MARKDOWN);
        article.setStatus(Article.STATUS_NORMAL);

        for (String key : datas.keySet()) {
            elementValue = datas.get(key);
            for (String ele : elementValue) {
                if ("title".equals(key)) {
                    article.setTitle(ele);
                } else if ("date".equals(key)) {
                    article.setCreated(sdf.parse(ele));
                } else if ("updated".equals(key)) {
                    article.setModified(sdf.parse(ele));
                }
            }
        }
        return article;
    }

    public String[] getCategories() throws ParseException {
        String[] resp = null;
        for (String key : datas.keySet()) {
            elementValue = datas.get(key);
            for (String ele : elementValue) {
                if ("categories".equals(key)) {
                    ele = ele.replaceAll("\\[","");
                    ele = ele.replaceAll("\\]","");
                    ele = ele.replaceAll("\"","");
                    resp = ele.split(",");
                }
            }
        }
        return resp;
    }

    /**
     * 解析markdown文档
     *
     * @param mdFile mdFile
     */
    public void parse(File mdFile) {
        try {
            markdown = FileUtil.readString(mdFile);
            datas = MarkdownUtils.getFrontMatter(markdown);
        } catch (Exception e) {
            log.warn("ConfigParser parser exception", e);
        }
    }
}
