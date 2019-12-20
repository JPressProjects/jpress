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
package io.jpress.web.install;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Before;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.DbKit;
import io.jboot.db.ArpManager;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.core.install.DbExecuter;
import io.jpress.core.install.InstallManager;
import io.jpress.core.install.Installer;
import io.jpress.model.Role;
import io.jpress.model.User;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.ControllerBase;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/install")
@Before(InstallInterceptor.class)
public class InstallController extends ControllerBase {

    public void index() {
        render("/WEB-INF/install/views/step1.html");
    }

    public void step1() {
        redirect("/install");
    }

    public void step2() {

        setAttr("JPRESS_DB_HOST", System.getenv("JPRESS_DB_HOST"));
        setAttr("JPRESS_DB_PORT", System.getenv("JPRESS_DB_PORT"));
        setAttr("JPRESS_DB_NAME", System.getenv("JPRESS_DB_NAME"));
        setAttr("JPRESS_DB_USER", System.getenv("JPRESS_DB_USER"));
        setAttr("JPRESS_DB_PASSWORD", System.getenv("JPRESS_DB_PASSWORD"));

        render("/WEB-INF/install/views/step2.html");
    }


    @EmptyValidate({
            @Form(name = "dbName", message = "数据库名不能为空"),
            @Form(name = "dbUser", message = "用户名不能为空"),
            @Form(name = "dbHost", message = "主机不能为空"),
            @Form(name = "dbPort", message = "端口号不能为空"),
    })
    public void gotoStep3() {

        String dbName = getPara("dbName");
        String dbUser = getPara("dbUser");
        String dbPwd = getPara("dbPwd");
        String dbHost = getPara("dbHost");
        int dbPort = getParaToInt("dbPort");


        boolean dbAutoCreate = getParaToBoolean("dbAutoCreate", false);
        if (dbAutoCreate) {
            if (!createDatabase(dbName, dbUser, dbPwd, dbHost, dbPort)) {
                renderJson(Ret.fail().set("message", "无法自动创建数据库，可能是用户名密码错误，或没有权限").set("errorCode", 5));
                return;
            }
        }


        try {

            InstallManager.me().init(dbName, dbUser, dbPwd, dbHost, dbPort);

            if (InstallManager.me().isDbExist() && !InstallManager.me().isJPressDb()) {
                renderJson(Ret.fail("message", "无法安装，该数据库已有表信息了，为了安全起见，请选择全新的数据库进行安装。")
                        .set("errorCode", 5));
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            renderFailJson();
            return;
        }

        renderOkJson();
    }


    public void step3() {

        if (!InstallManager.me().isInited()){
            redirect("/install");
            return;
        }

        //数据库需要升级
        if (InstallManager.me().isDbExist()
                && InstallManager.me().isJPressDb()
                && InstallManager.me().isNeedUpgrade()) {
            render("/WEB-INF/install/views/step3_upgrade.html");
        }

        //数据库已经存在
        //确定是 jpress 的数据库
        //数据库不需要升级
        else if (InstallManager.me().isDbExist()
                && InstallManager.me().isJPressDb()
                && !InstallManager.me().isNeedUpgrade()) {
            render("/WEB-INF/install/views/step3_notinit.html");
        }

        //全新的数据库
        else {
            render("/WEB-INF/install/views/step3.html");
        }

    }

    public void error() {
        render("/WEB-INF/install/views/error.html");
    }


    private boolean createDatabase(String dbName, String dbUser, String dbPwd, String dbHost, int dbPort) {

        DbExecuter dbExecuter = null;
        try {
            dbExecuter = new DbExecuter("information_schema", dbUser, dbPwd, dbHost, dbPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //可能是该用户没有 information_schema 的权限
        if (dbExecuter == null) {
            return false;
        }


        try {
            List<String> dbs = dbExecuter.query("select SCHEMA_NAME from `SCHEMATA`");
            if (dbs != null && dbs.contains(dbName)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            dbExecuter.executeSql("CREATE DATABASE IF NOT EXISTS " + dbName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }


    public void install() {

        Ret ret;

        //数据库需要升级
        if (InstallManager.me().isDbExist()
                && InstallManager.me().isJPressDb()
                && InstallManager.me().isNeedUpgrade()) {

            ret = doProcessUpgrade();
        }

        //数据库已经存在
        //确定是 jpress 的数据库
        //数据库不需要升级
        else if (InstallManager.me().isDbExist()
                && InstallManager.me().isJPressDb()
                && !InstallManager.me().isNeedUpgrade()) {

            ret = doProcessReInstall();
        }

        //全新的数据库
        else {
            ret = doProcessInstall();
        }

        // 设置 JPress 的版本
        if (ret.isOk()){
            OptionService optionService = Aop.get(OptionService.class);
            optionService.saveOrUpdate("jpress_version", JPressConsts.VERSION);
            optionService.saveOrUpdate("jpress_version_code", JPressConsts.VERSION_CODE);
        }


        renderJson(ret);
    }

    private Ret doProcessUpgrade() {

        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isNotBlank(username)) {

            if (StrUtil.isBlank(pwd)) {
                return Ret.fail().set("message", "密码不能为空").set("errorCode", 3);
            }

            if (StrUtil.isBlank(confirmPwd)) {
                return Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4);
            }

            if (pwd.equals(confirmPwd) == false) {
                return Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5);
            }


            try {
                InstallManager.me().doUpgradeDatabase();
            } catch (SQLException e) {
                e.printStackTrace();
                return Ret.fail().set("message", e.getMessage());
            }


            initActiveRecordPlugin();

            initFirstUser();

        } else {
            initActiveRecordPlugin();
        }


        if (doFinishedInstall()) {
            return Ret.ok();
        } else {
            return Ret.fail().set("message", "classes目录没有写入权限，请查看服务器配置是否正确。");
        }

    }

    private Ret doProcessReInstall() {

        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isNotBlank(username)) {

            if (StrUtil.isBlank(pwd)) {
                return Ret.fail().set("message", "密码不能为空").set("errorCode", 3);
            }

            if (StrUtil.isBlank(confirmPwd)) {
                return Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4);
            }

            if (pwd.equals(confirmPwd) == false) {
                return Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5);
            }

            initActiveRecordPlugin();

            initFirstUser();

        } else {
            initActiveRecordPlugin();
        }


        if (doFinishedInstall()) {
            return Ret.ok();
        } else {
            return Ret.fail().set("message", "classes目录没有写入权限，请查看服务器配置是否正确。");
        }
    }

    private Ret doProcessInstall() {

        String webName = getPara("web_name");
        String webTitle = getPara("web_title");
        String webSubtitle = getPara("web_subtitle");

        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isBlank(webName)) {
            return Ret.fail().set("message", "网站名称不能为空");
        }

        if (StrUtil.isBlank(webTitle)) {
            return Ret.fail().set("message", "网站标题不能为空");
        }

        if (StrUtil.isBlank(webSubtitle)) {
            return Ret.fail().set("message", "网站副标题不能为空");
        }

        if (StrUtil.isBlank(username)) {
            return Ret.fail().set("message", "账号不能为空");
        }

        if (StrUtil.isBlank(pwd)) {
            return Ret.fail().set("message", "密码不能为空");
        }

        if (StrUtil.isBlank(confirmPwd)) {
            return Ret.fail().set("message", "确认密码不能为空");
        }

        if (pwd.equals(confirmPwd) == false) {
            return Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5);
        }

        try {
            InstallManager.me().doInitDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
            return Ret.fail().set("message", e.getMessage());
        }

        initActiveRecordPlugin();

        OptionService optionService = Aop.get(OptionService.class);
        optionService.saveOrUpdate("web_name", webName);
        optionService.saveOrUpdate("web_title", webTitle);
        optionService.saveOrUpdate("web_subtitle", webSubtitle);

        JPressOptions.set("web_name", webName);
        JPressOptions.set("web_title", webTitle);
        JPressOptions.set("web_subtitle", webSubtitle);

        initFirstUser();

        if (doFinishedInstall()) {
            return Ret.ok();
        } else {
            return Ret.fail().set("message", "classes目录没有写入权限，请查看服务器配置是否正确。");
        }

    }

    private void initFirstUser() {

        String username = getPara("username");
        String pwd = getPara("pwd");


        UserService userService = Aop.get(UserService.class);
        User user = userService.findById(1L);
        if (user == null) {
            user = new User();
            user.setNickname(username);
            user.setRealname(username);
            user.setCreateSource(User.SOURCE_WEB_REGISTER);
            user.setCreated(new Date());
            user.setActivated(new Date());
        }


        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        user.setSalt(salt);
        user.setPassword(hashedPass);

        user.setUsername(username);
        if (StrUtil.isEmail(username)) {
            user.setEmail(username.toLowerCase());
        }

        user.setStatus(User.STATUS_OK);
        userService.saveOrUpdate(user);


        RoleService roleService = Aop.get(RoleService.class);

        Role role = roleService.findById(1);
        if (role == null){
            role = new Role();
            role.setCreated(new Date());
        }
        role.setName("默认角色");
        role.setDescription("这个是系统自动创建的默认角色");
        role.setFlag(Role.ADMIN_FLAG);
        role.setModified(new Date());

        roleService.saveOrUpdate(role);

        Db.update("DELETE FROM `user_role_mapping` WHERE `user_id` = 1");
        Db.update("INSERT INTO `user_role_mapping` (`user_id`, `role_id`) VALUES (1, 1);");
    }


    private void initActiveRecordPlugin() {


        DataSourceConfig config = InstallManager.me().getDataSourceConfig();

        // 在只有 jboot.properties 但是没有 install.lock 的情况下
        // jboot启动的时候会出初始化 jboot.properties 里配置的插件
        // 此时，会出现 Config already exist 的异常
        if (DbKit.getConfig(DataSourceConfig.NAME_DEFAULT) == null) {
            config.setName(DataSourceConfig.NAME_DEFAULT);
        } else {
            config.setName(StrUtil.uuid());
        }

        DataSourceConfigManager.me().addConfig(config);

        ActiveRecordPlugin activeRecordPlugin = ArpManager.me().createRecordPlugin(config);
        activeRecordPlugin.start();
    }


    private boolean doFinishedInstall() {
        try {

            //创建 install.lock 安装锁定文件
            InstallUtil.createInstallLockFile();

            //创建 jboot.properties 数据库配置文件
            InstallUtil.createJbootPropertiesFile();

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        /**
         *  设置安装标识
         */
        Installer.setInstalled(true);

        /**
         * 通知安装监听器
         */
        Installer.notifyAllListeners();

        return true;
    }


}
