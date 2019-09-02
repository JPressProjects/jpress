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

import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.DbKit;
import io.jboot.db.JbootDbManager;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.db.datasource.DataSourceConfigManager;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.core.install.DbUtil;
import io.jpress.core.install.InstallUtil;
import io.jpress.core.install.Installer;
import io.jpress.model.User;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;
import io.jpress.web.base.ControllerBase;

import java.io.File;
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

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private OptionService optionService;

    @Inject
    private InstallUtil installUtil;

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

    public void step3() {

        //已经安装过了
        if (installUtil.isInitBefore()) {
            render("/WEB-INF/install/views/step3_notinit.html");
        } else {
            render("/WEB-INF/install/views/step3.html");
        }

    }

    public void error() {
        render("/WEB-INF/install/views/error.html");
    }

    public void initdb() {

        String dbName = getPara("dbName");
        String dbUser = getPara("dbUser");
        String dbPwd = getPara("dbPwd");
        String dbHost = getPara("dbHost");
        String dbPort = getPara("dbPort");

        if (StrUtil.isBlank(dbName)) {
            renderJson(Ret.fail().set("message", "数据库名不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtil.isBlank(dbUser)) {
            renderJson(Ret.fail().set("message", "用户名不能为空").set("errorCode", 2));
            return;
        }

        if (StrUtil.isBlank(dbHost)) {
            renderJson(Ret.fail().set("message", "主机不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtil.isBlank(dbPort)) {
            renderJson(Ret.fail().set("message", "端口号不能为空").set("errorCode", 4));
            return;
        }

        boolean dbAutoCreate = getParaToBoolean("dbAutoCreate", false);
        if (dbAutoCreate) {
            if (!createDatabaseIfNeecessary(dbName, dbUser, dbPwd, dbHost, dbPort)) {
                renderJson(Ret.fail().set("message", "无法自动创建数据库，可能是用户名密码错误，或没有权限").set("errorCode", 5));
                return;
            }
        }


        try {

            installUtil.init(dbName, dbUser, dbPwd, dbHost, dbPort);

            if (installUtil.isInitBefore()) {
                renderOkJson();
                return;
            }

            if (!installUtil.isJpressDb()) {
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

    private boolean createDatabaseIfNeecessary(String dbName, String dbUser, String dbPwd, String dbHost, String dbPort) {

        DbUtil dbUtil = null;
        try {
            dbUtil = new DbUtil("information_schema", dbUser, dbPwd, dbHost, dbPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //可能是该用户没有 information_schema 的权限
        if (dbUtil == null) {
            return false;
        }


        try {
            List<String> dbs = dbUtil.query("select SCHEMA_NAME from `SCHEMATA`");
            if (dbs != null && dbs.contains(dbName)) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try {
            dbUtil.executeSql("CREATE DATABASE IF NOT EXISTS " + dbName + " DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;

    }


    public void install() {

        if (installUtil.isInitBefore()) {
            doProcessInOldDb();
        } else {
            doProcessNormal();
        }

    }

    private void doProcessInOldDb() {

        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");

        if (StrUtil.isNotBlank(username)) {

            if (StrUtil.isBlank(pwd)) {
                renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
                return;
            }

            if (StrUtil.isBlank(confirmPwd)) {
                renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
                return;
            }

            if (pwd.equals(confirmPwd) == false) {
                renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
                return;
            }

            initActiveRecordPlugin();

            String salt = HashKit.generateSaltForSha256();
            String hashedPass = HashKit.sha256(salt + pwd);

            User user = userService.findById(1l);
            if (user == null) user = new User();

            user.setUsername(username);
            user.setNickname(username);
            user.setRealname(username);

            user.setSalt(salt);
            user.setPassword(hashedPass);
            user.setCreated(new Date());
            user.setActivated(new Date());
            user.setStatus(User.STATUS_OK);
            user.setCreateSource(User.SOURCE_WEB_REGISTER);

            if (StrUtil.isEmail(username)) {
                user.setEmail(username.toLowerCase());
            }

            userService.saveOrUpdate(user);

        } else {
            initActiveRecordPlugin();
        }


        if (doCreatedInstallLockFiles()) {
            renderOkJson();
        } else {
            renderJson(Ret.fail().set("message", "classes目录没有写入权限，请查看服务器配置是否正确。"));
        }

    }

    private void doProcessNormal() {

        String webName = getPara("web_name");
        String webTitle = getPara("web_title");
        String webSubtitle = getPara("web_subtitle");

        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");


        if (StrUtil.isBlank(webName)) {
            renderJson(Ret.fail().set("message", "网站名称不能为空").set("errorCode", 10));
            return;
        }

        if (StrUtil.isBlank(webTitle)) {
            renderJson(Ret.fail().set("message", "网站标题不能为空").set("errorCode", 11));
            return;
        }

        if (StrUtil.isBlank(webSubtitle)) {
            renderJson(Ret.fail().set("message", "网站副标题不能为空").set("errorCode", 12));
            return;
        }

        if (StrUtil.isBlank(username)) {
            renderJson(Ret.fail().set("message", "账号不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtil.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtil.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
            return;
        }

        try {
            installUtil.initJPressTables();
        } catch (SQLException e) {
            e.printStackTrace();
            renderFailJson();
            return;
        }

        initActiveRecordPlugin();

        optionService.saveOrUpdate("web_name", webName);
        optionService.saveOrUpdate("web_title", webTitle);
        optionService.saveOrUpdate("web_subtitle", webSubtitle);

        JPressOptions.set("web_name", webName);
        JPressOptions.set("web_title", webTitle);
        JPressOptions.set("web_subtitle", webSubtitle);


        String salt = HashKit.generateSaltForSha256();
        String hashedPass = HashKit.sha256(salt + pwd);

        User user = userService.findById(1l);
        if (user == null) user = new User();

        user.setUsername(username);
        user.setNickname(username);
        user.setRealname(username);

        user.setSalt(salt);
        user.setPassword(hashedPass);
        user.setCreated(new Date());
        user.setActivated(new Date());
        user.setStatus(User.STATUS_OK);
        user.setCreateSource(User.SOURCE_WEB_REGISTER);


        if (StrUtil.isEmail(username)) {
            user.setEmail(username.toLowerCase());
        }

        userService.saveOrUpdate(user);
        roleService.initWebRole();

        if (doCreatedInstallLockFiles()) {
            renderOkJson();
        } else {
            renderJson(Ret.fail().set("message", "classes目录没有写入权限，请查看服务器配置是否正确。"));
        }

    }


    private void initActiveRecordPlugin() {


        DataSourceConfig config = installUtil.getDataSourceConfig();

        // 在只有 jboot.properties 但是没有 install.lock 的情况下
        // jboot启动的时候会出初始化 jboot.properties 里配置的插件
        // 此时，会出现 Config already exist 的异常
        if (DbKit.getConfig(DataSourceConfig.NAME_DEFAULT) == null) {
            config.setName(DataSourceConfig.NAME_DEFAULT);
        } else {
            config.setName(StrUtil.uuid());
        }

        DataSourceConfigManager.me().addConfig(config);

        ActiveRecordPlugin activeRecordPlugin = JbootDbManager.me().createRecordPlugin(config);
        activeRecordPlugin.start();
    }


    private boolean doCreatedInstallLockFiles() {
        try {
            File lockFile = installUtil.getLockFile();
            lockFile.createNewFile();

            installUtil.initJpressProperties();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }

        Installer.setInstalled(true);
        Installer.notifyAllListeners();

        return true;
    }


}
