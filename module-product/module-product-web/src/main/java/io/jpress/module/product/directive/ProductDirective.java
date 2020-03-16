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
package io.jpress.module.product.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("product")
public class ProductDirective extends JbootDirectiveBase {

    @Inject
    private ProductService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        String idOrSlug = getPara(0, scope);
        Product product = getProduct(idOrSlug);

        if (product == null) {
            return;
        }

        scope.setLocal("product", product);
        renderBody(env, scope, writer);
    }

    private Product getProduct(String idOrSlug) {
        return StrUtil.isNumeric(idOrSlug)
                ? service.findById(idOrSlug)
                : service.findFirstBySlug(idOrSlug);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
