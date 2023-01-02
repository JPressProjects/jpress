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

import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.*;

import java.io.File;
import java.util.*;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 根据数据库信息，生成 maven module
 * @Package io.jpress.core.code
 */
public class ModuleGenerator {

    private String moduleName;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String optionsTables;
    private String sortTables = "";
    private String sortOptionsTables = "";
    private String dbTables;
    private String modelPackage;
    private String servicePackage;

    private String basePath;

    private boolean genUI = false;

    public ModuleGenerator(String moduleName, String dbUrl, String dbUser, String dbPassword, String dbTables, String modelPackage, String servicePackage) {
        this.moduleName = moduleName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = CodeGenKit.getProjectRootPath() + "/module-" + moduleName;
    }

    public ModuleGenerator(String moduleName, String dbUrl, String dbUser, String dbPassword, String dbTables, String optionsTables, String modelPackage, String servicePackage) {
        this.moduleName = moduleName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.optionsTables = optionsTables;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = CodeGenKit.getProjectRootPath() + "/module-" + moduleName;
    }


    public ModuleGenerator(String moduleName, String dbUrl, String dbUser, String dbPassword, String dbTables,String optionsTables, String sortTables, String sortOptionsTables,  String modelPackage, String servicePackage) {
        this.moduleName = moduleName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.optionsTables = optionsTables;
        this.sortTables = sortTables;
        this.sortOptionsTables = sortOptionsTables;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = CodeGenKit.getProjectRootPath() + "/module-" + moduleName;
    }


    public boolean isGenUI() {
        return genUI;
    }

    public ModuleGenerator setGenUI(boolean genUI) {
        this.genUI = genUI;
        return this;
    }

    public void gen() {

        genModule();
        genPomXml();
        genCode();
    }

    private void genModule() {
        String modelPath = basePath + "/module-" + moduleName + "-model";
        String webPath = basePath + "/module-" + moduleName + "-web";
        String serviceApiPath = basePath + "/module-" + moduleName + "-service";
        String serviceProviderPath = basePath + "/module-" + moduleName + "-service-provider";

        File modelFile = new File(modelPath);
        File webFile = new File(webPath);
        File serviceApiFile = new File(serviceApiPath);
        File serviceProviderFile = new File(serviceProviderPath);

        modelFile.mkdirs();
        webFile.mkdirs();
        serviceApiFile.mkdirs();
        serviceProviderFile.mkdirs();
    }

    private void genPomXml() {

        String modulePath = basePath;
        String modelPath = basePath + "/module-" + moduleName + "-model";
        String webPath = basePath + "/module-" + moduleName + "-web";
        String serviceApiPath = basePath + "/module-" + moduleName + "-service";
        String serviceProviderPath = basePath + "/module-" + moduleName + "-service-provider";


        File modelFile = new File(modelPath);
        File webFile = new File(webPath);
        File serviceApiFile = new File(serviceApiPath);
        File serviceProviderFile = new File(serviceProviderPath);

        makeSrcDirectory(modelFile);
        makeSrcDirectory(webFile);
        makeSrcDirectory(serviceApiFile);
        makeSrcDirectory(serviceProviderFile);

        Map map = new HashMap();
        map.put("moduleName", moduleName);
        Engine engine = new Engine();
        engine.setToClassPathSourceFactory();    // 从 class path 内读模板文件
        engine.addSharedMethod(new StrKit());

        File modulePomXmlFile = new File(modulePath, "pom.xml");
        if (!modulePomXmlFile.exists()) {
            engine.getTemplate("io/jpress/codegen/templates/pom_module_template.jf").render(map, modulePomXmlFile);
        }

        File modelPomXmlFile = new File(modelFile, "pom.xml");
        if (!modelPomXmlFile.exists()) {
            engine.getTemplate("io/jpress/codegen/templates/pom_model_template.jf").render(map, modelPomXmlFile);
        }

        File webPomXmlFile = new File(webFile, "pom.xml");
        if (!webPomXmlFile.exists()) {
            engine.getTemplate("io/jpress/codegen/templates/pom_web_template.jf").render(map, webPomXmlFile);
        }

        File serviceApiPomXmlFile = new File(serviceApiFile, "pom.xml");
        if (!serviceApiPomXmlFile.exists()) {
            engine.getTemplate("io/jpress/codegen/templates/pom_service_template.jf").render(map, serviceApiPomXmlFile);
        }

        File serviceProviderPomXmlFile = new File(serviceProviderFile, "pom.xml");
        if (!serviceProviderPomXmlFile.exists()) {
            engine.getTemplate("io/jpress/codegen/templates/pom_service_provider_template.jf").render(map, serviceProviderPomXmlFile);
        }

    }

    private void makeSrcDirectory(File file) {
        if (!file.isDirectory()) {
            return;
        }

        new File(file, "src/main/java").mkdirs();
        new File(file, "src/main/resources").mkdirs();
        new File(file, "src/test/java").mkdirs();
        new File(file, "src/test/resources").mkdirs();
    }


    private void genCode() {

        String modelModuleName = "/module-" + moduleName + "-model";
        String serviceApiModuleName = "/module-" + moduleName + "-service";
        String serviceProviderModuleName = "/module-" + moduleName + "-service-provider";

        JbootApplication.setBootArg("jboot.datasource.url", dbUrl);
        JbootApplication.setBootArg("jboot.datasource.user", dbUser);
        JbootApplication.setBootArg("jboot.datasource.password", dbPassword);

        String baseModelPackage = modelPackage + ".base";

        String modelDir = basePath + modelModuleName + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = basePath + modelModuleName + "/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate... dir:" + modelDir);


        Set<String> genTableNames = StrUtil.splitToSet(dbTables, ",");

        MetaBuilder mb = CodeGenHelpler.createMetaBuilder();
        mb.setGenerateRemarks(true);
        List<TableMeta> tableMetas = mb.build();

        tableMetas.removeIf(tableMeta -> genTableNames != null && !genTableNames.contains(tableMeta.name.toLowerCase()));


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetas);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetas);

        String apiPath = basePath + serviceApiModuleName + "/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = basePath + serviceProviderModuleName + "/src/main/java/" + servicePackage.replace(".", "/") + "/provider";

        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetas);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetas);
        if (genUI) {
            new ModuleUIGenerator(moduleName, modelPackage, tableMetas).genListener().genControllers().genEdit().genList();
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
