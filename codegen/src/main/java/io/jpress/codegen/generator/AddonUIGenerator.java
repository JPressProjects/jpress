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
package io.jpress.codegen.generator;

import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import com.jfinal.template.Engine;
import com.jfinal.template.source.ClassPathSourceFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class AddonUIGenerator {

        private String moduleName;//product
    private String modulePackage;//io.jpress.module.product

    private String modelPackage;//io.jpress.module.product.model


    private String templatesDir = "io/jpress/codegen/templates/";

    private Kv data;
    private String[] templates = { "ui_controller_template_for_addon.jf", "ui_edit_template.jf", "ui_list_template.jf"};
    public static final int UI_CONTROLLER = 1;
    public static final int UI_EDIT = 2;
    public static final int UI_LIST = 3;


    private String controllerOutputDir;
    private String htmlOutputDir;

    private String targetTemplate;
    private String targetOutputDirFile;

    private List<TableMeta> tableMetaList;

    private String basePath;

    private Engine engine = Engine.create("forUI");

    public static void main(String[] args) {

    }

    public AddonUIGenerator(String basePath, String moduleName, String modelPackage, List<TableMeta> tableMetaList) {

        this.basePath = basePath;

        this.tableMetaList = tableMetaList;
        this.moduleName = moduleName;
        this.modelPackage = modelPackage;
        this.modulePackage = modelPackage.substring(0, modelPackage.lastIndexOf("."));

        modulePackage = modelPackage.substring(0, modelPackage.lastIndexOf("."));

        String moduleListenerPakcage = modelPackage.substring(0, modelPackage.lastIndexOf("."));
        String controllerPackage = modelPackage.substring(0, modelPackage.lastIndexOf(".")) + ".controller";


        controllerOutputDir = basePath + "/src/main/java/" + controllerPackage.replace(".", "/");
        htmlOutputDir = basePath + "/src/main/webapp/views/";

        data = Kv.by("moduleName", moduleName);//product
        data.set("upcasedModuleName", StrKit.firstCharToUpperCase(moduleName));//Product
        data.set("modulePackage", modulePackage);//io.jpress.module.product
        data.set("modelPackage", modelPackage);//io.jpress.module.product.model
        data.set("moduleListenerPakcage", moduleListenerPakcage);//io.jpress.module.product
        data.set("controllerPackage", controllerPackage);//io.jpress.module.product.controller

        engine.setSourceFactory(new ClassPathSourceFactory());
        engine.addSharedMethod(new StrKit());

    }


    public AddonUIGenerator genControllers() {
        generate(AddonUIGenerator.UI_CONTROLLER);
        return this;
    }

    public AddonUIGenerator genEdit() {
        generate(AddonUIGenerator.UI_EDIT);
        return this;
    }

    public AddonUIGenerator genList() {
        generate(AddonUIGenerator.UI_LIST);
        return this;
    }

    public void generate(int genType) {

        String targetOutputDir = "";

        for (TableMeta tableMeta : tableMetaList) {
            data.set("tableMeta", tableMeta);
            String lowerCaseModelName = StrKit.firstCharToLowerCase(tableMeta.modelName);
            data.set("lowerCaseModelName", lowerCaseModelName);

            if (AddonUIGenerator.UI_CONTROLLER == genType) {
                targetTemplate = templatesDir + templates[0];
                targetOutputDir = controllerOutputDir;
                targetOutputDirFile = targetOutputDir + File.separator + "_" + tableMeta.modelName + "Controller" + ".java";
            }

            if (AddonUIGenerator.UI_EDIT == genType) {
                targetTemplate = templatesDir + templates[1];
                targetOutputDir = htmlOutputDir;
                targetOutputDirFile = targetOutputDir + File.separator + tableMeta.name + "_edit.html";
            }
            if (AddonUIGenerator.UI_LIST == genType) {
                targetTemplate = templatesDir + templates[2];
                targetOutputDir = htmlOutputDir;
                targetOutputDirFile = targetOutputDir + File.separator + tableMeta.name + "_list.html";
            }
            // tableMeta.columnMetas.get(0).remarks
            String content = engine.getTemplate(targetTemplate).renderToString(data);

            //
            File dir = new File(targetOutputDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File targetFile = new File(targetOutputDirFile);
            if (targetFile.exists()) {
                return;
            }
            try {
                FileWriter fw = new FileWriter(targetOutputDirFile);
                try {
                    fw.write(content);
                } finally {
                    fw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
