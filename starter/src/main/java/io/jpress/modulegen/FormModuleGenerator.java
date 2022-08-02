package io.jpress.modulegen;

import io.jpress.codegen.ModuleGenerator;

/**
 * @program: JobModuleGenerator
 * @description: job模块构建
 * @create: 2022/6/22 10:39
 **/
public class FormModuleGenerator {

    private static String dbUrl = "jdbc:mysql://192.168.3.2:3306/jpress";
    private static String dbUser = "root";
    private static String dbPassword = "123456";

    private static String moduleName = "form";
    private static String dbTables = "form_datasource,form_datasource_item,form_info";
    private static String optionsTables = "form";
    private static String sortTables = "";
    private static String sortOptionsTables = "";
    private static String modelPackage = "io.jpress.module.form.model";
    private static String servicePackage = "io.jpress.module.form.service";


    public static void main(String[] args) {

        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleName, dbUrl, dbUser, dbPassword, dbTables, optionsTables,sortTables,sortOptionsTables, modelPackage, servicePackage);
        moduleGenerator.setGenUI(false).gen();

    }
}
