/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.model.MemberGroup;
import io.jpress.module.product.ProductFields;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductImageService;
import io.jpress.module.product.service.ProductService;
import io.jpress.service.MemberGroupService;
import io.jpress.service.MemberPriceService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.ArrayUtils;

import java.util.List;
import java.util.Set;


@RequestMapping(value = "/admin/product", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ProductController extends AdminControllerBase {

    @Inject
    private ProductService productService;

    @Inject
    private ProductCategoryService categoryService;

    @Inject
    private ProductImageService imageService;

    @Inject
    private MemberGroupService memberGroupService;

    @Inject
    private MemberPriceService memberPriceService;


    @AdminMenu(text = "商品列表", groupId = "product", order = 1)
    public void index() {
        Integer status = getParaToInt("status");
        String title = getPara("title");
        Long categoryId = getParaToLong("categoryId");

        Page<Product> page = status == null
                ? productService._paginateWithoutTrash(getPagePara(), 10, title, categoryId)
                : productService._paginateByStatus(getPagePara(), 10, title, categoryId, status);

        setAttr("page", page);


        List<ProductCategory> categories = categoryService.findListByType(ProductCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);
        flagCheck(categories, categoryId);


        long draftCount = productService.findCountByStatus(Product.STATUS_DRAFT);
        long trashCount = productService.findCountByStatus(Product.STATUS_TRASH);
        long normalCount = productService.findCountByStatus(Product.STATUS_NORMAL);

        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);


        render("product/product_list.html");
    }


    public void edit() {
        List<ProductCategory> categories = categoryService.findListByType(ProductCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        setAttr("fields", ProductFields.me());


        Long productId = getParaToLong(0, 0L);

        Product product = null;
        if (productId != null && productId > 0) {
            product = productService.findById(productId);
            if (product == null) {
                renderError(404);
                return;
            }
            setAttr("product", product);

            List<ProductCategory> tags = categoryService.findTagListByProductId(productId);
            setAttr("tags", tags);

            Long[] categoryIds = categoryService.findCategoryIdsByProductId(productId);
            flagCheck(categories, categoryIds);

            setAttr("images", imageService.findListByProductId(productId));
        }

        List<MemberGroup> memberGroups = memberGroupService.findAll();
        if (memberGroups != null && !memberGroups.isEmpty()) {
            if (product != null) {
                for (MemberGroup group : memberGroups) {
                    group.put("priceInfo", memberPriceService.findByPorductAndGroup("product", product.getId(), group.getId()));
                }
            }
            setAttr("memberGroups", memberGroups);
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

    @EmptyValidate({
            @Form(name = "product.title", message = "产品标题不能为空"),
            @Form(name = "product.price", message = "产品的销售价格不能为空")
    })
    public void doSave() {
        Product product = getModel(Product.class, "product");

        if (!validateSlug(product)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }


        if (StrUtil.isNotBlank(product.getSlug())) {
            Product existModel = productService.findFirstBySlug(product.getSlug());
            if (existModel != null && existModel.getId().equals(product.getId()) == false) {
                renderJson(Ret.fail("message", "该slug已经存在"));
                return;
            }
        }

        if (product.getOrderNumber() == null) {
            product.setOrderNumber(0);
        }

        if (product.getViewCount() == null){
            product.setViewCount(0L);
        }
        long id = (long) productService.saveOrUpdate(product);
        productService.doUpdateCommentCount(id);

        setAttr("productId", id);
        setAttr("product", product);


        Long[] saveBeforeCategoryIds = null;
        if (product.getId() != null){
            saveBeforeCategoryIds = categoryService.findCategoryIdsByProductId(product.getId());
        }


        Long[] categoryIds = getParaValuesToLong("category");
        Long[] tagIds = getTagIds(getParaValues("tag"));

        Long[] updateCategoryIds = ArrayUtils.addAll(categoryIds, tagIds);

        productService.doUpdateCategorys(id, updateCategoryIds);

        if (updateCategoryIds != null && updateCategoryIds.length > 0) {
            for (Long categoryId : updateCategoryIds) {
                categoryService.doUpdateProductCount(categoryId);
            }
        }

        if (saveBeforeCategoryIds != null && saveBeforeCategoryIds.length > 0) {
            for (Long categoryId : saveBeforeCategoryIds) {
                categoryService.doUpdateProductCount(categoryId);
            }
        }

        String[] imageIds = getParaValues("imageIds");
        String[] imageSrcs = getParaValues("imageSrcs");
        imageService.saveOrUpdateByProductId(product.getId(), imageIds, imageSrcs);


        String[] memberGroupIds = getParaValues("memberGroupIds");
        String[] memberGroupPrices = getParaValues("memberGroupPrices");
        memberPriceService.saveOrUpdateByProduct("product", product.getId(), memberGroupIds, memberGroupPrices);


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
        if (productService.deleteById(id)){
            imageService.deleteByProductId(id);
        }

        renderOkJson();
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

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        if (productService.batchDeleteByIds(idsSet.toArray())){
            for (String id : idsSet){
                imageService.deleteByProductId(Long.valueOf(id));
            }
        }
        renderOkJson();
    }

    @AdminMenu(text = "设置", groupId = "product", order = 99)
    public void setting() {
        render("product/setting.html");
    }
}
