/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import io.jboot.db.model.Columns;
import io.jboot.db.model.SqlBuilder;
import io.jboot.utils.StrUtil;

import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.commons.utils
 */
public class SqlUtils {

    public static String toWhereSql(Columns columns) {
        if (columns == null || columns.isEmpty()) {
            return "";
        }

        StringBuilder sql = new StringBuilder();
        SqlBuilder.buildWhereSql(sql, columns.getList(), ' ');
        return sql.toString();
    }


    public static String buildInSqlPara(Object... ids) {
        int iMax = ids.length - 1;
        StringBuilder b = new StringBuilder();
        b.append('(');
        for (int i = 0; ; i++) {
            String id = String.valueOf(ids[i]);
            if (!StrUtil.isNumeric(id)) {
                throw new IllegalArgumentException("id must is numeric");
            }
            b.append(id);
            if (i == iMax) {
                return b.append(')').toString();
            }
            b.append(", ");
        }
    }


    private static final String[] stringsToCheck = {"select", "drop", "truncate", "from",
            "exec", "exists", "update", "delete", "insert", "cast", "http", "order", "union",
            "sql", "null", "like", "mysql", "information_schema",
            "sleep", "version", "join", "declare", "having", "signed", "alter",
            "union", "where", "create", "shutdown", "grant", "privileges",
            "`", "--", ";", "#", "*", "%", "(", ")"};

    public static boolean hasSqlInject(String str) {

        if (StrUtil.isBlank(str)) {
            return false;
        }

        str = str.toLowerCase();
        for (String checkString : stringsToCheck) {
            if (str.contains(checkString)) {
                return true;
            }
        }

        return false;
    }


    public static void main(String[] args) {
        Columns columns = Columns.create();
        columns.eq("a.id", 1);
        System.out.println(toWhereSql(columns));

        columns.in("c.id", 1, 2, 4, 5);
        System.out.println(toWhereSql(columns));

        columns.or();
        columns.between("created", new Date(), new Date());
        System.out.println(toWhereSql(columns));
    }


}
