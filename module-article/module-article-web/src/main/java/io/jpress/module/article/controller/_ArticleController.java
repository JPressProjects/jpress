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
package io.jpress.module.article.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.model.User;
import io.jpress.module.article.ArticleFields;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.admin
 */
@RequestMapping(value = "/admin/article", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ArticleController extends AdminControllerBase {

    @Inject
    private ArticleService articleService;
    @Inject
    private ArticleCategoryService categoryService;
    @Inject
    private ArticleCommentService commentService;
    @Inject
    private MenuService menuService;

    @AdminMenu(text = "文章管理", groupId = "article", order = 0)
    public void index() {

        String status = getPara("status");
        String title = getPara("title");
        Long categoryId = getParaToLong("categoryId");

        Page<Article> page =
                StringUtils.isBlank(status)
                        ? articleService._paginateWithoutTrash(getPagePara(), 10, title, categoryId)
                        : articleService._paginateByStatus(getPagePara(), 10, title, categoryId, status);

        setAttr("page", page);

        int draftCount = articleService.findCountByStatus(Article.STATUS_DRAFT);
        int trashCount = articleService.findCountByStatus(Article.STATUS_TRASH);
        int normalCount = articleService.findCountByStatus(Article.STATUS_NORMAL);
        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);


        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        flagCheck(categories, categoryId);

        render("article/article_list.html");
    }


    @AdminMenu(text = "写文章", groupId = "article", order = 1)
    public void write() {

        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        setAttr("fields", ArticleFields.me());


        int articleId = getParaToInt(0, 0);

        Article article = null;
        if (articleId > 0) {
            article = articleService.findById(articleId);
            if (article == null) {
                renderError(404);
                return;
            }
            setAttr("article", article);

            List<ArticleCategory> tags = categoryService.findTagListByArticleId(articleId);
            setAttr("tags", tags);

            Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
            flagCheck(categories, categoryIds);
        }


        String editMode = article == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : article.getEditMode();
        setAttr("editMode", JPressConsts.EDIT_MODE_MARKDOWN.equals(editMode)
                ? JPressConsts.EDIT_MODE_MARKDOWN
                : JPressConsts.EDIT_MODE_HTML);

        initStylesAttr("article_");

        render("article/article_write.html");
    }

    @EmptyValidate({
            @Form(name = "id", message = "文章ID不能为空"),
            @Form(name = "mode", message = "文章编辑模式不能为空")
    })
    public void doChangeEditMode() {
        Long id = getParaToLong("id");
        String mode = getPara("mode");

        Article article = articleService.findById(id);
        if (article == null) {
            renderFailJson();
            return;
        }

        article.setEditMode(mode);
        articleService.update(article);
        renderOkJson();
    }

    private void initStylesAttr(String prefix) {
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null) {
            return;
        }
        setAttr("flags", template.getFlags());
        List<String> styles = template.getSupportStyles(prefix);
        setAttr("styles", styles);
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

        if (!validateSlug(article)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }


        if (StrUtil.isNotBlank(article.getSlug())) {
            Article slugArticle = articleService.findFirstBySlug(article.getSlug());
            if (slugArticle != null && slugArticle.getId().equals(article.getId()) == false) {
                renderJson(Ret.fail("message", "该slug已经存在"));
                return;
            }
        }

        if (article.getOrderNumber() == null) {
            article.setOrderNumber(0);
        }

        long id = (long) articleService.saveOrUpdate(article);
        articleService.doUpdateCommentCount(id);

        setAttr("articleId", id);
        setAttr("article", article);


        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));

        Long[] allIds = ArrayUtils.addAll(categoryIds, tagIds);

        articleService.doUpdateCategorys(id, allIds);

        if (allIds != null && allIds.length > 0) {
            for (Long categoryId : allIds) {
                categoryService.updateCount(categoryId);
            }
        }

        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }

    private Long[] getTagIds(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        List<ArticleCategory> categories = categoryService.doNewOrFindByTagString(tags);
        long[] ids = categories.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }


    @AdminMenu(text = "分类", groupId = "article", order = 2)
    public void category() {
        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);
        int id = getParaToInt(0, 0);
        if (id > 0) {
            for (ArticleCategory category : categories) {
                if (category.getId() == id) {
                    setAttr("category", category);
                    setAttr("isDisplayInMenu", menuService.findFirstByRelatives("article_category", id) != null);
                }
            }
        }
        initStylesAttr("artlist_");
        render("article/category_list.html");
    }


    @AdminMenu(text = "标签", groupId = "article", order = 4)
    public void tag() {
        Page<ArticleCategory> page = categoryService.paginateByType(getPagePara(), 10, ArticleCategory.TYPE_TAG);
        setAttr("page", page);

        int id = getParaToInt(0, 0);
        if (id > 0) {
            setAttr("category", categoryService.findById(id));
            setAttr("isDisplayInMenu", menuService.findFirstByRelatives("article_category", id) != null);
        }

        initStylesAttr("artlist_");
        render("article/tag_list.html");
    }


    @EmptyValidate({
            @Form(name = "category.title", message = "分类名称不能为空"),
            @Form(name = "category.slug", message = "slug 不能为空")
    })
    public void doCategorySave() {
        ArticleCategory category = getModel(ArticleCategory.class, "category");
        saveCategory(category);
    }

    private void saveCategory(ArticleCategory category) {
        if (!validateSlug(category)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }

        Object id = categoryService.saveOrUpdate(category);
        categoryService.updateCount(category.getId());

        Menu displayMenu = menuService.findFirstByRelatives("article_category", id);
        Boolean isDisplayInMenu = getParaToBoolean("displayInMenu");
        if (isDisplayInMenu != null && isDisplayInMenu) {
            if (displayMenu == null) {
                displayMenu = new Menu();
            }

            displayMenu.setUrl(category.getUrl());
            displayMenu.setText(category.getTitle());
            displayMenu.setType(Menu.TYPE_MAIN);
            displayMenu.setOrderNumber(category.getOrderNumber());
            displayMenu.setRelativeTable("article_category");
            displayMenu.setRelativeId((Long) id);

            if (displayMenu.getPid() == null) {
                displayMenu.setPid(0l);
            }

            if (displayMenu.getOrderNumber() == null) {
                displayMenu.setOrderNumber(99);
            }

            menuService.saveOrUpdate(displayMenu);
        } else if (displayMenu != null) {
            menuService.delete(displayMenu);
        }

        renderOkJson();
    }


    @EmptyValidate({
            @Form(name = "category.title", message = "标签名称不能为空"),
    })
    public void doTagSave() {

        ArticleCategory tag = getModel(ArticleCategory.class, "category");

        String slug = tag.getTitle().contains(".")
                ? tag.getTitle().replace(".", "_")
                : tag.getTitle();

        //新增 tag
        if (tag.getId() == null) {
            ArticleCategory indbTag = categoryService.findFirstByTypeAndSlug(ArticleCategory.TYPE_TAG, slug);
            if (indbTag != null) {
                renderJson(Ret.fail().set("message", "该标签已经存在，不能新增。"));
                return;
            }
        }

        tag.setSlug(slug);
        saveCategory(tag);
    }

    public void doCategoryDel() {
        categoryService.deleteById(getIdPara());
        renderOkJson();
    }


    @AdminMenu(text = "评论", groupId = "article", order = 5)
    public void comment() {

        String status = getPara("status");
        String key = getPara("keyword");
        Long articleId = getParaToLong("articleId");

        Page<ArticleComment> page =
                StrUtil.isBlank(status)
                        ? commentService._paginateWithoutTrash(getPagePara(), 10, articleId, key)
                        : commentService._paginateByStatus(getPagePara(), 10, articleId, key, status);

        setAttr("page", page);

        long unauditedCount = commentService.findCountByStatus(ArticleComment.STATUS_UNAUDITED);
        long trashCount = commentService.findCountByStatus(ArticleComment.STATUS_TRASH);
        long normalCount = commentService.findCountByStatus(ArticleComment.STATUS_NORMAL);

        setAttr("unauditedCount", unauditedCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", unauditedCount + trashCount + normalCount);

        render("article/comment_list.html");
    }


    /**
     * 评论回复 页面
     */
    public void commentReply() {
        long id = getIdPara();
        ArticleComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("article/comment_reply.html");
    }

    /**
     * 评论编辑 页面
     */
    public void commentEdit() {
        long id = getIdPara();
        ArticleComment comment = commentService.findById(id);
        setAttr("comment", comment);
        render("article/comment_edit.html");
    }

    public void doCommentSave() {
        ArticleComment comment = getBean(ArticleComment.class, "comment");
        commentService.saveOrUpdate(comment);
        renderOkJson();
    }


    /**
     * 进行评论回复
     */
    public void doCommentReply(String content, Long articleId, Long pid) {
        User user = getLoginedUser();

        ArticleComment comment = new ArticleComment();
        comment.setContent(content);
        comment.setUserId(user.getId());
        comment.setStatus(ArticleComment.STATUS_NORMAL);
        comment.setArticleId(articleId);
        comment.setPid(pid);

        commentService.save(comment);
        renderOkJson();
    }


    /**
     * 删除评论
     */
    public void doCommentDel() {
        Long id = getParaToLong("id");
        commentService.deleteById(id);
        renderOkJson();
    }


    /**
     * 批量删除评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doCommentDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 批量审核评论
     */
    @EmptyValidate(@Form(name = "ids"))
    public void doCommentAuditByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(commentService.batchChangeStatusByIds(ArticleComment.STATUS_NORMAL, idsSet.toArray()) ? OK : FAIL);
    }


    /**
     * 修改评论状态
     */
    public void doCommentStatusChange(Long id, String status) {
        render(commentService.doChangeStatus(id, status) ? OK : FAIL);
    }

    @AdminMenu(text = "设置", groupId = "article", order = 6)
    public void setting() {
        render("article/setting.html");
    }


    public void doDel() {
        Long id = getIdPara();
        render(articleService.deleteById(id) ? OK : FAIL);
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(articleService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }


    public void doTrash() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_TRASH) ? OK : FAIL);
    }

    public void doDraft() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_DRAFT) ? OK : FAIL);
    }

    public void doNormal() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_NORMAL) ? OK : FAIL);
    }

}
