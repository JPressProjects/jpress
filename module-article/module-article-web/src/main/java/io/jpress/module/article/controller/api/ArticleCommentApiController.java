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
import io.jboot.aop.annotation.DefaultValue;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.json.JsonBody;
import io.jpress.commons.Rets;
import io.jpress.module.article.model.ArticleComment;
import io.jpress.module.article.service.ArticleCommentService;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotNull;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章评论相关 API
 */
@RequestMapping("/api/article/comment")
public class ArticleCommentApiController extends ApiControllerBase {

    @Inject
    private ArticleCommentService commentService;


    public Ret paginateByArticleId(@NotNull Long articleId, @DefaultValue("1") int pageNumber, @DefaultValue("10") int pageSize) {
        return Ret.ok().set("page", commentService.paginateByArticleIdInNormal(pageNumber, pageSize, articleId));
    }

    /**
     * 删除评论
     */
    public Ret doDelete(@NotNull Long id) {
        commentService.deleteById(id);
        return Rets.OK;
    }

    /**
     * 创建新评论
     */
    public Ret doCreate(@JsonBody ArticleComment articleComment) {
        Object id = commentService.save(articleComment);
        return Ret.ok().set("id", id);
    }

    /**
     * 更新评论
     */
    public Ret doUpdate(@JsonBody ArticleComment articleComment) {
        commentService.update(articleComment);
        return Rets.OK;
    }

}
