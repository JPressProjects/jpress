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
import io.jpress.JPressOptions;
import io.jpress.commons.layer.SortModel;
import io.jpress.module.article.model.base.BaseArticleCategory;

import java.util.ArrayList;
import java.util.List;

/**
 * 此类用来定义 文章的类型，包含了：分类、标签和专题
 * 分类和标签只是对文章的逻辑归类
 * 专题可以用于知识付费
 * <p>
 * 标签和专题  只能有一个层级，分类可以有多个层级
 */
@Table(tableName = "article_category", primaryKey = "id")
public class ArticleCategory extends BaseArticleCategory<ArticleCategory> implements SortModel {

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


    public boolean isTag() {
        return TYPE_TAG.equals(getType());
    }

    private int layerNumber;
    private SortModel parent;
    private List<SortModel> childs;


    /**
     * 是否是顶级菜单
     *
     * @return
     */
    @Override
    public boolean isTop() {
        return getPid() != null && getPid() == 0;
    }

    @Override
    public Long getParentId() {
        return getPid();
    }

    @Override
    public void setParent(SortModel parent) {
        this.parent = parent;
    }

    @Override
    public SortModel getParent() {
        return parent;
    }

    @Override
    public void setChilds(List childs) {
        this.childs = childs;
    }

    @Override
    public void addChild(SortModel child) {
        if (childs == null) {
            childs = new ArrayList<>();
        }
        childs.add(child);
    }

    @Override
    public List getChilds() {
        return childs;
    }

    public boolean hasChild() {
        return childs != null && !childs.isEmpty();
    }

    @Override
    public void setLayerNumber(int layerNumber) {
        this.layerNumber = layerNumber;
    }

    @Override
    public int getLayerNumber() {
        return layerNumber;
    }

    public String getLayerString() {

        if (layerNumber == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < layerNumber; i++) {
            if (i == 0) {
                sb.append("|—");
            } else {
                sb.append("—");
            }
        }

        return sb.toString();
    }


    public boolean isMyChild(long id) {
        if (childs == null || childs.isEmpty()) {
            return false;
        }

        return isMyChild(childs, id);
    }

    private boolean isMyChild(List<SortModel> categories, long id) {
        for (SortModel category : categories) {
            if (category.getId() == id) {
                return true;
            }

            if (category.getChilds() != null) {
                boolean isChild = isMyChild(category.getChilds(), id);
                if (isChild) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getUrl() {
        switch (getType()) {
            case TYPE_CATEGORY:
                return JFinal.me().getContextPath() + "/article/category/" + getSlug() + JPressOptions.getAppUrlSuffix();
            case TYPE_TAG:
                return JFinal.me().getContextPath() + "/article/tag/" + getSlug() + JPressOptions.getAppUrlSuffix();
            default:
                return "";
        }
    }


    public String getHtmlView() {
        return StrUtil.isBlank(getStyle()) ? "artlist.html" : "artlist_" + getStyle().trim() + ".html";
    }

}
