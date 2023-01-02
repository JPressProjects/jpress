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
package io.jpress.module.product.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.service.ProductService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2019/11/30
 */
@RequestMapping("/api/product")
@Api("产品相关 API")
public class ProductApiController extends ApiControllerBase {

    @Inject
    private ProductService productService;

    @ApiOper(value = "获取商品详情", paraNotes = "id 和 slug 必须传入一个值")
    public Ret detail(@ApiPara("产品ID") Long id, @ApiPara("产品固定连接") String slug) {
        Product product = id != null ? productService.findById(id)
                : (StrUtil.isNotBlank(slug) ? productService.findFirstBySlug(slug) : null);

        if (product == null || !product.isNormal()) {
            return Rets.FAIL;
        }

        productService.doIncProductViewCount(product.getId());
        return Ret.ok().set("detail", product);
    }


    @ApiPara("根据产品的 Flag 获取产品列表")
    public Ret listByFlag(@ApiPara("产品标识 flag") String flag
            , @ApiPara(value = "是否必须要图片", notes = "true 必须有图片，false 必须无图片") Boolean hasThumbnail
            , @ApiPara("排序方式") String orderBy
            , @ApiPara("查询数量") @DefaultValue("10") int count) {

        Columns columns = Columns.create("flag", flag);
        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("thumbnail");
            } else {
                columns.isNull("thumbnail");
            }
        }

        List<Product> products = productService.findListByColumns(columns, orderBy, count);
        return Ret.ok().set("list", products);
    }


    @ApiPara("查询某个产品的相关产品")
    public Ret listByRelevant(@ApiPara("产品ID") @NotNull @Min(1) Long id
            , @ApiPara("要查询的产品数量") @DefaultValue("3") int count) {
        List<Product> relevantProducts = productService.findRelevantListByProductId(id, Product.STATUS_NORMAL, count);
        return Ret.ok().set("list", relevantProducts);
    }


    @ApiPara("商品搜索")
    public Ret search(@ApiPara("关键字") String keyword
            , @ApiPara("分页页码") @DefaultValue("1") int pageNumber
            , @ApiPara("每页数量") @DefaultValue("10") int pageSize) {
        Page<Product> dataPage = StrUtil.isNotBlank(keyword)
                ? productService.search(keyword, pageNumber, pageSize)
                : null;
        return Ret.ok().set("page", dataPage);
    }


    @ApiOper("删除产品")
    public Ret doDelete(@ApiPara("产品ID") @NotNull Long id) {
        productService.deleteById(id);
        return Rets.OK;
    }


    @ApiOper(value = "新增产品", contentType = ContentType.JSON)
    public Ret doCreate(@ApiPara("产品的 JSON 信息") @JsonBody Product product) {
        Object id = productService.save(product);
        return Ret.ok().set("id", id);
    }


    @ApiOper(value = "更新产品信息", contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("产品的 JSON 信息") @JsonBody Product product) {
        productService.update(product);
        return Rets.OK;
    }


}
