/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.install;


import com.jfinal.kit.PathKit;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log;
import io.jboot.Jboot;
import io.jboot.support.jwt.JwtConfig;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.StrUtil;
import io.jboot.web.JbootWebConfig;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.core.install.DbExecuter;
import io.jpress.core.install.InstallManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class InstallUtil {


    private static final Log log = Log.getLog(InstallUtil.class);

    public static void createInstallLockFile() throws IOException {
        File lockFile = new File(PathKit.getRootClassPath(), "install.lock");
        lockFile.createNewFile();
    }



    public static boolean createJbootPropertiesFile() {

        File propertieFile = new File(PathKit.getRootClassPath(), "jboot.properties");
        DbExecuter dbExecuter = InstallManager.me().getDbExecuter();

        Properties p = propertieFile.exists()
                ? PropKit.use("jboot.properties").getProperties()
                : new Properties();


        //jboot.app.mode
        putPropertie(p, "jboot.app.mode", "product");


        //cookieEncryptKey
        String cookieEncryptKey = StrUtil.uuid();
        if (putPropertie(p, "jboot.web.cookieEncryptKey", cookieEncryptKey)) {
            Jboot.config(JbootWebConfig.class).setCookieEncryptKey(cookieEncryptKey);
            CookieUtil.initEncryptKey(cookieEncryptKey);
        }

        //jwtSecret
        String jwtSecret = StrUtil.uuid();
        if (putPropertie(p, "jboot.web.jwt.secret", jwtSecret)) {
            Jboot.config(JwtConfig.class).setSecret(jwtSecret);
        }

        p.put("jboot.datasource.type", "mysql");
        p.put("jboot.datasource.url", dbExecuter.getJdbcUrl());
        p.put("jboot.datasource.user", dbExecuter.getDbUser());
        p.put("jboot.datasource.password", StrUtil.obtainDefault(dbExecuter.getDbPassword(), ""));

        return savePropertie(p, propertieFile);
    }

    private static boolean putPropertie(Properties p, String key, String value) {
        Object v = p.get(key);
        if (v == null) {
            p.put(key, value);
            return true;
        }

        return false;
    }


    private static boolean savePropertie(Properties p, File pFile) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(pFile);
            p.store(fos, "Auto create by JPress");
        } catch (Exception e) {
            log.warn(e.toString(), e);
            return false;
        } finally {
            CommonsUtils.quietlyClose(fos);
        }
        return true;
    }
}
