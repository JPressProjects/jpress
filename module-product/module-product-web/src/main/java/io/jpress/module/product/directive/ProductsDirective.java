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
package io.jpress.module.product.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.commons.service.SiteModelProxy;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("products")
public class ProductsDirective extends JbootDirectiveBase {

    @Inject
    private ProductService service;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("flag", scope);
        String style = getPara("style", scope);
        Boolean hasThumbnail = getParaToBool("hasThumbnail", scope);
        String orderBy = getPara("orderBy", scope, "id desc");
        int count = getParaToInt("count", scope, 10);

        //查询哪些站点，传入 * 或者 all，查询所有站点
        //或者传入站点的id，多个id用英文逗号隔开
        String withSite = getParaToString("withSite", scope);


        Columns columns = Columns.create("flag", flag);
        columns.eq("style", style);

        columns.eq("status", Product.STATUS_NORMAL);

        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("thumbnail");
            } else {
                columns.isNull("thumbnail");
            }
        }

        List<Product> products = null;
        if (StrUtil.isNotBlank(withSite)) {
            withSite = withSite.trim().toLowerCase();
            if (withSite.equals("*") || withSite.equals("all")) {
                try {
                    SiteModelProxy.useAllSites();
                    products = service.findListByColumns(columns, orderBy, count);
                } finally {
                    SiteModelProxy.clearUsed();
                }
            } else {
                long[] siteIds = StrUtil.splitToSet(withSite, ",").stream()
                        .mapToLong(Long::parseLong).toArray();
                try {
                    SiteModelProxy.useSites(siteIds);
                    products = service.findListByColumns(columns, orderBy, count);
                } finally {
                    SiteModelProxy.clearUsed();
                }
            }
        } else {
            products = service.findListByColumns(columns, orderBy, count);
        }


        if (products == null || products.isEmpty()) {
            return;
        }

        scope.setLocal("products", products);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
