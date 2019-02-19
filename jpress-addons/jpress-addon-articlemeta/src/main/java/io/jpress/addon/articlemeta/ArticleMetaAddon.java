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

import com.jfinal.render.RenderManager;
import io.jpress.addon.articlemeta.directive.ArticleMetaDirective;
import io.jpress.core.addon.Addon;
import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonUtil;

import java.sql.SQLException;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 文章元信息扩展插件
 */
public class ArticleMetaAddon implements Addon {

    @Override
    public void onInstall(AddonInfo addonInfo) {
        try {
            AddonUtil.execSqlFile(addonInfo, "sql/articlemeta_install.sql");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUninstall(AddonInfo addonInfo) {
        try {
            AddonUtil.execSqlFile(addonInfo, "sql/articlemeta_uninstall.sql");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStart(AddonInfo addonInfo) {
        RenderManager.me().getEngine().addDirective("articleMeta", ArticleMetaDirective.class);
    }

    @Override
    public void onStop(AddonInfo addonInfo) {
        RenderManager.me().getEngine().removeDirective("articleMeta");
    }
}
