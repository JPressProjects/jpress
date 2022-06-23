package io.jpress.core.template;

import java.util.List;

public class BlockInfo {

    // id
    private String id;

    //文件名称
    private String fileName;

    //标题
    private String title;

    //背景图片
    private String icon;

    //支持的配置内容
    private List<BlockOption> options;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
