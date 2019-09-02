/**
 * Copyright (c) 2015-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.core.install;


import io.jboot.db.datasource.DataSourceBuilder;
import io.jboot.db.datasource.DataSourceConfig;
import io.jboot.exception.JbootException;
import io.jpress.commons.utils.CommonsUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbUtil {

    private DataSource dataSource;
    private DataSourceConfig dataSourceConfig;

    public DbUtil(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public DbUtil(String dbName, String dbUser, String dbPassword, String dbHost, String dbHostPort) {


        String jdbcUrl = "jdbc:mysql://" + dbHost + ":" + dbHostPort + "/" + dbName + "?"
                + "useSSL=false&"
                + "characterEncoding=utf8&"
                + "zeroDateTimeBehavior=convertToNull";

        this.dataSourceConfig = new DataSourceConfig();
        this.dataSourceConfig.setUrl(jdbcUrl);
        this.dataSourceConfig.setUser(dbUser);
        this.dataSourceConfig.setPassword(dbPassword);
        this.dataSource = new DataSourceBuilder(dataSourceConfig).build();

    }


    public void executeSql(String batchSql) throws SQLException {
        Connection conn = getConnection();
        Statement pst = null;
        try {
            pst = conn.createStatement();
            if (null == batchSql) {
                throw new SQLException("SQL IS NULL");
            }
            if (batchSql.contains(";")) {
                String sqls[] = batchSql.split(";");
                for (String sql : sqls) {
                    if (null != sql && !"".equals(sql.trim()))
                        pst.addBatch(sql);
                }
            } else {
                pst.addBatch(batchSql);
            }
        } finally {
            pst.executeBatch();
            CommonsUtils.quietlyClose(pst, conn);
        }
    }


    public <T> List<T> query(String sql) throws SQLException {
        List result = new ArrayList();
        PreparedStatement pst = null;
        ResultSet rs = null;
        Connection conn = getConnection();
        try {
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            int colAmount = rs.getMetaData().getColumnCount();
            if (colAmount > 1) {
                while (rs.next()) {
                    Object[] temp = new Object[colAmount];
                    for (int i = 0; i < colAmount; i++) {
                        temp[i] = rs.getObject(i + 1);
                    }
                    result.add(temp);
                }
            } else if (colAmount == 1) {
                while (rs.next()) {
                    result.add(rs.getObject(1));
                }
            }
        } finally {
            CommonsUtils.quietlyClose(rs, pst, conn);
        }
        return result;
    }


    private Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new JbootException(e);
        }
    }


    public DataSource getDataSource() {
        return dataSource;
    }

    public DataSourceConfig getDataSourceConfig() {
        return dataSourceConfig;
    }
}
