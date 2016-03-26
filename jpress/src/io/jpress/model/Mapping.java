package io.jpress.model;

import java.sql.SQLException;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;

import io.jpress.core.annotation.Table;
import io.jpress.model.base.BaseMapping;

@Table(tableName="mapping",primaryKey="id")
public class Mapping extends BaseMapping<Mapping> {

	private static final long serialVersionUID = 1L;
	
	public static final Mapping DAO = new Mapping();
	
	
	public int doDelByContentId(long contentId){
		return doDelete("content_id = ?", contentId);
	}
	
	
	public boolean doBatchUpdate(final long contentId,final Long[] taxonomyIds){
		return Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				doDelByContentId(contentId);
				for(long taxonomyid : taxonomyIds){
					Mapping mapping = new Mapping();
					mapping.setContentId(contentId);
					mapping.setTaxonomyId(taxonomyid);
					mapping.save();
				}
				return true;
			}
		});
	}

}
