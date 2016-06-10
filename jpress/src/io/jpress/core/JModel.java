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
package io.jpress.core;

import io.jpress.core.db.DbDialectFactory;
import io.jpress.utils.StringUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

@SuppressWarnings("serial")
public class JModel<M extends JModel<M>> extends Model<M> {

	public Page<M> doPaginate(int pageNumber, int pageSize) {
		return doPaginate(pageNumber, pageSize, null);
	}

	public Page<M> doPaginate(int pageNumber, int pageSize, String whereSql, Object... params) {
		String from = DbDialectFactory.getDbDialect().forPaginateFrom(getTableName(), whereSql);
		return paginate(pageNumber, pageSize, "SELECT *", from, params);
	}

	public Page<M> doPaginateByCache(String cacheName, Object key, int pageNumber, int pageSize) {
		return doPaginateByCache(cacheName, key, pageNumber, pageSize, null);
	}

	public Page<M> doPaginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String whereSql,
			Object... params) {
		String from = DbDialectFactory.getDbDialect().forPaginateFrom(getTableName(), whereSql);
		return paginateByCache(cacheName, key, pageNumber, pageSize, "SELECT *", from, params);
	}

	private String createSelectSql() {
		return DbDialectFactory.getDbDialect().forSelect(getTableName());
	}
	

	private String createDeleteSql() {
		return DbDialectFactory.getDbDialect().forDelete(getTableName());
	}

	private String createSelectWhereSql() {
		return createSelectSql() + " WHERE ";
	}

	public List<M> doFind() {
		return find(createSelectSql());
	}

	public List<M> doFind(String where) {
		return find(createSelectWhereSql() + where);
	}

	public List<M> doFind(String where, Object... params) {
		return find(createSelectWhereSql() + where, params);
	}

	public List<M> doFindByCache(String cacheName, Object key) {
		return findByCache(cacheName, key, createSelectSql());
	}

	public List<M> doFindByCache(String cacheName, Object key, String where) {
		return findByCache(cacheName, key, createSelectWhereSql() + where);
	}

	public List<M> doFindByCache(String cacheName, Object key, String where, Object... params) {
		return findByCache(cacheName, key, createSelectWhereSql() + where, params);
	}

	public M doFindFirst(String where) {
		return findFirst(createSelectWhereSql() + where);
	}

	public M doFindFirst(String where, Object... params) {
		return findFirst(createSelectWhereSql() + where, params);
	}

	public M doFindFirstByCache(String cacheName, Object key, String where) {
		return findFirstByCache(cacheName, key, createSelectWhereSql() + where);
	}

	public M doFindFirstByCache(String cacheName, Object key, String where, Object... params) {
		return findFirstByCache(cacheName, key, createSelectWhereSql() + where, params);
	}

	public Long doFindCount() {
		return doFindCount(null);
	}

	public Long doFindCount(String whereSQL, Object... params) {
		String sql = DbDialectFactory.getDbDialect().forSelectCount(getTableName());
		final StringBuilder sqlBuilder = new StringBuilder(sql);
		if (null != whereSQL && !"".equals(whereSQL.trim())) {
			sqlBuilder.append(" WHERE ").append(whereSQL);
		}
		return Db.queryLong(tc(sqlBuilder.toString()), params);
	}

	public long doFindCountByCache(String cacheName, Object key) {
		return doFindCountByCache(cacheName, key, null);
	}

	public long doFindCountByCache(String cacheName, Object key, String whereSQL, final Object... params) {
		String sql = DbDialectFactory.getDbDialect().forSelectCount(getTableName());
		final StringBuilder sqlBuilder = new StringBuilder(sql);
		if (null != whereSQL && !"".equals(whereSQL.trim())) {
			sqlBuilder.append(" WHERE ").append(whereSQL);
		}
		return CacheKit.get(cacheName, key, new IDataLoader() {
			@Override
			public Object load() {
				return Db.queryLong(tc(sqlBuilder.toString()), params);
			}
		});
	}

	public int doDelete(String where, Object... objs) {
		String sql = createDeleteSql() + " WHERE " + where;
		return Db.update(sql, objs);
	}

	public boolean saveOrUpdate() {
		if (null == get(getPrimaryKey())) {
			return this.save();
		}
		return this.update();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;

		if (!(o instanceof JModel<?>))
			return false;

		if (((JModel<?>) o).get("id") == null)
			return false;

		return ((JModel<?>) o).get("id").equals(get("id"));
	}

	public String getTableName() {
		return TableMapping.me().getTable(getUsefulClass()).getName();
	}

	public String getPrimaryKey() {
		String[] primaryKeys = TableMapping.me().getTable(getUsefulClass()).getPrimaryKey();
		if (null != primaryKeys && primaryKeys.length == 1) {
			return primaryKeys[0];
		}
		throw new RuntimeException(String.format("get PrimaryKey is error in[%s]", getClass()));
	}

	public String[] getPrimaryKeys() {
		return TableMapping.me().getTable(getUsefulClass()).getPrimaryKey();
	}

	public boolean hasColumn(String columnLabel) {
		return TableMapping.me().getTable(getUsefulClass()).hasColumnLabel(columnLabel);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Class<? extends JModel> getUsefulClass() {
		Class c = getClass();
		return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
		// com.demo.blog.Blog$$EnhancerByCGLIB$$69a17158
	}

	/**
	 * if user set table prefix,this method auto add the prefix in sql.
	 * 
	 * @param sql
	 * @return new sql with table prefix
	 */
	private static String tc(String sql) {
		return DbDialectFactory.getDbDialect().doTableConvert(sql);
	}

	// -----------------------------Override----------------------------
	@Override
	public Page<M> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.paginate(pageNumber, pageSize, tc(select), tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginate(int pageNumber, int pageSize, boolean isGroupBySql, String select, String sqlExceptSelect,
			Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.paginate(pageNumber, pageSize, isGroupBySql, tc(select), tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		// TODO Auto-generated method stub
		return super.paginate(pageNumber, pageSize, tc(select), tc(sqlExceptSelect));
	}

	@Override
	public List<M> find(String sql, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.find(tc(sql), paras);
	}

	@Override
	public List<M> find(String sql) {
		// TODO Auto-generated method stub
		return super.find(tc(sql));
	}

	@Override
	public M findFirst(String sql, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.findFirst(tc(sql), paras);
	}

	@Override
	public M findFirst(String sql) {
		// TODO Auto-generated method stub
		return super.findFirst(tc(sql));
	}

	@Override
	public List<M> findByCache(String cacheName, Object key, String sql, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.findByCache(cacheName, key, tc(sql), paras);
	}

	@Override
	public List<M> findByCache(String cacheName, Object key, String sql) {
		// TODO Auto-generated method stub
		return super.findByCache(cacheName, key, tc(sql));
	}

	@Override
	public M findFirstByCache(String cacheName, Object key, String sql, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.findFirstByCache(cacheName, key, tc(sql), paras);
	}

	@Override
	public M findFirstByCache(String cacheName, Object key, String sql) {
		// TODO Auto-generated method stub
		return super.findFirstByCache(cacheName, key, tc(sql));
	}

	@Override
	public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select,
			String sqlExceptSelect, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.paginateByCache(cacheName, key, pageNumber, pageSize, tc(select), tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, boolean isGroupBySql,
			String select, String sqlExceptSelect, Object... paras) {
		// TODO Auto-generated method stub
		debugPrintParas(paras);
		return super.paginateByCache(cacheName, key, pageNumber, pageSize, isGroupBySql, tc(select), tc(sqlExceptSelect),
				paras);
	}

	@Override
	public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		// TODO Auto-generated method stub
		return super.paginateByCache(cacheName, key, pageNumber, pageSize, tc(select), tc(sqlExceptSelect));
	}

	private void debugPrintParas(Object... objects) {
		if (JFinal.me().getConstants().getDevMode()) {
			System.out.println("\r\n---------------Paras: " + Arrays.toString(objects) + "----------------");
		}
	}

	protected static boolean appendWhereOrAnd(StringBuilder builder, boolean needWhere) {
		if (needWhere) {
			builder.append(" WHERE ");
		} else {
			builder.append(" AND ");
		}
		return false;
	}
	
	protected static boolean appendIfNotEmpty(StringBuilder builder, String colName, String value, List<Object> params, boolean needWhere) {
		if(StringUtils.isNotBlank(value)){
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" ").append(colName).append(" = ? ");
			params.add(value);
		}
		return needWhere;
	}
	
	protected static boolean appendIfNotEmpty(StringBuilder builder, String colName, BigInteger value, List<Object> params, boolean needWhere) {
		if(value!=null && value.compareTo(BigInteger.ZERO) > 0){
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" ").append(colName).append(" = ? ");
			params.add(value);
		}
		return needWhere;
	}
	
	protected static boolean appendIfNotEmpty(StringBuilder builder, String colName, Object[] array, List<Object> params, boolean needWhere) {
		if (null != array && array.length > 0) {
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" (");
			for (int i = 0; i < array.length; i++) {
				if (i == 0) {
					builder.append(" ").append(colName).append(" = ? ");
				} else {
					builder.append(" OR ").append(colName).append(" = ? ");
				}
				params.add(array[i]);
			}
			builder.append(" ) ");
		}
		return needWhere;
	}
	
	protected static boolean appendIfNotEmptyWithLike(StringBuilder builder, String colName, Object[] array, List<Object> params, boolean needWhere) {
		if (null != array && array.length > 0) {
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" (");
			for (int i = 0; i < array.length; i++) {
				if (i == 0) {
					builder.append(" ").append(colName).append(" like ? ");
				} else {
					builder.append(" OR ").append(colName).append(" like ? ");
				}
				params.add("%"+array[i]+"%");
			}
			builder.append(" ) ");
		}
		return needWhere;
	}

}
