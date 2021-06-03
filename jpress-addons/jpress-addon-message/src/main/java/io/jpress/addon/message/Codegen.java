package io.jpress.addon.message;

import io.jpress.codegen.AddonGenerator;

public class Codegen {

    private static String dbUrl = "jdbc:mysql://127.0.0.1:3306/jpress3dev";
    private static String dbUser = "root";
    private static String dbPassword = "123456";

    private static String addonName = "message";
    private static String dbTables = "jpress_addon_message";
    private static String modelPackage = "io.jpress.addon.message.model";
    private static String servicePackage = "io.jpress.addon.message.service";


    public static void main(String[] args) {

        AddonGenerator moduleGenerator = new AddonGenerator(addonName, dbUrl, dbUser, dbPassword, dbTables, modelPackage, servicePackage);
//        moduleGenerator.setGenUI(true);
        moduleGenerator.gen();

    }

}
