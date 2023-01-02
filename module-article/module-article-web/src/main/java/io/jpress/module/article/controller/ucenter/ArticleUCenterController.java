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
package io.jpress.module.article.controller.ucenter;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.ArrayUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.commons.utils.JsoupUtils;
import io.jpress.commons.wordsfilter.WordFilterUtil;
import io.jpress.core.menu.annotation.UCenterMenu;
import io.jpress.model.User;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.web.base.UcenterControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping(value = "/ucenter/article", viewPath = "/WEB-INF/views/ucenter/")
public class ArticleUCenterController extends UcenterControllerBase {

    @Inject
    private ArticleService articleService;

    @Inject
    private ArticleCategoryService categoryService;

    @Inject
    private ArticleCommentService commentService;


    @UCenterMenu(text = "文章列表", groupId = "article", order = 0)
    public void index() {

        User loginedUser = getLoginedUser();
        Page<Article> page = articleService._paginateByUserId(getPagePara(), 10, loginedUser.getId());
        setAttr("page", page);

        render("article/article_list.html");
    }

    public void doDel() {

        Long id = getIdPara();
        if (id == null) {
            renderFailJson();
            return;
        }

        Article article = articleService.findById(id);
        if (article == null) {
            renderFailJson();
            return;
        }

        if (notLoginedUserModel(article)) {
            renderJson(Ret.fail().set("message", "非法操作"));
            return;
        }

        renderJson(articleService.deleteById(id) ? OK : FAIL);
    }

    @UCenterMenu(text = "投稿", groupId = "article", order = 1)
    public void write() {

        int articleId = getParaToInt(0, 0);

        Article article = null;
        if (articleId > 0) {

            article = articleService.findById(articleId);
            if (article == null) {
                renderError(404);
                return;
            }

            //用户投稿，不能编辑已经审核通过的文章
            if (article.isNormal()) {
                renderError(404);
                return;
            }

            //不是自己的文章
            if (notLoginedUserModel(article)) {
                renderError(404);
                return;
            }

            setAttr("article", article);
        }

        String editMode = article == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : article.getEditMode();
        setAttr("editMode", JPressConsts.EDIT_MODE_MARKDOWN.equals(editMode)
                ? JPressConsts.EDIT_MODE_MARKDOWN
                : JPressConsts.EDIT_MODE_HTML);


        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        List<ArticleCategory> tags = categoryService.findTagListByArticleId(articleId);//.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
        setAttr("tags", tags);

        Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
        flagCheck(categories, categoryIds);

        render("article/article_write.html");
    }

    private void flagCheck(List<ArticleCategory> categories, Long... checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (ArticleCategory category : categories) {
            for (Long id : checkIds) {
                if (id != null && id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
    }


    @EmptyValidate({
            @Form(name = "article.title", message = "标题不能为空"),
            @Form(name = "article.content", message = "文章内容不能为空")
    })
    public void doWriteSave() {

        Article article = getModel(Article.class, "article");
        article.keep("id", "title", "content", "slug", "edit_mode", "summary", "thumbnail", "meta_title", "meta_keywords", "meta_description");
        article.setContent(getCleanedOriginalPara("article.content"));

        article.setUserId(getLoginedUser().getId());

        if (!getLoginedUser().isStatusOk()) {
            renderJson(Ret.fail().set("message", "当前脏话未激活，无法投稿。"));
            return;
        }


        if (article.getId() != null && notLoginedUserModel(article)) {
            renderJson(Ret.fail().set("message", "非法操作"));
            return;
        }

        if (!validateSlug(article)) {
            renderJson(Ret.fail("message", "固定连接不能以数字结尾"));
            return;
        }

        if (StrUtil.isNotBlank(article.getSlug())) {
            Article existArticle = articleService.findFirstBySlug(article.getSlug());
            if (existArticle != null && !existArticle.getId().equals(article.getId())) {
                renderJson(Ret.fail("message", "该固定链接已经存在"));
                return;
            }
        }

        if (WordFilterUtil.isMatchedFilterWords(article.getText())) {
            renderJson(Ret.fail().set("message", "投稿内容包含非法字符。"));
            return;
        }

        //只保留的基本的html，其他的html比如<script>将会被清除
        if (!article._isMarkdownMode()) {
            String content = JsoupUtils.clean(article.getContent());
            article.setContent(content);
        }

        article.setUserId(getLoginedUser().getId());
        article.setStatus(Article.STATUS_DRAFT);
        long id = (long) articleService.saveOrUpdate(article);

        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));
        Long[] allIds = ArrayUtils.addAll(categoryIds, tagIds);

        articleService.doUpdateCategorys(id, allIds);

        if (allIds != null && allIds.length > 0) {
            for (Long categoryId : allIds) {
                categoryService.doUpdateArticleCount(categoryId);
            }
        }

        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }

    private boolean validateSlug(Model<?> model) {
        String slug = model.get("slug");
        if (slug == null) {
            return true;
        }

        if (!slug.contains("-")) {
            return true;
        }

        return !StrUtil.isNumeric(slug.substring(slug.lastIndexOf("-") + 1));
    }

    private Long[] getTagIds(String[] tags) {
        if (ArrayUtil.isNullOrEmpty(tags)) {
            return null;
        }

        Set<String> tagset = new HashSet<>();
        for (String tag : tags) {
            tagset.addAll(StrUtil.splitToSet(tag, ","));
        }

        List<ArticleCategory> categories = categoryService.doCreateOrFindByTagString(tagset.toArray(new String[0]));
        long[] ids = categories.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }


    @UCenterMenu(text = "文章评论", groupId = "comment", order = 0, icon = "<i class=\"fas fa-comment\"></i>")
    public void comment() {
        Page<ArticleComment> page = commentService._paginateByUserId(getPagePara(), 10, getLoginedUser().getId());
        setAttr("page", page);
        render("article/comment_list.html");
    }


    public void doCommentDel() {

        ArticleComment comment = commentService.findById(getPara("id"));
        if (comment == null) {
            renderFailJson();
            return;
        }


        if (notLoginedUserModel(comment)) {
            renderFailJson("非法操作");
            return;
        }

        renderJson(commentService.delete(comment) ? OK : FAIL);
    }

}