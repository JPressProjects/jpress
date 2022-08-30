package io.jpress;

import io.jboot.apidoc.ApiDocConfig;
import io.jboot.apidoc.ApiDocManager;

public class ApiDocGenerator {

    public static void main(String[] args) {

        ApiDocConfig config = new ApiDocConfig();
        config.setBasePath("../doc/development/api");
        

        ApiDocManager.me().genDocs(config);
    }
}
