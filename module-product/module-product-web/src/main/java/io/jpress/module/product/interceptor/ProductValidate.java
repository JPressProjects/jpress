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
package io.jpress.module.product.interceptor;

import com.jfinal.aop.Inject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.Ret;
import io.jboot.utils.RequestUtil;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.model.User;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.interceptor.UserInterceptor;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/12/5
 */
public class ProductValidate implements Interceptor {

    @Inject
    private ProductService productService;
    private static final String ATTR_PRODUCT = "product";

    public static Product getThreadLocalProduct() {
        return JbootControllerContext.get().getAttr(ATTR_PRODUCT);
    }

    @Override
    public void intercept(Invocation inv) {

        Controller c = inv.getController();

        Long productId = inv.getController().getLong("id");
        Product product = productService.findById(productId);

        if (product == null || !product.isNormal()) {
            if (RequestUtil.isAjaxRequest(c.getRequest())) {
                c.renderJson(Ret.fail().set("code", "2").set("message", "商品不存在或已下架。"));
            } else {
                c.renderError(404);
            }
            return;
        }


        User user = UserInterceptor.getThreadLocalUser();
        if (user == null) {
            if (RequestUtil.isAjaxRequest(c.getRequest())) {
                c.renderJson(Ret.fail()
                        .set("code", 1)
                        .set("message", "用户未登录")
                        .set("gotoUrl", JFinal.me().getContextPath() + "/user/login?gotoUrl=" + product.getUrl()));
            } else {
                c.redirect("/user/login?gotoUrl=" + product.getUrl());
            }
            return;
        }

        c.setAttr(ATTR_PRODUCT,product);
        inv.invoke();
    }
}
