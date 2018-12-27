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
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import io.jboot.db.JbootDbManager;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.core.install.InstallUtils;
import io.jpress.core.install.JPressInstaller;
import io.jpress.model.User;
import io.jpress.service.OptionService;
import io.jpress.service.RoleService;
import io.jpress.service.UserService;

import javax.inject.Inject;
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
public class InstallController extends JbootController {

    @Inject
    private UserService userService;

    @Inject
    private RoleService roleService;

    @Inject
    private OptionService optionService;

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
        if (InstallUtils.isInitBefore()) {
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

        if (StrUtils.isBlank(dbName)) {
            renderJson(Ret.fail().set("message", "数据库名不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtils.isBlank(dbUser)) {
            renderJson(Ret.fail().set("message", "用户名不能为空").set("errorCode", 2));
            return;
        }

        if (StrUtils.isBlank(dbHost)) {
            renderJson(Ret.fail().set("message", "主机不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtils.isBlank(dbPort)) {
            renderJson(Ret.fail().set("message", "端口号不能为空").set("errorCode", 4));
            return;
        }

        try {
            InstallUtils.init(dbName, dbUser, dbPwd, dbHost, dbPort);

            List<String> tables = InstallUtils.getTableList();

            if (ArrayUtils.isNotEmpty(tables)
                    && tables.contains("attachment")
                    && tables.contains("option")
                    && tables.contains("menu")
                    && tables.contains("permission")
                    && tables.contains("role")
                    && tables.contains("user")
                    && tables.contains("utm")) {

                InstallUtils.setInitBefore(true);
                renderJson(Ret.ok());
                return;
            }

            if (ArrayUtils.isNotEmpty(tables)) {
                renderJson(Ret.fail("message", "无法安装，该数据库已有表信息了，为了安全起见，请选择全新的数据库进行安装。")
                        .set("errorCode", 5));
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
            renderJson(Ret.fail());
            return;
        }

        renderJson(Ret.ok());
    }


    public void install() {

        if (InstallUtils.isInitBefore()) {
            doProcessInitBefore();
        } else {
            doProcessNormal();
        }

    }

    private void doProcessInitBefore() {


        String username = getPara("username");
        String pwd = getPara("pwd");
        String confirmPwd = getPara("confirmPwd");


        if (StrUtils.isNotBlank(username)) {

            if (StrUtils.isBlank(pwd)) {
                renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
                return;
            }

            if (StrUtils.isBlank(confirmPwd)) {
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


            if (StrUtils.isEmail(username)) {
                user.setEmail(username.toLowerCase());
            }

            userService.saveOrUpdate(user);

        } else {
            initActiveRecordPlugin();
        }


        if (doCreatedInstallLockFiles()) {
            renderJson(Ret.ok());
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


        if (StrUtils.isBlank(webName)) {
            renderJson(Ret.fail().set("message", "网站名称不能为空").set("errorCode", 10));
            return;
        }

        if (StrUtils.isBlank(webTitle)) {
            renderJson(Ret.fail().set("message", "网站标题不能为空").set("errorCode", 11));
            return;
        }

        if (StrUtils.isBlank(webSubtitle)) {
            renderJson(Ret.fail().set("message", "网站副标题不能为空").set("errorCode", 12));
            return;
        }


        if (StrUtils.isBlank(username)) {
            renderJson(Ret.fail().set("message", "账号不能为空").set("errorCode", 1));
            return;
        }

        if (StrUtils.isBlank(pwd)) {
            renderJson(Ret.fail().set("message", "密码不能为空").set("errorCode", 3));
            return;
        }

        if (StrUtils.isBlank(confirmPwd)) {
            renderJson(Ret.fail().set("message", "确认密码不能为空").set("errorCode", 4));
            return;
        }

        if (pwd.equals(confirmPwd) == false) {
            renderJson(Ret.fail().set("message", "两次输入密码不一致").set("errorCode", 5));
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


        if (StrUtils.isEmail(username)) {
            user.setEmail(username.toLowerCase());
        }

        userService.saveOrUpdate(user);
        roleService.initWebRole();

        if (doCreatedInstallLockFiles()) {
            renderJson(Ret.ok());
        } else {
            renderJson(Ret.fail().set("message", "classes目录没有写入权限，请查看服务器配置是否正确。"));
        }

    }


    private void initActiveRecordPlugin() {
        try {
            /**
             * 尝试去初始化数据库
             * 备注：如果检测之前数据库已经有表了，说明该数据库已经被初始化过了，不会再初始化
             */
            InstallUtils.tryInitJPressTables();
        } catch (SQLException e) {
            e.printStackTrace();
            renderJson(Ret.fail());
            return;
        }

        DataSourceConfig config = InstallUtils.getDataSourceConfig();
        config.setName(DataSourceConfig.NAME_DEFAULT);

        ActiveRecordPlugin activeRecordPlugin = JbootDbManager.me().createRecordPlugin(config);
        activeRecordPlugin.start();
    }


    private boolean doCreatedInstallLockFiles() {
        try {
            File lockFile = InstallUtils.lockFile();
            lockFile.createNewFile();

            InstallUtils.initJpressProperties();

        } catch (IOException e) {
            e.printStackTrace();

            return false;
        }


        JPressInstaller.setInstalled(true);
        JPressInstaller.notifyAllListeners();
        return true;
    }


}
