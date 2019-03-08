package io.jpress.addon.articlemeta;

import com.jfinal.aop.Aop;
import com.jfinal.render.RenderManager;
import io.jpress.addon.articlemeta.directive.ArticleMetaDirective;
import io.jpress.addon.articlemeta.model.ArticleMetaInfo;
import io.jpress.addon.articlemeta.service.ArticleMetaInfoService;
import io.jpress.core.addon.Addon;
import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonUtil;
import io.jpress.module.article.ArticleFields;

import java.sql.SQLException;
import java.util.List;

/**
 * 文章元数据插件
 *
 * @author Eric.Huang
 * @date 2019-02-28 10:57
 * @package io.jpress.addon.articlemeta
 **/

public class ArticleMetadataAddon implements Addon {

    @Override
    public void onInstall(AddonInfo addonInfo) {
        /**
         * 在 onInstall ，我们一般需要 创建自己的数据表
         * onInstall 方法只会执行一次，执行完毕之后不会再执行，除非是用户卸载插件再次安装
         */

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
        List<ArticleMetaInfo> metaInfos = Aop.get(ArticleMetaInfoService.class).findAll();
        if (metaInfos != null) {
            for (ArticleMetaInfo inf : metaInfos) {
                ArticleFields.me().addField(inf.toSmartField());
            }
        }
    }

    @Override
    public void onStop(AddonInfo addonInfo) {
        RenderManager.me().getEngine().removeDirective("articleMeta");
        List<ArticleMetaInfo> metaInfos = Aop.get(ArticleMetaInfoService.class).findAll();
        if (metaInfos != null) {
            for (ArticleMetaInfo inf : metaInfos) {
                ArticleFields.me().removeField(inf.getFieldId());
            }
        }
    }
}
