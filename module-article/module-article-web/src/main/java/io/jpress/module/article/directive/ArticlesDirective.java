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
package io.jpress.module.article.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.commons.service.SiteModelProxy;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.service.ArticleService;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@JFinalDirective("articles")
public class ArticlesDirective extends JbootDirectiveBase {

    @Inject
    private ArticleService service;


    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String flag = getPara("flag", scope);
        String style = getPara("style", scope);
        Boolean hasThumbnail = getParaToBool("hasThumbnail", scope);
        String orderBy = getPara("orderBy", scope, "id desc");
        int count = getParaToInt("count", scope, 10);
        Boolean withRecommend = getParaToBool("withRecommend", scope);
        Boolean withTop = getParaToBool("withTop", scope);
        Boolean withHot = getParaToBool("withHot", scope);
        Boolean withLeadNews = getParaToBool("withLeadNews", scope);

        //查询哪些站点，传入 * 或者 all，查询所有站点
        //或者传入站点的id，多个id用英文逗号隔开
        String withSite = getParaToString("withSite", scope);

        Columns columns = Columns.create();

        columns.eq("flag", flag);
        columns.eq("style", style);
        columns.eq("with_recommend", withRecommend);
        columns.eq("with_top", withTop);
        columns.eq("with_hot", withHot);
        columns.eq("with_lead_news", withLeadNews);
        columns.eq("status", Article.STATUS_NORMAL);

        if (hasThumbnail != null) {
            if (hasThumbnail) {
                columns.isNotNull("thumbnail");
            } else {
                columns.isNull("thumbnail");
            }
        }


        List<Article> articles = null;
        if (StrUtil.isNotBlank(withSite)) {
            withSite = withSite.trim().toLowerCase();
            if (withSite.equals("*") || withSite.equals("all")) {
                try {
                    SiteModelProxy.useAllSites();
                    articles = service.findListByColumns(columns, orderBy, count);
                } finally {
                    SiteModelProxy.clearUsed();
                }
            } else {
                long[] siteIds = StrUtil.splitToSet(withSite, ",").stream()
                        .mapToLong(Long::parseLong).toArray();
                try {
                    SiteModelProxy.useSites(siteIds);
                    articles = service.findListByColumns(columns, orderBy, count);
                } finally {
                    SiteModelProxy.clearUsed();
                }
            }
        } else {
            articles = service.findListByColumns(columns, orderBy, count);
        }


        if (articles == null || articles.isEmpty()) {
            return;
        }

        scope.setLocal("articles", articles);
        renderBody(env, scope, writer);
    }


    @Override
    public boolean hasEnd() {
        return true;
    }
}
