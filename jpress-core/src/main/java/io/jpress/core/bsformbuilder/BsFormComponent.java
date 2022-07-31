package io.jpress.core.bsformbuilder;

import io.jboot.utils.StrUtil;

import java.util.ArrayList;
import java.util.List;

public class BsFormComponent {

    private String name;
    private String tag;
    private Drag drag = new Drag();
    private List<Prop> props;
    private Boolean disableTools;

    private String template;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.setDragTitle(name);
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Drag getDrag() {
        return drag;
    }

    public void setDragIcon(String dragIcon) {
        this.drag.icon = dragIcon;
    }

    public void setDragTitle(String dragTitle) {
        this.drag.title = dragTitle;
    }

    public void setDragIndex(String dragIndex) {
        if (StrUtil.isNotBlank(dragIndex)) {
            this.drag.index = Integer.valueOf(dragIndex.trim());
        }
    }

    public void setDragType(String dragType) {
        if (StrUtil.isNotBlank(dragType)) {
            this.drag.type = dragType;
        }
    }

    public List<Prop> getProps() {
        return props;
    }


    public void setProps(List<Prop> props) {
        this.props = props;
    }


    public void addProp(Prop prop) {
        if (props == null) {
            props = new ArrayList<>();
        }

        props.add(prop);
    }

    public Boolean getDisableTools() {
        return disableTools;
    }

    public void setDisableTools(Boolean disableTools) {
        this.disableTools = disableTools;
    }

    public String template() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * 左边模块拖动的图标
     */
    public static class Drag {
        //名称
        private String title = "untitle";

        //类型
        private String type = "template";

        //排序
        private Integer index = 0;

        //icon 图标
        private String icon;

        public String getTitle() {
            return title;
        }

        public String getType() {
            return type;
        }

        public Integer getIndex() {
            return index;
        }

        public String getIcon() {
            return icon;
        }
    }


    /**
     * 组件支持的属性配置
     */
    public static class Prop {

        public Prop() {
        }

        public Prop(String name) {
            this.name = name;
        }

        public Prop(String name, String type) {
            this.name = name;
            this.type = type;
        }

        //属性名称
        private String name;

        // 属性类型，支持有 input/select/textarea/radio/checkbox 和 none，
        // none：不会在右边的面板里显示
        private String type = "input";

        //属性在右侧的属性面板里显示的名称
        private String label;

        //默认值
        private String defaultValue;

        //是否禁用（禁止用户输入）
        private String disabled;

        //是否是必须的
        private String required;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getDefaultValue() {
            return defaultValue;
        }

        public void setDefaultValue(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String getDisabled() {
            return disabled;
        }

        public void setDisabled(String disabled) {
            this.disabled = disabled;
        }

        public String getRequired() {
            return required;
        }

        public void setRequired(String required) {
            this.required = required;
        }
    }
}
