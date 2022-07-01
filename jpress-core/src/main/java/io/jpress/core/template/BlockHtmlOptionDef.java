package io.jpress.core.template;

import io.jpress.core.template.bsformbuilder.BsFormComponent;

import java.util.Objects;

/**
 * block_***.html 里定义的 #blockOption 指令或者方法
 */
public class BlockHtmlOptionDef {

    private String name;
    private String type = "input";
    private String defaultValue;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null) {
            throw new NullPointerException("key is null!");
        }

        int indexOf = name.indexOf(":");
        if (indexOf > 0) {
            this.name = name.substring(0, indexOf).trim();
            this.type = name.substring(indexOf + 1).trim();
        } else {
            this.name = name.trim();
        }
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BlockHtmlOptionDef that = (BlockHtmlOptionDef) o;
        return Objects.equals(name, that.name);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    public BsFormComponent.Prop toBsFormComponentPorp() {
        BsFormComponent.Prop prop = new BsFormComponent.Prop();
        prop.setName(name);
        prop.setLabel(name);
        prop.setType(type);
        prop.setDefaultValue(defaultValue);
        return prop;
    }
}
