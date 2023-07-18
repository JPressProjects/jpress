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
package io.jpress.module.article.controller.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.db.model.Columns;
import io.jboot.utils.DateUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.support.smartfield.SmartField;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.Menu;
import io.jpress.module.article.ArticleFields;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.model.ArticleMetaInfo;
import io.jpress.module.article.model.ArticleMetaRecord;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.article.service.ArticleMetaInfoService;
import io.jpress.module.article.service.ArticleMetaRecordService;
import io.jpress.module.article.service.ArticleService;
import io.jpress.service.MenuService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

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
    private MenuService menuService;

    @Inject
    private ArticleMetaInfoService metaInfoService;

    @Inject
    private ArticleMetaRecordService metaRecordService;


    @AdminMenu(text = "文章管理", groupId = "article", order = 0)
    public void list() {

        String status = getPara("status");
        String title = getPara("title");
        Long categoryId = getParaToLong("categoryId");
        Long userId = getParaToLong("userId");

        Date startDate = null;
        Date endDate = null;

        String dateRange = getPara("dateRange");
        if (StrUtil.isNotBlank(dateRange) && dateRange.contains("~")) {
            String[] startAndEndDateStrings = dateRange.split("~");
            startDate = DateUtil.parseDate(startAndEndDateStrings[0].trim());
            endDate = DateUtil.parseDate(startAndEndDateStrings[1].trim());
        }

        Columns columns = Columns.create("mapping.category_id", categoryId);

        if (startDate != null && endDate != null) {
            columns.gt("article.created", startDate);
            columns.le("article.created", DateUtil.addDays(endDate, 1));
        }

        columns.eq("article.user_id", userId);
        columns.likeAppendPercent("article.title", title);

        if (StrUtil.isBlank(status)) {
            columns.ne("article.status", Article.STATUS_TRASH);
        } else {
            columns.eq("article.status", status);
        }

        Page<Article> page = articleService._paginateByColumns(getPagePara(), getPageSizePara(), columns, null);

        setAttr("page", page);

        Long draftCount = articleService.findCountByStatus(Article.STATUS_DRAFT);
        Long trashCount = articleService.findCountByStatus(Article.STATUS_TRASH);
        Long normalCount = articleService.findCountByStatus(Article.STATUS_NORMAL);

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

        Article editArticle = articleService.findById(getParaToInt());
        if (editArticle != null) {
            setAttr("article", editArticle);

            List<ArticleCategory> tags = categoryService.findTagListByArticleId(editArticle.getId());
            setAttr("tags", tags);

            Long[] categoryIds = categoryService.findCategoryIdsByArticleId(editArticle.getId());
            flagCheck(categories, categoryIds);


            List<ArticleMetaInfo> metaInfos = metaInfoService.findAll();

            if (metaInfos != null && metaInfos.size() > 0) {
                Map<String, String> values = new HashMap<>();
                for (ArticleMetaInfo inf : metaInfos) {
                    ArticleMetaRecord record = metaRecordService.findByArticleIdAndFieldName(editArticle.getId(), inf.getFieldName());
                    if (record != null && StrUtil.isNotBlank(record.getValue())) {
                        values.put(record.getFieldName(), record.getValue());
                    }
                }

                if (!values.isEmpty()) {
                    //必须保证 articleMeta 和 ArticleMetaInfo.toSmartField() 里的setName 一致
                    //具体原因在 SmartField.doGetDataFromControllerByName 的时候，自动去找到这个map
                    set("articleMeta", values);
                }
            }
        }

        //初始化文章的编辑模式（编辑器）
        String editMode = editArticle == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : editArticle.getEditMode();
        setAttr("editMode", JPressConsts.EDIT_MODE_MARKDOWN.equals(editMode) ? JPressConsts.EDIT_MODE_MARKDOWN : JPressConsts.EDIT_MODE_HTML);

        //设置文章当前的样式
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

        //默认情况下，请求会被 escape，通过 getOriginalPara 获得非 escape 的数据
        article.setContent(getCleanedOriginalPara("article.content"));

        Ret validRet = validateSlug(article);
        if (validRet.isFail()) {
            renderJson(validRet);
            return;
        }


        if (StrUtil.isNotBlank(article.getSlug())) {
            Article existArticle = articleService.findFirstBySlug(article.getSlug());
            if (existArticle != null && !existArticle.getId().equals(article.getId())) {
                renderJson(Ret.fail("message", "该固定链接已经存在"));
                return;
            }
        }

        if (article.getCreated() == null) {
            article.setCreated(new Date());
        }

        if (article.getModified() == null) {
            article.setModified(new Date());
        }

        if (article.getOrderNumber() == null) {
            article.setOrderNumber(0);
        }
        if (getLoginedUser() != null) {
            //文章作者
            article.setAuthor(getLoginedUser().getNickname());
        }

        long id = (long) articleService.saveOrUpdate(article);
        articleService.doUpdateCommentCount(id);

        setAttr("articleId", id);
        setAttr("article", article);
        doSaveMetaInfos(article);


        Long[] saveBeforeCategoryIds = null;
        if (article.getId() != null) {
            saveBeforeCategoryIds = categoryService.findCategoryIdsByArticleId(article.getId());
        }


        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));

        Long[] updateCategoryIds = ArrayUtils.addAll(categoryIds, tagIds);

        articleService.doUpdateCategorys(id, updateCategoryIds);


        if (updateCategoryIds != null && updateCategoryIds.length > 0) {
            for (Long categoryId : updateCategoryIds) {
                categoryService.doUpdateArticleCount(categoryId);
            }
        }

        if (saveBeforeCategoryIds != null && saveBeforeCategoryIds.length > 0) {
            for (Long categoryId : saveBeforeCategoryIds) {
                categoryService.doUpdateArticleCount(categoryId);
            }
        }

        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }



    private void doSaveMetaInfos(Article article) {

        List<ArticleMetaInfo> metaInfos = metaInfoService.findAll();
        if (metaInfos == null || metaInfos.size() == 0) {
            return;
        }
        if (article == null || article.getId() == null) {
            return;
        }
        List<ArticleMetaRecord> records = new ArrayList<>();
        for (ArticleMetaInfo inf : metaInfos) {

            if (!inf.isEnable()) {
                continue;
            }

            String key = ArticleMetaInfo.buildFieldName(inf);
            String value = SmartField.TYPE_CHECKBOX.equals(inf.getType())
                    ? getValue(getParaValues(key))
                    : getPara(key);

            ArticleMetaRecord record = new ArticleMetaRecord();
            record.setArticleId(article.getId());
            record.setFieldName(inf.getFieldName());
            record.setValue(value);
            records.add(record);
        }

        metaRecordService.batchSaveOrUpdate(records);
    }


    private static String getValue(String[] values) {
        if (values == null) {
            return null;
        }
        if (values.length == 1) {
            return values[0];
        }

        int iMax = values.length - 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(values[i]);
            if (i == iMax) {
                return b.toString();
            }
            b.append(",");
        }
    }



    private Long[] getTagIds(String[] tags) {
        if (tags == null || tags.length == 0) {
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


    @AdminMenu(text = "分类", groupId = "article", order = 2)
    public void category() {
        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);
        long id = getParaToLong(0, 0L);
        if (id > 0 && categories != null) {
            for (ArticleCategory category : categories) {
                if (category.getId().equals(id)) {
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
        Page<ArticleCategory> page = categoryService.paginateByType(getPagePara(), getPageSizePara(), ArticleCategory.TYPE_TAG);
        setAttr("page", page);

        int categoryId = getParaToInt(0, 0);
        if (categoryId > 0) {
            setAttr("category", categoryService.findById(categoryId));
            //该分类是否显示在菜单上
            setAttr("isDisplayInMenu", menuService.findFirstByRelatives("article_category", categoryId) != null);
        }

        initStylesAttr("artlist_");
        render("article/tag_list.html");
    }


    @EmptyValidate({
            @Form(name = "category.title", message = "分类名称不能为空"),
    })
    public void doCategorySave() {
        ArticleCategory category = getModel(ArticleCategory.class, "category");
        saveCategory(category);
    }

    private void saveCategory(ArticleCategory category) {

        Ret validRet = validateSlug(category);
        if (validRet.isFail()) {
            renderJson(validRet);
            return;
        }

        ArticleCategory existModel = categoryService.findFirstByTypeAndSlug(category.getType(), category.getSlug());
        if (existModel != null && !Objects.equals(existModel.getId(), category.getId())) {
            renderJson(Ret.fail("message", "该分类的固定连接以及被占用"));
            return;
        }

        Object id = categoryService.saveOrUpdate(category);
        categoryService.doUpdateArticleCount(category.getId());

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
                displayMenu.setPid(0L);
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
