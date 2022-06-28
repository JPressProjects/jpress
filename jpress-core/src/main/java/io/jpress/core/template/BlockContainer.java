package io.jpress.core.template;

import java.util.HashSet;
import java.util.Set;

/**
 * 板块容器
 */
public class BlockContainer {

    // 容器 ID
    private String id;

    // 所在的模板文件
    private String templateFile;

    // 支持存放的板块 ID 列表
    private Set<String> supportBlocks;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTemplateFile() {
        return templateFile;
    }

    public void setTemplateFile(String templateFile) {
        this.templateFile = templateFile;
    }

    public Set<String> getSupportBlocks() {
        return supportBlocks;
    }

    public void setSupportBlocks(Set<String> supportBlocks) {
        this.supportBlocks = supportBlocks;
    }

    public void addSupportBlockId(String blockId){
        if (this.supportBlocks == null){
            this.supportBlocks = new HashSet<>();
        }
        this.supportBlocks.add(blockId);
    }

}
