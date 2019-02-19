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
package io.jpress.addon.articlemeta;

import com.jfinal.aop.Aop;
import com.jfinal.config.Interceptors;
import com.jfinal.template.Engine;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jpress.addon.articlemeta.directive.ArticleMetaDirective;
import io.jpress.addon.articlemeta.model.ArticleMetaInfo;
import io.jpress.addon.articlemeta.service.ArticleMetaInfoService;
import io.jpress.addon.articlemeta.web.MetaInterceptor;
import io.jpress.module.article.ArticleFields;

import java.util.List;

/**
 * 此类没任何作用，只是用于开发的时候
 * 当做一个module进行开发而已，发布插件的时候可以删除
 */
public class ArticleMetaModule extends JbootAppListenerBase {


    @Override
    public void onEngineConfig(Engine engine) {
        engine.addDirective("articleMeta", ArticleMetaDirective.class);
    }

    @Override
    public void onInterceptorConfig(Interceptors interceptors) {
        interceptors.add(Aop.get(MetaInterceptor.class));
    }


    @Override
    public void onStart() {
        List<ArticleMetaInfo> metaInfos = Aop.get(ArticleMetaInfoService.class).findAll();
        if (metaInfos != null) {
            for (ArticleMetaInfo inf : metaInfos) {
                ArticleFields.me().addField(inf.toSmartField());
            }
        }
    }
}
