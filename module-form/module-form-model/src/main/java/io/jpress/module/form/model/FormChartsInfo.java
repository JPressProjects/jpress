package io.jpress.module.form.model;

public class FormChartsInfo {


    private String name;
    private int value;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public FormChartsInfo(String name, int value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "FormChartsInfo{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public FormChartsInfo() {

    }

}
