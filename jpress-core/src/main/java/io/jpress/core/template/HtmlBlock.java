package io.jpress.core.template;

import io.jboot.utils.StrUtil;
import io.jpress.core.bsformbuilder.BsFormComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HtmlBlock {

    //模板组件
    public static final String DRAG_TYPE_TEMPLATE = "template";

    //系统内置组件
    public static final String DRAG_TYPE_SYSTEM = "system";

    //布局组件
    public static final String DRAG_TYPE_LAYOUT = "layout";


    // id
    protected String id;

    //类型
    protected String type = DRAG_TYPE_TEMPLATE;

    //标题
    protected String title;

    //背景图片
    protected String icon;

    //排序
    protected String index;

    //模板内容
    protected String template;

    //支持的配置内容
    protected List<HtmlBlockOptionDef> optionDefs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public void addOptionDef(HtmlBlockOptionDef optionDef) {
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
        TemplateUtil.readAndFillHtmlBlock(template,this);
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
        component.setDragIcon(icon);
        component.setDragTitle(getTitle());
        component.setDragIndex(index);
        component.setDragType(type);

        if (optionDefs != null) {
            for (HtmlBlockOptionDef optionDef : optionDefs) {
                component.addProp(optionDef.toBsFormComponentPorp());
            }
        }

        return component;
    }

    public void onPrepareRenderData(Map<String, Object> datas) {

    }
}
