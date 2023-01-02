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
package io.jpress.module.product.controller.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.commons.wordsfilter.WordFilterUtil;
import io.jpress.model.User;
import io.jpress.module.product.ProductNotifyKit;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.ProductCategory;
import io.jpress.module.product.model.ProductComment;
import io.jpress.module.product.model.ProductImage;
import io.jpress.module.product.service.ProductCategoryService;
import io.jpress.module.product.service.ProductCommentService;
import io.jpress.module.product.service.ProductImageService;
import io.jpress.module.product.service.ProductService;
import io.jpress.service.OptionService;
import io.jpress.service.UserService;
import io.jpress.web.base.TemplateControllerBase;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 产品前台页面Controller
 * @Package io.jpress.module.product.controller
 */
@RequestMapping("/product")
public class ProductController extends TemplateControllerBase {

    @Inject
    private ProductService productService;

    @Inject
    private ProductImageService imageService;

    @Inject
    private UserService userService;

    @Inject
    private ProductCategoryService categoryService;

    @Inject
    private OptionService optionService;

    @Inject
    private ProductCommentService commentService;



    public void index() {
        Product product = getProduct();

        //当产品处于下架等的时候，显示404
        render404If(product == null || !product.isNormal());

        if (getLoginedUser() == null) {
            setAttr("productShareUrl", RequestUtil.getBaseUrl() + product.getUrl());
        }else {
            setAttr("productShareUrl", RequestUtil.getBaseUrl() + product.getUrl() + "?did=" + getLoginedUser().getId());
        }

        //设置页面的seo信息
        setSeoInfos(product);


        //设置菜单高亮
        doFlagMenuActive(product);

        //记录当前浏览量
        productService.doIncProductViewCount(product.getId());

//        User productAuthor = product.getUserId() != null
//                ? userService.findById(product.getUserId())
//                : null;

//        product.put("user", productAuthor);

        setAttr("product", product);

        List<ProductImage> productImages = imageService.findListByProductId(product.getId());
        setAttr("productImages", productImages);

        String distUserId = getPara("did");
        if (StrUtil.isNotBlank(distUserId)) {
            CookieUtil.put(this, buildDistUserCookieName(product.getId()), distUserId);
        }

        render(product.getHtmlView());
    }

    private String buildDistUserCookieName(long productId) {
        return "did-" + productId;
    }

    private void setSeoInfos(Product product) {
        setSeoTitle(StrUtil.isBlank(product.getMetaTitle()) ? product.getTitle() : product.getMetaTitle());
        setSeoKeywords(product.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(product.getMetaDescription())
                ? CommonsUtils.maxLength(product.getText(), 100)
                : product.getMetaDescription());
    }


    private Product getProduct() {
        String idOrSlug = getIdOrSlug();
        return StrUtil.isNumeric(idOrSlug)
                ? productService.findById(idOrSlug)
                : productService.findFirstBySlug(StrUtil.urlDecode(idOrSlug));
    }


    private void doFlagMenuActive(Product product) {

        setMenuActive(menu -> menu.isUrlStartWidth(product.getUrl()));

        List<ProductCategory> productCategories = categoryService.findCategoryListByProductId(product.getId());
        if (productCategories == null || productCategories.isEmpty()) {
            return;
        }

        setMenuActive(menu -> {
            if ("product_category".equals(menu.getRelativeTable())) {
                for (ProductCategory category : productCategories) {
                    if (category.getId().equals(menu.getRelativeId())) {
                        return true;
                    }
                }
            }
            return false;
        });

    }


    /**
     * 发布评论
     */
    public void postComment() {

        Long productId = getParaToLong("productId");
        Long pid = getParaToLong("pid");
        String nickname = getPara("nickname");
        String content = getPara("content");

        if (productId == null || productId <= 0) {
            renderFailJson();
            return;
        }

        if (StrUtil.isBlank(content)) {
            renderJson(Ret.fail().set("message", "评论内容不能为空"));
            return;
        } else {
            content = StrUtil.escapeHtml(content);
        }

        //是否对用户输入验证码进行验证
        Boolean vCodeEnable = JPressOptions.isTrueOrEmpty("product_comment_vcode_enable");
        if (vCodeEnable != null && vCodeEnable == true) {
            if (validateCaptcha("captcha") == false) {
                renderJson(Ret.fail().set("message", "验证码错误").set("errorCode", 2));
                return;
            }
        }

        if (WordFilterUtil.isMatchedFilterWords(content)) {
            renderJson(Ret.fail().set("message", "非法内容，无法发布评论信息"));
            return;
        }


        Product product = productService.findById(productId);
        if (product == null) {
            renderFailJson();
            return;
        }

        // 关闭了评论的功能
        if (!product.isCommentEnable()) {
            renderJson(Ret.fail().set("message", "该产品的评论功能已关闭"));
            return;
        }

        //是否开启评论功能
        Boolean commentEnable = JPressOptions.isTrueOrEmpty("product_comment_enable");
        if (commentEnable == null || commentEnable == false) {
            renderJson(Ret.fail().set("message", "评论功能已关闭"));
            return;
        }


        //是否允许未登录用户参与评论
        Boolean unLoginEnable = optionService.findAsBoolByKey("product_comment_unlogin_enable");
        if (unLoginEnable == null || unLoginEnable == false) {
            if (getLoginedUser() == null) {
                renderJson(Ret.fail().set("message", "未登录用户不能评论").set("errorCode", 9));
                return;
            }
        }

        ProductComment comment = new ProductComment();

        comment.setProductId(productId);
        comment.setContent(content);
        comment.setAuthor(nickname);
        comment.setPid(pid);

        User user = getLoginedUser();
        if (user != null) {
            comment.setUserId(user.getId());
            comment.setAuthor(user.getNickname());
        }

        //是否是管理员必须审核
        Boolean reviewEnable = optionService.findAsBoolByKey("product_comment_review_enable");
        if (reviewEnable != null && reviewEnable == true) {
            comment.setStatus(ProductComment.STATUS_UNAUDITED);
        }
        /**
         * 无需管理员审核、直接发布
         */
        else {
            comment.setStatus(ProductComment.STATUS_NORMAL);
        }

        //记录产品的评论量
        productService.doIncProductCommentCount(productId);

        commentService.saveOrUpdate(comment);

        if (pid != null) {
            //记录评论的回复数量
            commentService.doIncCommentReplyCount(pid);

            ProductComment parent = commentService.findById(pid);
            if (parent != null && parent.isNormal()) {
                comment.put("parent", parent);
            }
        }

        Ret ret = Ret.ok().set("code", 0);

        Map<String, Object> paras = new HashMap<>();
        paras.put("comment", comment);
        paras.put("product", product);

        if (user != null) {
            paras.put("user", user.keepSafe());
            comment.put("user", user.keepSafe());
        }


        renderHtmltoRet("/WEB-INF/views/commons/product/defaultProductCommentItem.html", paras, ret);
        renderJson(ret);

        ProductNotifyKit.doNotifyAdministrator(product, comment, user);

    }



}
