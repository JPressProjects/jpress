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
package io.jpress.module.article.model;

import com.jfinal.core.JFinal;
import io.jboot.db.annotation.Table;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.commons.utils.JsoupUtils;
import io.jpress.commons.utils.MarkdownUtils;
import io.jpress.module.article.model.base.BaseArticle;

import java.util.ArrayList;
import java.util.List;

/**
 * Generated by Jboot.
 */
@Table(tableName = "article", primaryKey = "id")
public class Article extends BaseArticle<Article> {

    public static final String STATUS_NORMAL = "normal";
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_TRASH = "trash";


    public boolean isNormal() {
        return STATUS_NORMAL.equals(getStatus());
    }

    public boolean isDraft() {
        return STATUS_DRAFT.equals(getStatus());
    }

    public boolean isTrash() {
        return STATUS_TRASH.equals(getStatus());
    }

    public String getHtmlView() {
        return StrUtil.isBlank(getStyle()) ? "article.html" : "article_" + getStyle().trim() + ".html";
    }

    /**
     * 用于渲染html模板，是否高亮
     * @return
     */
    public boolean isActive() {
        Article currentArticle = JbootControllerContext.get().getAttr("article");

        //当前页面并不是文章详情页面
        if (currentArticle == null || currentArticle.getId() == null) {
            return false;
        }

        return currentArticle.getId().equals(getId());
    }

    public String getUrl() {
        String link = getLinkTo();
        if (StrUtil.isNotBlank(link)) {
            return link;
        }

        if (StrUtil.isBlank(getSlug())) {
            return JFinal.me().getContextPath() + "/article/" + getId() + JPressOptions.getAppUrlSuffix();
        } else {
            return JFinal.me().getContextPath() + "/article/" + getSlug() + JPressOptions.getAppUrlSuffix();
        }
    }

    public boolean isCommentEnable() {
        Boolean cs = getCommentStatus();
        return cs != null && cs == true;
    }

    public String getText() {
        return JsoupUtils.getText(getContent());
    }

    @Override
    public String getContent() {
        String content = super.getContent();
        if (_isMarkdownMode()) {
            content = MarkdownUtils.toHtml(content);
            content = JsoupUtils.clean(content);
        }
        return JsoupUtils.makeImageSrcToAbsolutePath(content, JPressOptions.getResDomain());
    }


    public boolean _isMarkdownMode() {
        return JPressConsts.EDIT_MODE_MARKDOWN.equals(getEditMode());
    }


    public String _getEditContent() {

        String originalContent = super.getContent();
        if (StrUtil.isBlank(originalContent) || _isMarkdownMode()) {
            return originalContent;
        }

        //ckeditor 编辑器有个bug，自动把 &lt; 转化为 < 和 把 &gt; 转化为 >
        //因此，此处需要 把 "&lt;" 替换为 "&amp;lt;" 和 把 "&gt;" 替换为 "&amp;gt;"
        //方案：http://komlenic.com/246/encoding-entities-to-work-with-ckeditor-3/
        return originalContent.replace("&lt;", "&amp;lt;")
                .replace("&gt;", "&amp;gt;");

    }


    /**
     * 获取文章的所有图片
     *
     * @return
     */
    public List<String> getImages() {
        return JsoupUtils.getImageSrcs(getContent());
    }

    /**
     * 获取前面几张图片
     *
     * @param count
     * @return
     */
    public List<String> getImages(int count) {
        List<String> list = getImages();
        if (list == null || list.size() <= count) {
            return list;
        }

        List<String> newList = new ArrayList<>();
        for (int i = 0; 0 < count; i++) newList.add(list.get(i));
        return newList;
    }

    public boolean hasImage() {
        return getFirstImage() != null;
    }

    public boolean hasVideo() {
        return getFirstVideo() != null;
    }

    public boolean hasAudio() {
        return getFirstAudio() != null;
    }

    public String getFirstImage() {
        return JsoupUtils.getFirstImageSrc(getContent());
    }

    public String getFirstVideo() {
        return JsoupUtils.getFirstVideoSrc(getContent());
    }

    public String getFirstAudio() {
        return JsoupUtils.getFirstAudioSrc(getContent());
    }

    public String getShowImage() {
        String thumbnail = getThumbnail();
        return StrUtil.isNotBlank(thumbnail) ? thumbnail : getFirstImage();
    }

    public String getHighlightContent() {
        return getStr("highlightContent");
    }

    public void setHighlightContent(String highlightContent) {
        put("highlightContent", highlightContent);
    }

    public String getHighlightTitle() {
        return getStr("highlightTitle");
    }

    public void setHighlightTitle(String highlightTitle) {
        put("highlightTitle", highlightTitle);
    }

    @Override
    public boolean save() {
        CommonsUtils.escapeHtmlForAllAttrs(this, "content");
        return super.save();
    }

    @Override
    public boolean update() {
        CommonsUtils.escapeHtmlForAllAttrs(this, "content");
        return super.update();
    }

    @Override
    public Long getOrderNumber() {
        Long order = super.getOrderNumber();
        return order == null ? 0 : order;
    }
}
