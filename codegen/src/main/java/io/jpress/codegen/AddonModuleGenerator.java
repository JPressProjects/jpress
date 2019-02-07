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
package io.jpress.codegen;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.BaseModelGenerator;
import io.jpress.codegen.generator.ModelGenerator;
import io.jpress.codegen.generator.ServiceApiGenerator;
import io.jpress.codegen.generator.ServiceProviderGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 根据数据库信息，生成 maven module
 * @Package io.jpress.core.code
 */
public class AddonModuleGenerator {

    private String addonName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbTables;
    private String modelPackage;
    private String servicePackage;

    private String basePath;


    public AddonModuleGenerator(String addonName, String dbUrl, String dbUser, String dbPassword, String dbTables, String modelPackage, String servicePackage) {
        this.addonName = addonName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = PathKit.getWebRootPath() + "/../jpress-addon-" + addonName;
    }

    public void gen() {

        genCode();

    }



    private void genCode() {


        JbootApplication.setBootArg("jboot.datasource.url", dbUrl);
        JbootApplication.setBootArg("jboot.datasource.user", dbUser);
        JbootApplication.setBootArg("jboot.datasource.password", dbPassword);

        String baseModelPackage = modelPackage + ".base";

        String modelDir = basePath  + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = basePath  + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate... dir:" + modelDir);

        List<TableMeta> tableMetaList = CodeGenHelpler.createMetaBuilder().build();
        if (StrUtil.isNotBlank(dbTables)) {
            List<TableMeta> newTableMetaList = new ArrayList<TableMeta>();
            Set<String> excludeTableSet = StrUtil.splitToSet(dbTables, ",");
            for (TableMeta tableMeta : tableMetaList) {
                if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
                    newTableMetaList.add(tableMeta);
                }
            }
            tableMetaList.clear();
            tableMetaList.addAll(newTableMetaList);
        }


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        String apiPath = basePath  + "/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = basePath  + "/src/main/java/" + servicePackage.replace(".", "/") + "/provider";

        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetaList);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetaList);

    }
}
