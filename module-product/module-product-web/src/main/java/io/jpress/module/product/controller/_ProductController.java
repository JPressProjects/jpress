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
package io.jpress.module.product.controller;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;


@RequestMapping(value = "/admin/product", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ProductController extends AdminControllerBase {

    @Inject
    private ProductService productService;

    @Inject
    private ProductCategoryService categoryService;

    @AdminMenu(text = "商品列表", groupId = "product", order = 1)
    public void index() {
        Page<Product> entries = productService.paginate(getPagePara(), 10);
        setAttr("page", entries);
        render("product/product_list.html");
    }


    public void edit() {
        List<ProductCategory> categories = categoryService.findListByType(ProductCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

//        setAttr("fields", ArticleFields.me());


        int articleId = getParaToInt(0, 0);

        Product product = null;
        if (articleId > 0) {
            product = productService.findById(articleId);
            if (product == null) {
                renderError(404);
                return;
            }
            setAttr("product", product);

            List<ProductCategory> tags = categoryService.findTagListByProductId(articleId);
            setAttr("tags", tags);

            Long[] categoryIds = categoryService.findCategoryIdsByArticleId(articleId);
            flagCheck(categories, categoryIds);
        }

        initStylesAttr("product_");
        render("product/product_edit.html");
    }


    private void flagCheck(List<ProductCategory> categories, Long... checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (ProductCategory category : categories) {
            for (Long id : checkIds) {
                if (id != null && id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
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

    public void doSave() {
        Product product = getModel(Product.class, "product");

        if (!validateSlug(product)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }


        if (StrUtil.isNotBlank(product.getSlug())) {
            Product slugArticle = productService.findFirstBySlug(product.getSlug());
            if (slugArticle != null && slugArticle.getId().equals(product.getId()) == false) {
                renderJson(Ret.fail("message", "该slug已经存在"));
                return;
            }
        }

        if (product.getOrderNumber() == null) {
            product.setOrderNumber(0);
        }

        long id = (long) productService.saveOrUpdate(product);
        productService.doUpdateCommentCount(id);

        setAttr("articleId", id);
        setAttr("article", product);


        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));

        Long[] allIds = ArrayUtils.addAll(categoryIds, tagIds);

        productService.doUpdateCategorys(id, allIds);

        if (allIds != null && allIds.length > 0) {
            for (Long categoryId : allIds) {
                categoryService.doUpdateProductCount(categoryId);
            }
        }

        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }


    private Long[] getTagIds(String[] tags) {
        if (tags == null || tags.length == 0) {
            return null;
        }

        List<ProductCategory> categories = categoryService.findOrCreateByTagString(tags);
        long[] ids = categories.stream().mapToLong(value -> value.getId()).toArray();
        return ArrayUtils.toObject(ids);
    }



    public void doDel() {
        Long id = getIdPara();
        render(productService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    public void doTrash() {
        Long id = getIdPara();
        render(productService.doChangeStatus(id, Product.STATUS_TRASH) ? OK : FAIL);
    }

    public void doDraft() {
        Long id = getIdPara();
        render(productService.doChangeStatus(id, Product.STATUS_DRAFT) ? OK : FAIL);
    }

    public void doNormal() {
        Long id = getIdPara();
        render(productService.doChangeStatus(id, Product.STATUS_NORMAL) ? OK : FAIL);
    }
}