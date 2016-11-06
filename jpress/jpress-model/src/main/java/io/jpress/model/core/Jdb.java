/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.model.core;

import java.util.Arrays;
import java.util.List;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;

public class Jdb {
	
	static String tx(String sql){
		return JModelMapping.me().tx(sql);
	}
	
	private static void debugPrintParas(Object ...objects){
		if(JFinal.me().getConstants().getDevMode()){
			System.out.println("\r\n---------------Paras: "+Arrays.toString(objects)+"----------------");
		}
	}
	
	public static <T> List<T> query(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.query(tx(sql), paras);
	}
	
	public static <T> List<T> query(String sql) {
		return Db.query(tx(sql));
	}
	
	public static <T> T queryFirst(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryFirst(tx(sql), paras);
	}
	
	public static <T> T queryFirst(String sql) {
		return Db.queryFirst(tx(sql));
	}
	
	public static <T> T queryColumn(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryColumn(tx(sql), paras);
	}
	
	public static <T> T queryColumn(String sql) {
		return Db.queryColumn(tx(sql));
	}
	
	public static String queryStr(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryStr(tx(sql), paras);
	}
	
	public static String queryStr(String sql) {
		return Db.queryStr(tx(sql));
	}
	
	public static Integer queryInt(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryInt(tx(sql), paras);
	}
	
	public static Integer queryInt(String sql) {
		return Db.queryInt(tx(sql));
	}
	
	public static Long queryLong(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryLong(tx(sql), paras);
	}
	
	public static Long queryLong(String sql) {
		return Db.queryLong(tx(sql));
	}
	
	public static Double queryDouble(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryDouble(tx(sql), paras);
	}
	
	public static Double queryDouble(String sql) {
		return Db.queryDouble(tx(sql));
	}
	
	public static Float queryFloat(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryFloat(tx(sql), paras);
	}
	
	public static Float queryFloat(String sql) {
		return Db.queryFloat(tx(sql));
	}
	
	public static java.math.BigDecimal queryBigDecimal(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryBigDecimal(tx(sql), paras);
	}
	
	public static java.math.BigDecimal queryBigDecimal(String sql) {
		return Db.queryBigDecimal(tx(sql));
	}
	
	public static byte[] queryBytes(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryBytes(tx(sql), paras);
	}
	
	public static byte[] queryBytes(String sql) {
		return Db.queryBytes(tx(sql));
	}
	
	public static java.util.Date queryDate(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryDate(tx(sql), paras);
	}
	
	public static java.util.Date queryDate(String sql) {
		return Db.queryDate(tx(sql));
	}
	
	public static java.sql.Time queryTime(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryTime(tx(sql), paras);
	}
	
	public static java.sql.Time queryTime(String sql) {
		return Db.queryTime(tx(sql));
	}
	public static java.sql.Timestamp queryTimestamp(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryTimestamp(tx(sql), paras);
	}
	
	public static java.sql.Timestamp queryTimestamp(String sql) {
		return Db.queryTimestamp(tx(sql));
	}
	
	public static Boolean queryBoolean(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryBoolean(tx(sql), paras);
	}
	
	public static Boolean queryBoolean(String sql) {
		return Db.queryBoolean(tx(sql));
	}
	
	public static Number queryNumber(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.queryNumber(tx(sql), paras);
	}
	
	public static Number queryNumber(String sql) {
		return Db.queryNumber(tx(sql));
	}
	// 26 queryXxx method under -----------------------------------------------
	
	
	public static int update(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.update(tx(sql), paras);
	}
	
	public static int update(String sql) {
		return Db.update(tx(sql));
	}
	
	
	public static List<Record> find(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.find(tx(sql), paras);
	}
	
	public static List<Record> find(String sql) {
		return Db.find(tx(sql));
	}
	
	public static Record findFirst(String sql, Object... paras) {
		debugPrintParas(paras);
		return Db.findFirst(tx(sql), paras);
	}
	
	public static Record findFirst(String sql) {
		return Db.findFirst(tx(sql));
	}
	
	public static Page<Record> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		debugPrintParas(paras);
		return Db.paginate(pageNumber, pageSize, tx(select), tx(sqlExceptSelect), paras);
	}
	
	public static Page<Record> paginate(int pageNumber, int pageSize, boolean isGroupBySql, String select, String sqlExceptSelect, Object... paras) {
		debugPrintParas(paras);
		return Db.paginate(pageNumber, pageSize, isGroupBySql, tx(select), tx(sqlExceptSelect), paras);
	}

	public static Page<Record> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		return Db.paginate(pageNumber, pageSize, tx(select), tx(sqlExceptSelect));
	}
	

}
