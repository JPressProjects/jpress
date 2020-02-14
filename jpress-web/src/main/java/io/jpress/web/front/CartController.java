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
package io.jpress.web.front;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.core.finance.ProductManager;
import io.jpress.model.UserCart;
import io.jpress.service.UserAddressService;
import io.jpress.service.UserCartService;
import io.jpress.service.UserFavoriteService;
import io.jpress.service.UserService;
import io.jpress.web.base.UcenterControllerBase;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping(value = "/ucenter/cart", viewPath = "/WEB-INF/views/ucenter")
public class CartController extends UcenterControllerBase {

    @Inject
    private UserService userService;

    @Inject
    private UserCartService cartService;

    @Inject
    private UserAddressService addressService;

    @Inject
    private UserFavoriteService favoriteService;


    /**
     * 购物车
     */
    public void index() {
        Page<UserCart> page = cartService.paginateByUser(1, 20, getLoginedUser().getId());


        if (page != null && page.getList() != null) {
            for (UserCart userCart : page.getList()) {
                userCart.put("optionsMap", ProductManager.me().renderProductOptions(userCart));
            }
        }

        setAttr("page", page);
        setAttr("selectItemCount", cartService.querySelectedCount(getLoginedUser().getId()));

        List<UserCart> selectedUserCarts = cartService.findSelectedListByUserId(getLoginedUser().getId());
        if (selectedUserCarts != null) {
            BigDecimal totalPrice = new BigDecimal(0);
            for (UserCart cart : selectedUserCarts) {
                totalPrice = totalPrice.add(cart.getShouldPayPrice());
            }
            setAttr("selectItemTotalPrice", totalPrice);
        }


        render("cart.html");
    }


    public void querySelectedItemCountAndPrice() {

        long count = cartService.querySelectedCount(getLoginedUser().getId());
        BigDecimal price = BigDecimal.ZERO;


        List<UserCart> userCarts = cartService.findSelectedListByUserId(getLoginedUser().getId());
        if (userCarts != null) {
            for (UserCart cart : userCarts) {
                price = price.add(cart.getShouldPayPrice());
            }
        }

        renderJson(Ret.ok().set("count", count).set("price", new DecimalFormat("0.00").format(price)));
    }

    /**
     * 选择某个
     */
    @EmptyValidate({
            @Form(name = "id", message = "id不能为空")
    })
    public void select() {
        Set<String> ids = StrUtil.splitToSet(getPara("id"), ",");

        for (String idvalue : ids) {
            UserCart cart = cartService.findById(idvalue);
            if (cart != null && isLoginedUserModel(cart)) {
                cart.setSelected(true);
                cartService.update(cart);
            }
        }

        renderOkJson();
    }

    /**
     * 取消选择
     */
    @EmptyValidate({
            @Form(name = "id", message = "id不能为空")
    })
    public void unselect() {
        Set<String> ids = StrUtil.splitToSet(getPara("id"), ",");
        for (String idvalue : ids) {
            UserCart cart = cartService.findById(idvalue);
            if (cart != null && isLoginedUserModel(cart)) {
                cart.setSelected(false);
                cartService.update(cart);
            }
        }
        renderOkJson();
    }


    public void doDelSelectedItems() {
        List<UserCart> userCarts = cartService.findSelectedListByUserId(getLoginedUser().getId());
        if (userCarts != null) {
            for (UserCart cart : userCarts) {
                cartService.delete(cart);
            }
        }
        renderOkJson();
    }


    public void doRemoveSelectedItemsToFavorites() {
        List<UserCart> userCarts = cartService.findSelectedListByUserId(getLoginedUser().getId());
        if (userCarts != null) {
            for (UserCart cart : userCarts) {
                favoriteService.doAddToFavorite(cart.toFavorite());
                cartService.delete(cart);
            }
        }
        renderOkJson();
    }

    /**
     * 对某个购物车商品 +1
     */
    @EmptyValidate({
            @Form(name = "id", message = "id不能为空")
    })
    public void addcount() {
        UserCart userCart = cartService.findById(getPara("id"));
        if (notLoginedUserModel(userCart)) {
            renderFailJson();
            return;
        }

        userCart.setProductCount(userCart.getProductCount() + 1);
        cartService.update(userCart);
        renderJson(Ret.ok().set("shouldPayPrice", new DecimalFormat("0.00").format(userCart.getShouldPayPrice())));

    }

    /**
     * 都某个购物车商品 -1
     */
    @EmptyValidate({
            @Form(name = "id", message = "id不能为空")
    })
    public void subtractcount() {
        UserCart userCart = cartService.findById(getPara("id"));
        if (notLoginedUserModel(userCart)) {
            renderFailJson();
            return;
        }

        if (userCart.getProductCount() > 1) {
            userCart.setProductCount(userCart.getProductCount() - 1);
            cartService.update(userCart);
        }

        renderJson(Ret.ok().set("shouldPayPrice", new DecimalFormat("0.00").format(userCart.getShouldPayPrice())));
    }

    /**
     * 删除某个商品
     */
    public void doDel() {
        UserCart userCart = cartService.findById(getPara("id"));
        if (notLoginedUserModel(userCart)) {
            renderFailJson();
            return;
        }
        cartService.delete(userCart);
        renderOkJson();
    }


}
