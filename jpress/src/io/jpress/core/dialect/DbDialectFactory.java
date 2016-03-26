package io.jpress.core.dialect;

public class DbDialectFactory {
	static DbDialect dialect;

	public static DbDialect getDbDialect() {

		if (dialect == null) {
			// 暂时只支持 Mysql
			dialect = new MysqlDialect();
		}

		return dialect;
	}

}
