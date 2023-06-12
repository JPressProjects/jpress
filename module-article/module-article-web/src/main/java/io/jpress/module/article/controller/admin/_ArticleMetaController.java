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
package io.jpress.module.article.controller.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.article.ArticleFields;
import io.jpress.module.article.model.ArticleMetaInfo;
import io.jpress.module.article.service.ArticleMetaInfoService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping(value = "/admin/article/meta", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _ArticleMetaController extends AdminControllerBase {

    @Inject
    private ArticleMetaInfoService articleMetaInfoService;

    @AdminMenu(groupId = "article", text = "元信息")
    public void list() {
        Page<ArticleMetaInfo> page = articleMetaInfoService.paginate(getPagePara(), 10);
        setAttr("page", page);
        render("article/article_meta_list.html");
    }


    public void edit() {
        Long id = getLong("id");
        if (id != null) {
            ArticleMetaInfo metaInfo = articleMetaInfoService.findById(id);
            setAttr("meta", metaInfo);
        }
        render("article/article_meta_edit.html");
    }


    public void doSave() {
        ArticleMetaInfo metaInfo = getModel(ArticleMetaInfo.class, "meta");


        if (StrUtil.isBlank(metaInfo.getLabel())) {
            renderJson(Ret.fail().set("message", "元信息不能为空。"));
            return;
        }

        if (StrUtil.isBlank(metaInfo.getFieldId())) {
            renderJson(Ret.fail().set("message", "Id属性值不能为空。"));
            return;
        }

        if (StrUtil.isBlank(metaInfo.getFieldName())) {
            renderJson(Ret.fail().set("message", "Name属性值不能为空。"));
            return;
        }

        //update
        if (metaInfo.getId() != null) {
            ArticleMetaInfo dbInfo = articleMetaInfoService.findById(metaInfo.getId());
            if (!metaInfo.getFieldId().equals(dbInfo.getId())) {
                ArticleFields.me().removeField(dbInfo.getFieldId());
            }
        }

        articleMetaInfoService.saveOrUpdate(metaInfo);

        if (metaInfo.isEnable()) {
            ArticleFields.me().addField(metaInfo.toSmartField());
        } else {
            ArticleFields.me().removeField(metaInfo.getFieldId());
        }

        renderOkJson();
    }

    public void doMetaDel() {
        Long id = getLong("id");
        if (id == null || id <= 0) {
            renderError(404);
        }

        ArticleMetaInfo inf = articleMetaInfoService.findById(id);
        if (inf == null) {
            renderFailJson();
            return;
        }

        ArticleFields.me().removeField(inf.getFieldId());
        articleMetaInfoService.deleteById(id);
        renderOkJson();
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doMetaDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        for (String id : idsSet) {
            ArticleMetaInfo inf = articleMetaInfoService.findById(id);
            if (inf == null) {
                continue;
            }
            ArticleFields.me().removeField(inf.getFieldId());
            articleMetaInfoService.deleteById(id);
        }

        renderOkJson();
    }
}
