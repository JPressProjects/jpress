package io.jpress.model;

import io.jboot.codegen.model.JbootModelGenerator;


public class ModelGen {

    public static void main(String[] args) {

        String modelPackage = "io.jpress";

        JbootModelGenerator.run(modelPackage);
    }
}
