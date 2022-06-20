package io.jpress.core.template;

import java.util.ArrayList;
import java.util.List;

/**
 * 模板文件
 */
public class TemplateFile {

    //文件名称
    private String fileName;

    //标题
    private String title;

    //容器
    private List<SectionContainer> containers;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<SectionContainer> getContainers() {
        return containers;
    }

    public void setContainers(List<SectionContainer> containers) {
        this.containers = containers;
    }

    public void addContainer(SectionContainer container){
        if (this.containers == null){
            this.containers = new ArrayList<>();
        }

        this.containers.add(container);
    }
}
