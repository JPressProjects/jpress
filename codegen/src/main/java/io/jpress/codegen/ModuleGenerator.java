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

import com.jfinal.kit.Kv;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.BaseModelGenerator;
import io.jpress.codegen.generator.ModelGenerator;
import io.jpress.codegen.generator.ServiceApiGenerator;
import io.jpress.codegen.generator.ServiceProviderGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    private String dbTables;
    private String modelPackage;
    private String servicePackage;

    private String basePath;


    public ModuleGenerator(String moduleName, String dbUrl, String dbUser, String dbPassword, String dbTables, String modelPackage, String servicePackage) {
        this.moduleName = moduleName;
        this.dbUrl = dbUrl;
        this.dbUser = dbUser;
        this.dbPassword = dbPassword;
        this.dbTables = dbTables;
        this.modelPackage = modelPackage;
        this.servicePackage = servicePackage;
        this.basePath = PathKit.getWebRootPath() + "/../module-" + moduleName;
    }

    public void gen() {

        genModule();
        genPomXml();
        genCode();
        genModuleListener();

    }

    private void genModule() {
        String modelPath = basePath + "/module-" + moduleName + "-model";
        String webPath = basePath + "/module-" + moduleName + "-web";
        String serviceApiPath = basePath + "/module-" + moduleName + "-service-api";
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
        String serviceApiPath = basePath + "/module-" + moduleName + "-service-api";
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

        engine.getTemplate("io/jpress/codegen/templates/pom_module_template.jf").render(map, new File(modulePath, "pom.xml"));
        engine.getTemplate("io/jpress/codegen/templates/pom_model_template.jf").render(map, new File(modelFile, "pom.xml"));
        engine.getTemplate("io/jpress/codegen/templates/pom_web_template.jf").render(map, new File(webFile, "pom.xml"));
        engine.getTemplate("io/jpress/codegen/templates/pom_service_api_template.jf").render(map, new File(serviceApiFile, "pom.xml"));
        engine.getTemplate("io/jpress/codegen/templates/pom_service_provider_template.jf").render(map, new File(serviceProviderFile, "pom.xml"));


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
        String serviceApiModuleName = "/module-" + moduleName + "-service-api";
        String serviceProviderModuleName = "/module-" + moduleName + "-service-provider";

        JbootApplication.setBootArg("jboot.datasource.url", dbUrl);
        JbootApplication.setBootArg("jboot.datasource.user", dbUser);
        JbootApplication.setBootArg("jboot.datasource.password", dbPassword);

        String baseModelPackage = modelPackage + ".base";

        String modelDir = basePath + modelModuleName + "/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = basePath + modelModuleName + "/src/main/java/" + baseModelPackage.replace(".", "/");

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

        String apiPath = basePath + serviceApiModuleName + "/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = basePath + serviceProviderModuleName + "/src/main/java/" + servicePackage.replace(".", "/") + "/provider";

        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetaList);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetaList);

    }

    private void genModuleListener() {
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


        System.out.println("Generate  module listener ...");
        System.out.println("Module listener Output Dir: ");

        Engine engine = Engine.create("forModuleListener");
        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());


        Kv data = Kv.by("forModuleListener", moduleName);

        String upcasedModuleName = (new StringBuilder()).append(Character.toUpperCase(moduleName.charAt(0))).append(moduleName.substring(1)).toString();
        data.set("moduleName", moduleName);
        data.set("upcasedModuleName", upcasedModuleName);
        data.set("modelPackage", modelPackage);
        String template = "/io/jpress/codegen/templates/module_listener_template.jf";
        String moduleListenerContent = engine.getTemplate(template).renderToString(data);


        String webPath = basePath + "/module-" + moduleName + "-web";
        String moduleListenerPakcage = modelPackage.substring(0, modelPackage.lastIndexOf("."));
        String path = webPath + "/src/main/java/" + moduleListenerPakcage.replace(".", "/");


        System.out.println("Generate  module listener for " + path);

        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String target = path + File.separator + upcasedModuleName + "ModuleListener" + ".java";

        File targetFile = new File(target);
        if (targetFile.exists()) {
            return;
        }
        try {
            FileWriter fw = new FileWriter(target);
            try {
                fw.write(moduleListenerContent);
            } finally {
                fw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
