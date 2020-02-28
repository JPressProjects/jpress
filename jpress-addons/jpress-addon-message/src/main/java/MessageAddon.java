import com.jfinal.render.RenderManager;
import io.jpress.JPressConsts;
import io.jpress.core.addon.Addon;
import io.jpress.core.addon.AddonInfo;
import io.jpress.core.addon.AddonUtil;
import io.jpress.core.menu.MenuGroup;
import io.jpress.core.menu.MenuManager;

import java.sql.SQLException;

/**
 *留言信息插件
 */
public class MessageAddon implements Addon {

    @Override
    public void onInstall(AddonInfo addonInfo) {
        try {
            AddonUtil.execSqlFile(addonInfo, "sql/install.sql");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onUninstall(AddonInfo addonInfo) {
        try {
            AddonUtil.execSqlFile(addonInfo, "sql/uninstall.sql");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onStart(AddonInfo addonInfo) {

        /**
         *  在 onStart 方法中，我们可以做很多事情，例如：创建后台或用户中心的菜单
         *
         *  此方法是每次项目启动，都会执行。
         *
         *  同时用户也可以在后台触发
         */
        MenuGroup orderMenuGroup = new MenuGroup();
        orderMenuGroup.setId("message");
        orderMenuGroup.setText("留言管理");
        orderMenuGroup.setIcon("<i class=\"fa fa-fw fa-gg-circle\"></i>");
        MenuManager.me().getModuleMenus().add(orderMenuGroup);

    }

    @Override
    public void onStop(AddonInfo addonInfo) {

        /**
         *  和 onStart 对应，在 onStart 所处理的事情，在 onStop 应该释放
         *
         *  同时用户也可以在后台触发
         */
        MenuManager.me().deleteMenuGroup("message");

    }
}
