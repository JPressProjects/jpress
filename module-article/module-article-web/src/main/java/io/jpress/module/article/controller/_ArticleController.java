package io.jpress.module.article.controller;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.Jboot;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.controller.validate.EmptyValidate;
import io.jboot.web.controller.validate.Form;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.model.User;
import io.jpress.module.article.kits.ArticleModuleKit;
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

import javax.inject.Inject;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.admin
 */
@RequestMapping("/admin/article")
public class _ArticleController extends AdminControllerBase {

    @Inject
    private ArticleService articleService;
    @Inject
    private ArticleCategoryService categoryService;
    @Inject
    private ArticleCommentService commentService;

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


        int articleId = getParaToInt(0, 0);

        if (articleId > 0) {
            Article article = articleService.findById(articleId);
            if (article == null) {
                renderError(404);
                return;
            }
            setAttr("article", article);

            List<ArticleCategory> tags = categoryService.findListByArticleId(articleId, ArticleCategory.TYPE_TAG);
            setAttr("tags", tags);

            Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
            flagCheck(categories, categoryIds);
        }

        initStylesAttr("article_");

        render("article/article_write.html");
    }

    private void initStylesAttr(String prefix) {
        List<String> styles = TemplateManager.me().getCurrentTemplate().getSupportStyles(prefix);
        if (styles != null && !styles.isEmpty()) {
            setAttr("styles", styles);
        }
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

        long id = articleService.doGetIdBySaveOrUpdateAction(article);

        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));

        Long[] allIds = ArrayUtils.addAll(categoryIds, tagIds);

        articleService.doUpdateCategorys(id, allIds);

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


    @EmptyValidate(@Form(name = "article.title"))
    public void save(Article article) {
        boolean success = articleService.saveOrUpdate(article);
        render(success ? Ret.ok() : Ret.fail());
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

        }

        initStylesAttr("artlist_");
        render("article/tag_list.html");
    }

    public void doAddCategoryToMenu() {

        Long id = getIdPara();

        ArticleCategory category = categoryService.findById(id);
        if (category == null) {
            renderJson(Ret.fail().set("message", "该数据已经被删除"));
            return;
        }

        Menu menu = new Menu();
        menu.setPid(0l);
        menu.setUrl(ArticleModuleKit.getCategoryUrl(category));
        menu.setText(category.getTitle());
        menu.setType(io.jpress.model.Menu.TYPE_MAIN);
        menu.setRelativeTable("article_category");
        menu.setRelativeId(id);
        menu.setOrderNumber(9);

        MenuService menuService = Jboot.bean(MenuService.class);
        menuService.saveOrUpdate(menu);

        renderJson(Ret.ok());
    }


    @EmptyValidate({
            @Form(name = "category.title", message = "分类名称不能为空"),
            @Form(name = "category.slug", message = "slug 不能为空")
    })
    public void doCategorySave() {
        ArticleCategory category = getModel(ArticleCategory.class, "category");
        if (!validateSlug(category)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }

        categoryService.saveOrUpdate(category);
        renderJson(Ret.ok());
    }

    public void doCategoryDel() {
        categoryService.deleteById(getIdPara());
        renderJson(Ret.ok());
    }


    @AdminMenu(text = "评论", groupId = "article", order = 5)
    public void comment() {

        String status = getPara("status");
        String key = getPara("keyword");

        Page<ArticleComment> page =
                StrUtils.isBlank(status)
                        ? commentService._paginateWithoutTrash(getPagePara(), 10, key)
                        : commentService._paginateByStatus(getPagePara(), 10, key, status);

        setAttr("page", page);

        int unauditedCount = commentService.findCountByStatus(ArticleComment.STATUS_UNAUDITED);
        int trashCount = commentService.findCountByStatus(ArticleComment.STATUS_TRASH);
        int normalCount = commentService.findCountByStatus(ArticleComment.STATUS_NORMAL);

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
        renderJson(Ret.ok());
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

        commentService.saveOrUpdate(comment);
        renderJson(Ret.ok());
    }


    /**
     * 删除评论
     */
    public void doCommentDel() {
        commentService.deleteById(getIdPara());
        renderJson(Ret.ok());
    }


    /**
     * 批量删除评论
     */
    public void doCommentDelByIds() {
        String ids = getPara("ids");
        if (StrUtils.isBlank(ids)) {
            renderJson(Ret.fail());
            return;
        }

        Set<String> idsSet = StrUtils.splitToSet(ids, ",");
        if (idsSet == null || idsSet.isEmpty()) {
            renderJson(Ret.fail());
            return;
        }
        render(commentService.deleteByIds(idsSet.toArray()) ? Ret.ok() : Ret.fail());
    }


    /**
     * 批量审核评论
     */
    public void doCommentAuditByIds() {
        String ids = getPara("ids");
        if (StrUtils.isBlank(ids)) {
            renderJson(Ret.fail());
            return;
        }

        Set<String> idsSet = StrUtils.splitToSet(ids, ",");
        if (idsSet == null || idsSet.isEmpty()) {
            renderJson(Ret.fail());
            return;
        }
        render(commentService.batchChangeStatusByIds(ArticleComment.STATUS_NORMAL, idsSet.toArray()) ? Ret.ok() : Ret.fail());
    }


    /**
     * 修改评论状态
     */
    public void doCommentStatusChange(Long id, String status) {
        render(commentService.doChangeStatus(id, status) ? Ret.ok() : Ret.fail());
    }

    @AdminMenu(text = "设置", groupId = "article", order = 6)
    public void setting() {
        render("article/setting.html");
    }


    public void doDel() {
        Long id = getIdPara();
        render(articleService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    public void doDelByIds() {
        String ids = getPara("ids");
        if (StrUtils.isBlank(ids)) {
            renderJson(Ret.fail());
            return;
        }

        Set<String> idsSet = StrUtils.splitToSet(ids, ",");
        if (idsSet == null || idsSet.isEmpty()) {
            renderJson(Ret.fail());
            return;
        }
        render(articleService.deleteByIds(idsSet.toArray()) ? Ret.ok() : Ret.fail());
    }


    public void doTrash() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_TRASH) ? Ret.ok() : Ret.fail());
    }

    public void doDraft() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_DRAFT) ? Ret.ok() : Ret.fail());
    }

    public void doNormal() {
        Long id = getIdPara();
        render(articleService.doChangeStatus(id, Article.STATUS_NORMAL) ? Ret.ok() : Ret.fail());
    }

}
