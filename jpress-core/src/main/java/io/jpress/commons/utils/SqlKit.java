package io.jpress.commons.utils;

import io.jboot.db.model.Column;
import io.jboot.utils.ArrayUtils;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.commons.utils
 */
public class SqlKit {

    public static void appendWhereIfNotEmpty(List<Column> columns, StringBuilder sqlBuilder) {
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

}
