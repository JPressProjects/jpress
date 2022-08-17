package io.jpress.module.form.model;

public class EChartsItem {


    private String name;
    private Long value;

    public EChartsItem() {
    }

    public EChartsItem(String name, Long value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getValue() {
        return value != null ? value : 0L;
    }

    public void setValue(Long value) {
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
