package io.jpress.core.template;

import io.jboot.utils.StrUtil;
import io.jpress.core.bsformbuilder.BsFormComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * html 块，或者说是一个 html 片段
 * 一个 HtmlBlock 可以转换为 bsFormComponent
 */
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

    //组件名称
    protected String name;

    //icon
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

    public String getName() {
        return StrUtil.isNotBlank(name) ? name : id;
    }

    public void setName(String name) {
        this.name = name;
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

        HtmlBlockOptionDef existOptionDef = null;
        for (HtmlBlockOptionDef def : optionDefs) {
            if (Objects.equals(def.getName(), optionDef.getName())) {
                existOptionDef = def;
            }
        }

        if (existOptionDef == null) {
            optionDefs.add(optionDef);
        } else {
            mergeOptionDef(optionDef, existOptionDef);
        }
    }

    /**
     * 合并新的 option 到已经存在的 options
     *
     * @param newOptionDef
     * @param existOptionDef
     */
    private void mergeOptionDef(HtmlBlockOptionDef newOptionDef, HtmlBlockOptionDef existOptionDef) {
        if (StrUtil.isNotBlank(newOptionDef.getType())) {
            existOptionDef.setType(newOptionDef.getType());
        }

        if (StrUtil.isNotBlank(newOptionDef.getDefaultValue())) {
            existOptionDef.setDefaultValue(newOptionDef.getDefaultValue());
        }
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
        TemplateUtil.readAndFillHtmlBlock(template, this);
    }

    public void addTemplateLine(String line) {
        if (template == null) {
            template = "";
        }
        template += line;
    }


    public BsFormComponent toBsFormComponent() {

        BsFormComponent component = new BsFormComponent();
        component.setName(getName());
        component.setTag(id);
        component.setDragIcon(icon);
        component.setDragTitle(getName());
        component.setDragIndex(index);
        component.setDragType(type);
        component.setTemplate(getTemplate());

        if (optionDefs != null) {
            for (HtmlBlockOptionDef optionDef : optionDefs) {
                component.addProp(optionDef.toBsFormComponentPorp());
            }
        }

        return component;
    }

}
