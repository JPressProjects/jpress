package io.jpress.service;

import io.jboot.codegen.service.JbootServiceGenerator;


public class ServiceGen {

    public static void main(String[] args) {

        String basePackage = "io.jpress.service";
        String modelPackage = "io.jpress.model";

        JbootServiceGenerator.run(basePackage, modelPackage);
    }
}
