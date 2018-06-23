package io.jpress;

import io.jboot.config.annotation.PropertyConfig;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 环境配置
 * @Package io.jpress
 */
@PropertyConfig(prefix = "io.jpress")
public class JPressConfig {

    private String indexAction = "/page";


    public String getIndexAction() {
        return indexAction;
    }

    public void setIndexAction(String indexAction) {
        this.indexAction = indexAction;
    }
}
