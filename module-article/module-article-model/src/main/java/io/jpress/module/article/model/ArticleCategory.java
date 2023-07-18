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
package io.jpress.module.article.model;

import io.jboot.db.annotation.Table;
import io.jboot.utils.StrUtil;
import io.jboot.web.json.JsonIgnore;
import io.jpress.commons.utils.UrlUtils;
import io.jpress.module.article.model.base.BaseArticleCategory;

/**
 * 此类用来定义 文章的类型，包含了：分类、标签和专题
 * 分类和标签只是对文章的逻辑归类
 * 专题可以用于知识付费
 * <p>
 * 标签和专题  只能有一个层级，分类可以有多个层级
 */
@Table(tableName = "article_category", primaryKey = "id")
public class ArticleCategory extends BaseArticleCategory<ArticleCategory> {

    /**
     * 普通的分类
     * 分类可以有多个层级
     */
    public static final String TYPE_CATEGORY = "category";

    /**
     * 标签
     * 标签只有一个层级
     */
    public static final String TYPE_TAG = "tag";


    /**
     * 用户自建分类，目前暂不考虑这部分的实现
     */
    public static final String TYPE_USER_CATEGORY = "user_category";


    @JsonIgnore
    public boolean isTag() {
        return TYPE_TAG.equals(getType());
    }

    @JsonIgnore
    public String getUrl() {
        String prefix = TYPE_CATEGORY.equals(getType()) ? "/category/" : "/tag/";
        return UrlUtils.getUrl(prefix, getSlugOrId());
    }

    private String getSlugOrId() {
        String slug = getSlug();
        return StrUtil.isNotBlank(slug) ? slug : getId().toString();
    }


    public String getUrlWithPageNumber(int pageNumber) {
        if (pageNumber <= 1) {
            return getUrl();
        }

        String prefix = TYPE_CATEGORY.equals(getType()) ? "/category/" : "/tag/";
        return UrlUtils.getUrl(prefix, getSlug(), "-", pageNumber);
    }

    @JsonIgnore
    public String getHtmlView() {
        return StrUtil.isBlank(getStyle()) ? "artlist.html" : "artlist_" + getStyle().trim() + ".html";
    }

}
