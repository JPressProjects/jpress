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
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

//import io.jpress.core.db.DbDialectFactory;

@SuppressWarnings("serial")
public class JModel<M extends JModel<M>> extends Model<M> {
	
	private String forPaginateFrom(String tableName, String where) {
		StringBuilder from = new StringBuilder(" FROM ");
		from.append("`" + tableName + "`");

		if (where != null && !"".equals(where.trim())) {
			from.append(" WHERE " + where);
		}
		return from.toString();
	}
	
	private String forSelect(String tableName) {
		return String.format("SELECT * FROM `%s`", tableName);
	}
	
	private String forDelete(String tableName) {
		return String.format("DELETE FROM `%s`", tableName);
	}

	private String forSelectCount(String tableName) {
		return String.format("SELECT COUNT(*) FROM `%s`", tableName);
	}


	public Page<M> doPaginate(int pageNumber, int pageSize) {
		return doPaginate(pageNumber, pageSize, null);
	}

	public Page<M> doPaginate(int pageNumber, int pageSize, String whereSql, Object... params) {
		String from = forPaginateFrom(getTableName(), whereSql);
		return paginate(pageNumber, pageSize, "SELECT *", from, params);
	}
	

	public Page<M> doPaginateByCache(String cacheName, Object key, int pageNumber, int pageSize) {
		return doPaginateByCache(cacheName, key, pageNumber, pageSize, null);
	}

	public Page<M> doPaginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String whereSql,
			Object... params) {
		String from = forPaginateFrom(getTableName(), whereSql);
		return paginateByCache(cacheName, key, pageNumber, pageSize, "SELECT *", from, params);
	}

	private String createSelectSql() {
		return forSelect(getTableName());
	}

	private String createDeleteSql() {
		return forDelete(getTableName());
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
		String sql = forSelectCount(getTableName());
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
		String sql = forSelectCount(getTableName());
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
		String[] primaryKeys = getPrimaryKeys();
		if (null != primaryKeys && primaryKeys.length == 1) {
			return primaryKeys[0];
		}
		throw new RuntimeException(String.format("get PrimaryKey is error in[%s]", getClass()));
	}

	public String[] getPrimaryKeys() {
		Table t = TableMapping.me().getTable(getUsefulClass());
		if (t == null) {
			throw new RuntimeException("can't get table of " + getUsefulClass() + " , maybe jpress install incorrect");
		}
		return t.getPrimaryKey();
	}

	public boolean hasColumn(String columnLabel) {
		return TableMapping.me().getTable(getUsefulClass()).hasColumnLabel(columnLabel);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Class<? extends JModel> getUsefulClass() {
		Class c = getClass();
		return c.getName().indexOf("EnhancerByCGLIB") == -1 ? c : c.getSuperclass();
	}

	private static String tc(String sql) {
		return JModelMapping.me().tx(sql);
	}

	// -----------------------------Override----------------------------
	@Override
	public Page<M> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras) {
		return super.paginate(pageNumber, pageSize, tc(select), tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginate(int pageNumber, int pageSize, boolean isGroupBySql, String select, String sqlExceptSelect,
			Object... paras) {
		return super.paginate(pageNumber, pageSize, isGroupBySql, tc(select), tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect) {
		return super.paginate(pageNumber, pageSize, tc(select), tc(sqlExceptSelect));
	}

	@Override
	public List<M> find(String sql, Object... paras) {
		debugPrintParas(paras);
		return super.find(tc(sql), paras);
	}

	@Override
	public List<M> find(String sql) {
		return super.find(tc(sql));
	}

	@Override
	public M findFirst(String sql, Object... paras) {
		debugPrintParas(paras);
		return super.findFirst(tc(sql), paras);
	}

	@Override
	public M findFirst(String sql) {
		return super.findFirst(tc(sql));
	}

	@Override
	public List<M> findByCache(String cacheName, Object key, String sql, Object... paras) {
		return super.findByCache(cacheName, key, tc(sql), paras);
	}

	@Override
	public List<M> findByCache(String cacheName, Object key, String sql) {
		return super.findByCache(cacheName, key, tc(sql));
	}

	@Override
	public M findFirstByCache(String cacheName, Object key, String sql, Object... paras) {
		return super.findFirstByCache(cacheName, key, tc(sql), paras);
	}

	@Override
	public M findFirstByCache(String cacheName, Object key, String sql) {
		return super.findFirstByCache(cacheName, key, tc(sql));
	}

	@Override
	public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select,
			String sqlExceptSelect, Object... paras) {
		return super.paginateByCache(cacheName, key, pageNumber, pageSize, tc(select), tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, boolean isGroupBySql,
			String select, String sqlExceptSelect, Object... paras) {
		return super.paginateByCache(cacheName, key, pageNumber, pageSize, isGroupBySql, tc(select),
				tc(sqlExceptSelect), paras);
	}

	@Override
	public Page<M> paginateByCache(String cacheName, Object key, int pageNumber, int pageSize, String select,
			String sqlExceptSelect) {
		return super.paginateByCache(cacheName, key, pageNumber, pageSize, tc(select), tc(sqlExceptSelect));
	}

	private void debugPrintParas(Object... objects) {
		if (JFinal.me().getConstants().getDevMode()) {
			System.out.println("\r\n---------------Paras: " + Arrays.toString(objects) + "----------------");
		}
	}
	
	public String metadata(String key) {
		//让子类去实现的
		return null;
	}

}
