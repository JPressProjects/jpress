package io.jpress.module.form.model;

public class EChartsItem {


    private String name;
    private int value;

    public EChartsItem() {
    }

    public EChartsItem(String name, int value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "EChartsItem{" +
                "name='" + name + '\'' +
                ", value=" + value +
                '}';
    }
}
