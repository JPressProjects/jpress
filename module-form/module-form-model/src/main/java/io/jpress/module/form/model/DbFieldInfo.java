package io.jpress.module.form.model;

import io.jboot.utils.StrUtil;

import java.util.Objects;

public class DbFieldInfo {

    private String name;
    private String type;
    private Integer typeLen;
    private String comment;

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

    public Integer getTypeLen() {
        return typeLen;
    }

    public void setTypeLen(Integer typeLen) {
        this.typeLen = typeLen;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DbFieldInfo that = (DbFieldInfo) o;
        return Objects.equals(name, that.name) && Objects.equals(type, that.type) && Objects.equals(typeLen, that.typeLen);
    }


    @Override
    public int hashCode() {
        return Objects.hash(name, type, typeLen);
    }


    public String toSql() {
        //  `name` varchar(64) DEFAULT NULL COMMENT '站点名称'
        //  `created` datetime DEFAULT NULL
        return "`" + name.trim() + "` " + type + (typeLen == null ? "" : "(" + typeLen + ") ") + "DEFAULT NULL"
                + (StrUtil.isNotBlank(comment) ? " COMMENT '" + comment.trim() + "'" : "");
    }
}
