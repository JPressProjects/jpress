/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.addon.articlemeta.web;

import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.addon.articlemeta.model.ArticleMetaInfo;
import io.jpress.addon.articlemeta.service.ArticleMetaInfoService;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.module.article.ArticleFields;
import io.jpress.web.base.AdminControllerBase;

import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping(value = "/admin/article/meta", viewPath = "/")
public class _ArticleMetaController extends AdminControllerBase {

    @Inject
    private ArticleMetaInfoService articleMetaInfoService;

    @AdminMenu(groupId = "article", text = "元信息")
    public void index() {
        Page<ArticleMetaInfo> page = articleMetaInfoService.paginate(getPagePara(), 10);
        setAttr("page", page);
        render("views/article_meta_list.html");
    }


    public void edit() {
        Long id = getLong("id");
        if (id != null) {
            ArticleMetaInfo metaInfo = articleMetaInfoService.findById(id);
            setAttr("meta", metaInfo);
        }
        render("views/article_meta_edit.html");
    }


    public void doSave() {
        ArticleMetaInfo metaInfo = getModel(ArticleMetaInfo.class, "meta");
        articleMetaInfoService.saveOrUpdate(metaInfo);
        ArticleFields.me().addField(metaInfo.toSmartField());
        redirect("/admin/article/meta");
    }

    public void doMetaDel() {
        Long id = getLong("id");
        if (id == null || id <= 0) {
            renderError(404);
        }

        ArticleMetaInfo inf = articleMetaInfoService.findById(id);
        if (inf == null) {
            renderJson(Ret.fail());
            return;
        }

        ArticleFields.me().removeField(inf.getFieldId());
        articleMetaInfoService.deleteById(id);
        renderJson(Ret.ok());
    }

    public void doMetaDelByIds() {

        String ids = getPara("ids");
        if (StrUtil.isBlank(ids)) {
            renderJson(Ret.fail());
            return;
        }

        Set<String> idsSet = StrUtil.splitToSet(ids, ",");
        if (idsSet == null || idsSet.isEmpty()) {
            renderJson(Ret.fail());
            return;
        }

        for (String id : idsSet) {
            ArticleMetaInfo inf = articleMetaInfoService.findById(id);
            if (inf == null) continue;
            ArticleFields.me().removeField(inf.getFieldId());
            articleMetaInfoService.deleteById(id);
        }

        renderJson(Ret.ok());
    }
}
