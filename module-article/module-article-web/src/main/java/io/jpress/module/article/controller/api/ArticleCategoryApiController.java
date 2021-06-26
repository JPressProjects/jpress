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
package io.jpress.module.article.controller.api;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
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
public class ArticleCategoryApiController extends ApiControllerBase {

    @Inject
    private ArticleCategoryService categoryService;


    /**
     * 获取分类详情的API
     * <p>
     * 可以通过 id 获取文章分类，也可以通过 type + slug 定位到分类
     * 分类可能是后台对应的分类，有可以是一个tag（tag也是一种分类）
     * <p>
     * 例如：
     * http://127.0.0.1:8080/api/article/category/detail?id=123
     * 或者
     * http://127.0.0.1:8080/api/article/category/detail?type=category&slug=myslug
     * http://127.0.0.1:8080/api/article/category/detail?type=tag&slug=myslug
     */
    public Ret detail(Long id, String slug, String type) {
        if (id == null && slug == null) {
            return Ret.fail().set("message", "id 或者 slug 必须有一个不能为空");
        }

        if (id != null) {
            ArticleCategory category = categoryService.findById(id);
            return Ret.ok("detail", category);
        }

        ArticleCategory category = categoryService.findFirstByTypeAndSlug(type, slug);
        return Ret.ok("detail", category);
    }

    /**
     * 获取文章的分类
     */
    public Ret listByType(@NotEmpty String type, Long pid) {

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


    /**
     * 删除分类
     */
    public Ret doDelete(@NotNull Long id) {
        categoryService.deleteById(id);
        return Rets.OK;
    }

    /**
     * 创建新分类
     */
    public Ret doCreate(@JsonBody ArticleCategory articleCategory) {
        Object id = categoryService.save(articleCategory);
        return Ret.ok().set("id",id);
    }

    /**
     * 更新分类
     */
    public Ret doUpdate(@JsonBody ArticleCategory articleCategory) {
        categoryService.update(articleCategory);
        return Rets.OK;
    }


}
