package io.jpress.module.form.model;

import io.jboot.utils.ObjectUtil;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.SqlUtils;

import java.util.Date;
import java.util.Objects;

public class FieldInfo {

    private String tag;
    private String label;

    private String paraName;
    private Boolean showInList = true;
    private Boolean withSearch = false;

    private String fieldName;
    private String fieldType;
    private Integer fieldTypeLen;
    private Boolean required;


    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

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

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public boolean isRequired() {
        return required != null && required;
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
        if (isSupportUpload()) {
            return "text";
        }
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


    public boolean checkFieldStateOk() {
        return StrUtil.isNotBlank(fieldName)
                && !StrUtil.isNumeric(fieldName)
                && !SqlUtils.hasSqlInject(fieldName)
                && !SqlUtils.hasSqlInject(label);
    }


    /**
     * 检查数据长度
     *
     * @param value
     * @return
     */
    public boolean checkValueLen(String value) {
        if (isSupportUpload()) {
            return value.length() < 65535;
        }

        switch (fieldType) {
            case "varchar":
                return fieldTypeLen > value.length();
            case "text":
                // text 长度：65535
                return value.length() < 65535;
            case "int":
            case "boolean":
            case "datetime":
                return true;
            default:
                throw new IllegalStateException("not support type: " + fieldType);
        }
    }

    /**
     * 数据转换
     *
     * @param value
     * @return
     */
    public Object convertValueData(Object value) {
        if (value == null) {
            return null;
        }

        if (isSupportUpload()) {
            return ObjectUtil.convert(value, String.class);
        }

        switch (fieldType) {
            case "varchar":
            case "text":
                return ObjectUtil.convert(value, String.class);
            case "int":
                return ObjectUtil.convert(value, Long.class);
            case "boolean":
                return ObjectUtil.convert(value, boolean.class);
            case "datetime":
                return ObjectUtil.convert(value, Date.class);
            default:
                throw new IllegalStateException("not support type: " + fieldType);
        }
    }



    public boolean isSupportChat() {
        return "checkbox".equals(tag) || "radio".equals(tag) || "select".equals(tag);
    }

    public boolean isSupportSearch() {
        return withSearch != null && withSearch && showInList != null && showInList;
    }

    public boolean isSupportUpload() {
        return "image-upload".equals(tag) || "file-upload".equals(tag);
    }
}
