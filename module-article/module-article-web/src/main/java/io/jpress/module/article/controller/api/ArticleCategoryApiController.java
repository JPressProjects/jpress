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
package io.jpress.module.article.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.Api;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.apidoc.annotation.ApiPara;
import io.jboot.apidoc.annotation.ApiResp;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.commons.layer.SortKit;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章分类、tag 相关 api
 */
@RequestMapping("/api/article/category")
@Api("文章分类相关API文档")
public class ArticleCategoryApiController extends ApiControllerBase {

    @Inject
    private ArticleCategoryService categoryService;


    @ApiOper(value = "文章分类详情", paraNotes = "id 或者 slug 必须有一个不能为空")
    @ApiResp(field = "detail", dataType = ArticleCategory.class, notes = "文章分类详情")
    public Ret detail(@ApiPara("分类ID") Long id
            , @ApiPara("分类固定连接") String slug
    ) {
        if (id == null && slug == null) {
            return Ret.fail().set("message", "id 或者 slug 必须有一个不能为空");
        }

        if (id != null) {
            ArticleCategory category = categoryService.findById(id);
            return Ret.ok("detail", category);
        }

        ArticleCategory category = categoryService.findFirstByFlag(slug);
        return Ret.ok("detail", category);
    }


    @ApiOper("根据文章分类的type查询文章分类")
    @ApiResp(field = "list", notes = "文章分类列表", dataType = List.class, genericTypes = ArticleCategory.class)
    public Ret listByType(@ApiPara("分类type") @NotEmpty String type, @ApiPara("上级分类ID") Long pid) {

        List<ArticleCategory> categories = categoryService.findListByType(type);
        if (categories == null || categories.isEmpty()) {
            return Ret.ok().set("list", new HashMap<>());
        }

        if (pid != null) {
            categories = categories.stream()
                    .filter(category -> pid.equals(category.getPid()))
                    .collect(Collectors.toList());
        } else {
            SortKit.toTree(categories);
        }

        return Ret.ok().set("list", categories);
    }


    @ApiOper("删除文章分类（Tag）")
    public Ret doDelete(@ApiPara("分类ID") @NotNull Long id) {
        categoryService.deleteById(id);
        return Rets.OK;
    }

    @ApiOper(value = "创建新的文章分类",contentType = ContentType.JSON)
    @ApiResp(field = "id", notes = "文章分类D", dataType = Long.class, mock = "123")
    public Ret doCreate(@ApiPara("文章分类json") @JsonBody ArticleCategory articleCategory) {
        Object id = categoryService.save(articleCategory);
        return Ret.ok().set("id", id);
    }

    @ApiOper(value = "更新文章分类",contentType = ContentType.JSON)
    public Ret doUpdate(@ApiPara("文章分类json") @JsonBody ArticleCategory articleCategory) {
        categoryService.update(articleCategory);
        return Rets.OK;
    }


}
