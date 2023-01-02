/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 根据数据库信息，生成 maven module
 * @Package io.jpress.core.code
 */
public class AddonGenerator {

    private String addonName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbTables;
    private String optionsTables;
    private String sortTables = "";
    private String sortOptionsTables = "";
    private String modelPackage;
    private String servicePackage;

    private String basePath;

    private boolean genUI = false;


    public AddonGenerator(String addonName, String dbUrl, String dbUser, String dbPassword, String dbTables, String modelPackage, String servicePackage) {
        this.addonName = addonName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = CodeGenKit.getProjectRootPath() + "/jpress-addon-" + addonName;
    }


    public AddonGenerator(String addonName, String dbUrl, String dbUser, String dbPassword, String dbTables, String optionsTables, String modelPackage, String servicePackage) {
        this.addonName = addonName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.optionsTables = optionsTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = CodeGenKit.getProjectRootPath() + "/jpress-addon-" + addonName;
    }


    public AddonGenerator(String addonName, String dbUrl, String dbUser, String dbPassword, String dbTables, String optionsTables, String sortTables, String sortOptionsTables, String modelPackage, String servicePackage) {
        this.addonName = addonName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.optionsTables = optionsTables;
        this.sortTables = sortTables;
        this.sortOptionsTables = sortOptionsTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = CodeGenKit.getProjectRootPath() + "/jpress-addons/jpress-addon-" + addonName;
    }



    public boolean isGenUI() {
        return genUI;
    }

    public AddonGenerator setGenUI(boolean genUI) {
        this.genUI = genUI;
        return this;
    }

    public void gen() {

        genCode();

    }


    private void genCode() {


        JbootApplication.setBootArg("jboot.datasource.url", dbUrl);
        JbootApplication.setBootArg("jboot.datasource.user", dbUser);
        JbootApplication.setBootArg("jboot.datasource.password", dbPassword);

        String baseModelPackage = modelPackage + ".base";

        String modelDir = basePath + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = basePath + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate... dir:" + modelDir);

        MetaBuilder metaBuilder = CodeGenHelpler.createMetaBuilder();
        metaBuilder.setGenerateRemarks(true);
        List<TableMeta> tableMetas = metaBuilder.build();

        Set<String> genTableNames = StrUtil.splitToSet(dbTables, ",");
        tableMetas.removeIf(tableMeta -> genTableNames != null && !genTableNames.contains(tableMeta.name.toLowerCase()));


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetas);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetas);

        String apiPath = basePath + "/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = basePath + "/src/main/java/" + servicePackage.replace(".", "/") + "/provider";

        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetas);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetas);

        if (genUI) {
            new AddonUIGenerator(basePath, addonName, modelPackage, tableMetas)
                    .genControllers()
                    .genEdit()
                    .genList();
        }

        Set<String> optionsTableNames = StrUtil.splitToSet(optionsTables, ",");
        if (optionsTableNames != null && optionsTableNames.size() > 0) {
            new BaseOptionsModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,optionsTableNames));
        }


        //SortTables
        Set<String> sortTableNames = StrUtil.splitToSet(sortTables, ",");
        if (sortTableNames != null && sortTableNames.size() > 0) {
            new BaseSortModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,sortTableNames));
        }

        //SortOptionsTables
        Set<String> sortOptionsTableNames = StrUtil.splitToSet(sortOptionsTables, ",");
        if (sortOptionsTableNames != null && sortOptionsTableNames.size() > 0) {
            new BaseSortOptionsModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,sortOptionsTableNames));
        }
    }


    private  static List<TableMeta> copyTableMetasByNames(List<TableMeta> tableMetas,Set<String> names){
        List<TableMeta> retList = new ArrayList<>();
        tableMetas.forEach(tableMeta -> {
            if (names.contains(tableMeta.name.toLowerCase())){
                retList.add(tableMeta);
            }
        });
        return retList;
    }
}
