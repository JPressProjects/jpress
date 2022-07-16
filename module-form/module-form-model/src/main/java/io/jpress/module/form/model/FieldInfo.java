package io.jpress.module.form.model;

import io.jboot.utils.StrUtil;

import java.util.Objects;

public class FieldInfo {

    private String label;

    private String paraName;
    private Boolean showInList = true;
    private Boolean withSearch = false;

    private String fieldName;
    private String fieldType;
    private Integer fieldTypeLen;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getParaName() {
        return paraName;
    }

    public void setParaName(String paraName) {
        this.paraName = paraName;
    }

    public Boolean getShowInList() {
        return showInList;
    }

    public void setShowInList(Boolean showInList) {
        this.showInList = showInList;
    }

    public Boolean getWithSearch() {
        return withSearch;
    }

    public void setWithSearch(Boolean withSearch) {
        this.withSearch = withSearch;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public Integer getFieldTypeLen() {
        return fieldTypeLen;
    }

    public void setFieldTypeLen(Integer fieldTypeLen) {
        this.fieldTypeLen = fieldTypeLen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FieldInfo that = (FieldInfo) o;
        return Objects.equals(fieldName, that.fieldName) && Objects.equals(fieldType, that.fieldType) && Objects.equals(fieldTypeLen, that.fieldTypeLen);
    }


    @Override
    public int hashCode() {
        return Objects.hash(fieldName, fieldType, fieldTypeLen);
    }


    public String toFieldSql() {
        //  `name` varchar(64) DEFAULT NULL COMMENT '站点名称'
        //  `created` datetime DEFAULT NULL
        return "`" + fieldName.trim() + "` " + getTypeAndLen() + " DEFAULT NULL"
                + (StrUtil.isNotBlank(label) ? " COMMENT '" + label.trim() + "'" : "");
    }


    private String getTypeAndLen() {
        switch (fieldType) {
            case "varchar":
                return "varchar(" + fieldTypeLen + ")";
            case "text":
                return "text";
            case "int":
                return "int(" + fieldTypeLen + ")";
            case "boolean":
                return "int(1)";
            case "datetime":
                return "datetime";
            default:
                throw new IllegalStateException("not support type: " + fieldType);
        }
    }


    public boolean isStateOk() {
        return fieldName != null && !StrUtil.isNumeric(fieldName);
    }

    public boolean isSupportSearch() {
        return withSearch != null && withSearch && showInList != null && showInList;
    }
}
