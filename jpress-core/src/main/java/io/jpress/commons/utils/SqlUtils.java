package io.jpress.commons.utils;

import io.jboot.db.model.Column;
import io.jboot.db.model.Columns;
import io.jboot.utils.ArrayUtils;
import io.jboot.utils.StrUtils;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.utils
 */
public class SqlUtils {

    public static void appendWhereByColumns(Columns columns, StringBuilder sqlBuilder) {
        appendWhereByColumns(columns.getList(), sqlBuilder);
    }

    public static void appendWhereByColumns(List<Column> columns, StringBuilder sqlBuilder) {
        if (ArrayUtils.isNotEmpty(columns)) {
            sqlBuilder.append(" WHERE ");

            int index = 0;
            for (Column column : columns) {
                if (column.isMustNeedValue()) {
                    sqlBuilder.append(String.format(" %s %s ? ", column.getName(), column.getLogic()));
                } else {
                    sqlBuilder.append(String.format(" %s %s ", column.getName(), column.getLogic()));
                }
                if (index != columns.size() - 1) {
                    sqlBuilder.append(" AND ");
                }
                index++;
            }
        }
    }

    public static void likeAppend(Columns columns, String column, Object value) {
        if (StrUtils.isNotBlank(value)) {
            columns.like(column, "%" + value + "%");
        }
    }


}
