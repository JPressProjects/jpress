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
package io.jpress.module.article.kits;

import io.jboot.Jboot;
import io.jboot.utils.StrUtils;
import io.jpress.JPressConsts;
import io.jpress.module.article.model.Article;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.service.OptionService;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.module.article.kits
 */
public class ArticleModuleKit {

    public static String getCategoryUrl(ArticleCategory category) {
        OptionService service = Jboot.bean(OptionService.class);
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return category.getUrl("");
        }

        String suffix = service.findByKey(JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StrUtils.isBlank(suffix) ? category.getUrl("") : category.getUrl(suffix);
    }


    public static String getArticleUrl(Article article) {
        OptionService service = Jboot.bean(OptionService.class);
        Boolean fakeStaticEnable = service.findAsBoolByKey(JPressConsts.OPTION_WEB_FAKE_STATIC_ENABLE);
        if (fakeStaticEnable == null || fakeStaticEnable == false) {
            return article.getUrl("");
        }

        String suffix = service.findByKey(JPressConsts.OPTION_WEB_FAKE_STATIC_SUFFIX);
        return StrUtils.isBlank(suffix) ? article.getUrl("") : article.getUrl(suffix);
    }

}
