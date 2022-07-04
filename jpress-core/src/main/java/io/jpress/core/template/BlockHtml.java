package io.jpress.core.template;

import io.jboot.utils.StrUtil;
import io.jpress.core.bsformbuilder.BsFormComponent;

import java.util.ArrayList;
import java.util.List;

public class BlockHtml {

    //模板组件
    public static final String DRAG_TYPE_TEMPLATE = "template";

    //系统内置组件
    public static final String DRAG_TYPE_SYSTEM = "system";

    //布局组件
    public static final String DRAG_TYPE_LAYOUT = "layout";



    // id
    protected String id;

    //标题
    protected String title;

    //背景图片
    protected String icon;

    //排序
    protected String index;

    //模板内容
    protected String template;

    //支持的配置内容
    protected List<BlockHtmlOptionDef> optionDefs;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
