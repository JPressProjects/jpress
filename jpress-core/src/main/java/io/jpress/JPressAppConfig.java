package io.jpress;

import io.jboot.config.annotation.PropertyConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 环境配置
 * @Package io.jpress
 */
@PropertyConfig(prefix = "io.jpress")
public class JPressAppConfig {

    private String indexAction = "/page";
    private String defaultTemplate = "jportal";


    public String getIndexAction() {
        return indexAction;
    }

    public void setIndexAction(String indexAction) {
        this.indexAction = indexAction;
    }

    public String getDefaultTemplate() {
        return defaultTemplate;
    }

    public void setDefaultTemplate(String defaultTemplate) {
        this.defaultTemplate = defaultTemplate;
    }
}
