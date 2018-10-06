/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

    public static String buildInSqlPara(Object... ids) {
        int iMax = ids.length - 1;
        StringBuilder b = new StringBuilder();
        b.append('(');
        for (int i = 0; ; i++) {
            String id = String.valueOf(ids[i]);
            if (!StrUtils.isNumeric(id)) {
                throw new IllegalArgumentException("id must is numeric");
            }
            b.append(id);
            if (i == iMax)
                return b.append(')').toString();
            b.append(", ");
        }
    }


}
