package io.jpress.model;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseMetadata;

import java.util.List;

@Table(tableName = "metadata", primaryKey = "id")
public class Metadata extends BaseMetadata<Metadata> {

	private static final long serialVersionUID = 1L;

	public static final Metadata DAO = new Metadata();

	@Override
	public boolean save() {

		removeCache(getObjectType() + getObjectId());

		return super.save();
	}

	@Override
	public boolean update() {

		removeCache(getObjectType() + getObjectId());

		return super.update();
	}

	public static List<Metadata> findListByTypeAndId(String type, long id) {
		return DAO.doFind("object_type = ? and object_id = ?", type, id);
	}

	public static Metadata findFirstByTypeAndValue(String type,
			String key, Object value) {

		return DAO.doFindFirst(
				"object_type = ? and meta_key = ? and meta_value = ?", type,
				key, value);

	}
	
	public static List<Metadata> findListByTypeAndValue(String type,
			String key, Object value) {

		return DAO.doFind(
				"object_type = ? and meta_key = ? and meta_value = ?", type,
				key, value);

	}

	public static Metadata findByTypeAndIdAndKey(String type, long id,
			String key) {

		return DAO.doFindFirstByCache(CACHE_NAME, key + id,
				"object_type = ? and object_id = ? and meta_key = ? ", type,
				id, key);

	}

}
