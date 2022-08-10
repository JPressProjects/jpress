package io.jpress.modulegen;

import io.jpress.codegen.ModuleGenerator;

/**
 * @program: JobModuleGenerator
 * @description: job模块构建
 * @create: 2022/6/22 10:39
 **/
public class JobModuleGenerator {

    private static String dbUrl = "jdbc:mysql://192.168.3.2:3306/jpress";
    private static String dbUser = "root";
    private static String dbPassword = "123456";

    private static String moduleName = "job";
    private static String dbTables = "job,job_category,job_apply";
    private static String optionsTables = "job";
    private static String sortTables = "";
    private static String sortOptionsTables = "job_category";
    private static String modelPackage = "io.jpress.module.job.model";
    private static String servicePackage = "io.jpress.module.job.service";


    public static void main(String[] args) {

        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleName, dbUrl, dbUser, dbPassword, dbTables, optionsTables,sortTables,sortOptionsTables, modelPackage, servicePackage);
        moduleGenerator.setGenUI(true).gen();

    }
}
