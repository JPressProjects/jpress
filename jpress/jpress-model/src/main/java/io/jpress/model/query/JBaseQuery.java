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
package io.jpress.model.query;

import java.math.BigInteger;
import java.util.List;

import io.jpress.utils.StringUtils;

public class JBaseQuery {

	protected static boolean appendWhereOrAnd(StringBuilder builder, boolean needWhere) {
		if (needWhere) {
			builder.append(" WHERE ");
		} else {
			builder.append(" AND ");
		}
		return false;
	}

	protected static boolean appendIfNotEmpty(StringBuilder builder, String colName, String value, List<Object> params,
			boolean needWhere) {
		if (value != null) {
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" ").append(colName).append(" = ? ");
			params.add(value);
		}
		return needWhere;
	}

	protected static boolean appendIfNotEmpty(StringBuilder builder, String colName, BigInteger value,
			List<Object> params, boolean needWhere) {
		if (value != null) {
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" ").append(colName).append(" = ? ");
			params.add(value);
		}
		return needWhere;
	}

	protected static boolean appendIfNotEmpty(StringBuilder builder, String colName, Object[] array,
			List<Object> params, boolean needWhere) {
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

	protected static boolean appendIfNotEmptyWithLike(StringBuilder builder, String colName, String value,
			List<Object> params, boolean needWhere) {
		if (StringUtils.isNotBlank(value)) {
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" ").append(colName).append(" like ? ");
			if (value.contains("%")) {
				params.add(value);
			} else {
				params.add("%" + value + "%");
			}

		}
		return needWhere;
	}

	protected static boolean appendIfNotEmptyWithLike(StringBuilder builder, String colName, String[] array,
			List<Object> params, boolean needWhere) {
		if (null != array && array.length > 0) {
			needWhere = appendWhereOrAnd(builder, needWhere);
			builder.append(" (");
			for (int i = 0; i < array.length; i++) {
				if (i == 0) {
					builder.append(" ").append(colName).append(" like ? ");
				} else {
					builder.append(" OR ").append(colName).append(" like ? ");
				}
				String value = array[i];
				if (value.contains("%")) {
					params.add(value);
				} else {
					params.add("%" + value + "%");
				}
			}
			builder.append(" ) ");
		}
		return needWhere;
	}

}
