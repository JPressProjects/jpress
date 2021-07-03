package io.jpress;

import io.jboot.apidoc.ApiJsonGenerator;

public class ApiJsonConfigGenerator {

    public static void main(String[] args) {

        ApiJsonGenerator.JsonGeneratorConfig config = new ApiJsonGenerator.JsonGeneratorConfig();
        config.setJsonFilePath("src/main/resources/api-remarks.json");
        ApiJsonGenerator.genRemarksJson(config);

        config.setJsonFilePath("src/main/resources/api-mock.json");
        ApiJsonGenerator.genMockJson(config);
    }
}
