package io.jpress.core.template;

import java.util.Set;

/**
 * 板块容器
 */
public class SectionContainer {

    private TemplateFile templateFile;

    // 容器 ID
    private String id;

    // 支持存放的板块 ID 列表
    private Set<String> supports;

    // 是否支持系统组件
    private boolean isSupportSystemSection = true;

    public TemplateFile getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(TemplateFile templateFile) {
        this.templateFile = templateFile;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<String> getSupports() {
        return supports;
    }

    public void setSupports(Set<String> supports) {
        this.supports = supports;
    }

    public boolean isSupportSystemSection() {
        return isSupportSystemSection;
    }

    public void setSupportSystemSection(boolean supportSystemSection) {
        isSupportSystemSection = supportSystemSection;
    }
}
