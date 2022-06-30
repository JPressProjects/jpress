package io.jpress.core.template;

import io.jboot.utils.StrUtil;
import io.jpress.core.template.editor.BsFormComponent;

import java.util.ArrayList;
import java.util.List;

public class BlockHtml {

    // id
    private String id;

    //文件名称
    private String fileName;

    //标题
    private String title;

    //背景图片
    private String icon;

    //排序
    private String index;

    //模板内容
    private String template;

    //支持的配置内容
    private List<BlockHtmlOptionDef> optionDefs;


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
        return StrUtil.isNotBlank(title) ? title : id;
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

    public void addOptionDef(BlockHtmlOptionDef optionDef) {
        if (optionDefs == null) {
            optionDefs = new ArrayList<>();
        }
        optionDefs.add(optionDef);
    }

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public void addTemplateLine(String line) {
        if (template == null) {
            template = "";
        }
        template += line;
    }



    public BsFormComponent toBsFormComponent() {

        BsFormComponent component = new BsFormComponent();
        component.setName(getTitle());
        component.setTag(id);

//        String template = this.template.trim();

        //必须是 html 开头
//        if (!template.startsWith("<")){
//            template = "<div>" + template +"</div>";
//        }
//        component.setTemplate(template);

        component.setDragIcon(icon);
        component.setDragTitle(getTitle());
        component.setDragIndex(index);

        if (optionDefs != null) {
            for (BlockHtmlOptionDef optionDef : optionDefs) {
                component.addProp(optionDef.toBsFormComponentPorp());
            }
        }

        return component;
    }
}
