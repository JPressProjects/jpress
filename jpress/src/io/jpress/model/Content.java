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
package io.jpress.model;

import io.jpress.core.Jdb;
import io.jpress.core.annotation.Table;
import io.jpress.model.ModelSorter.ISortModel;
import io.jpress.model.base.BaseContent;
import io.jpress.utils.StringUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

@Table(tableName = "content", primaryKey = "id")
public class Content extends BaseContent<Content> implements ISortModel<Content>{
	
	public static String STATUS_DELETE = "delete";
	public static String STATUS_DRAFT = "draft";
	public static String STATUS_NORMAL = "normal";

	private static final long serialVersionUID = 1L;
	public static final Content DAO = new Content();
	
	private List<Taxonomy> taxonomys;
	
	private int layer = 0;
	private List<Content> childList;
	private Content parent;
	private List<Metadata> metadatas;
	
	public Page<Content> doPaginateByModule(int page, int pagesize, String module) {
		return paginate(page, pagesize, true,"select c.*,GROUP_CONCAT(t.id ,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username",
				" FROM content c"
				+ " left join`mapping`  on c.id = `mapping`.`content_id`"
				+ " left join taxonomy  t on  `mapping`.`taxonomy_id` = t.id"
				+ " left join `user` u on c.user_id = u.id"
				+ " WHERE c.module = ?  "
				+ " GROUP BY c.id"
				+ " ORDER BY c.created DESC", module );
	}

	public Page<Content> doPaginateByModuleAndStatus(int page, int pagesize, String module,String status) {
		return paginate(page, pagesize, true,"select c.*,GROUP_CONCAT(t.id ,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username",
				" FROM content c"
				+ " left join`mapping`  on c.id = `mapping`.`content_id`"
				+ " left join taxonomy  t on  `mapping`.`taxonomy_id` = t.id"
				+ " left join `user` u on c.user_id = u.id"
				+ " WHERE c.module = ? AND c.status = ? "
				+ " GROUP BY c.id"
				+ " ORDER BY c.created DESC", module , status);
	}
	
	
	public Page<Content> doPaginateInNormalByModule(int page, int pagesize, String module) {
		return paginate(page, pagesize, true,"select c.*,GROUP_CONCAT(t.id ,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username",
				" FROM content c"
				+ " left join`mapping`  on c.id = `mapping`.`content_id`"
				+ " left join taxonomy  t on  `mapping`.`taxonomy_id` = t.id"
				+ " left join `user` u on c.user_id = u.id"
				+ " WHERE c.module = ? AND c.status <> ? "
				+ " GROUP BY c.id"
				+ " ORDER BY c.created DESC", module , STATUS_DELETE);
	}

	public Page<Content> doPaginateByMetadata(int page, int pagesize, String meta_key,String meta_value) {
		return paginate(page, pagesize, true,"select * ",
				"FROM (select c.*,GROUP_CONCAT(t.id ,':',t.title,':',t.type SEPARATOR ',') as taxonomys,"
				+ "GROUP_CONCAT(m.id ,':',m.meta_key,':',m.meta_value SEPARATOR ',') metadatas , u.username"
				+ " FROM content c"
				+ " left join`mapping`  on c.id = `mapping`.`content_id`"
				+ " left join taxonomy  t on  `mapping`.`taxonomy_id` = t.id"
				+ " left join `user` u on c.user_id = u.id"
				+ " left join `metadata` m on c.id = m.`object_id` and m.`object_type`='content'"
				+ " GROUP BY c.id"
				+ " ORDER BY c.created DESC) c where c.`metadatas` like ?","%:"+meta_key+":"+meta_value);
	}

	/**
	 * @param page
	 * @param pagesize
	 * @param orderBy
	 * @param keyword
	 * @param typeIds
	 * @param typeSlugs
	 * @param modules
	 * @param styles
	 * @param slugs
	 * @param userIds
	 * @param parentIds
	 * @param tags
	 * @return
	 */
	public List<Content> findList(int page, int pagesize, String orderBy,
			String keyword, Long[] typeIds, String[] typeSlugs,String[] modules, String[] styles,String[] slugs,
			Integer[] userIds, Integer[] parentIds,String[] tags) {
		

		StringBuffer whereSqlPart= new StringBuffer(" WHERE c.id = m.content_id ");
		String selectSqlPart = "SELECT m.taxonomy_id taxonomyId,c.* ";
		
		String fromSqlPart = " FROM mapping m,content c ";
		
		if((null != tags && tags.length > 0) || (null != typeSlugs && typeSlugs.length > 0)){
			
			selectSqlPart = " SELECT m.taxonomy_id taxonomyId,c.*,t.* ";
			fromSqlPart = " FROM mapping m,content c,taxonomy t ";
			
			whereSqlPart.append(" AND t.id=m.`taxonomy_id` ");
		}
		

		LinkedList<Object> params = new LinkedList<Object>();

		andSqlTagbuild(typeIds, whereSqlPart, params, "m.taxonomy_id");
		
		andSqlTagbuild(modules, whereSqlPart, params, "c.module");
		andSqlTagbuild(styles, whereSqlPart, params, "c.style");
		andSqlTagbuild(slugs, whereSqlPart, params, "c.slug");
		andSqlTagbuild(userIds, whereSqlPart, params, "c.user_id");
		andSqlTagbuild(parentIds, whereSqlPart, params, "c.parent_id");
		
		andSqlTagbuild(typeSlugs, whereSqlPart, params, "t.slug");
		
		if (null != tags && tags.length > 0) {
			andSqlTagbuild(tags, whereSqlPart, params, "t.name");
			whereSqlPart.append(" AND t.taxonomy_module='tag' ");
		}

		if (null != keyword && !"".equals(keyword.trim())) {
			whereSqlPart.append(" AND c.title like '%?%'");
			params.add(keyword);
		}
		
		
		whereSqlPart.append("GROUP BY m.content_id ");

		if (null != orderBy && !"".equals(orderBy)) {
			whereSqlPart.append(" ORDER BY ").append(orderBy);
		} else {
			whereSqlPart.append(" ORDER BY ").append("c.created");
		}
		
		return find(selectSqlPart+ fromSqlPart + whereSqlPart.toString(),params.toArray());
	}
	
	/**
	 * @param array
	 * @param sqlBuffer
	 * @param params
	 * @param colName
	 */
	private void andSqlTagbuild(Object[] array, StringBuffer sqlBuffer,
			LinkedList<Object> params, String colName) {
		if (null != array && array.length > 0) {
			sqlBuffer.append(" AND (");
			for (int i = 0; i < array.length; i++) {
				if (i == 0) {
					sqlBuffer.append(String.format(" %s = ? ", colName));
				} else {
					sqlBuffer.append(String.format(" OR %s = ? ", colName));
				}
				params.add(array[i]);
			}
			sqlBuffer.append(" ) ");
		}
	}
	
	
	public List<Content> findMenuList(){
		return doFind("module = ? ","menu");
	}

	
	public Content findBySlug(String slug) {
		return doFindFirst("slug = ?",slug);
	}
	
	
	public long findCountByModule(String module){
		return doFindCount("module = ?" ,module);
	}
	
	public long findCountByModuleAndStatus(String module ,String status){
		return doFindCount("module = ? AND status = ?", module,status);
	}
	
	
	public long findCountInNormalByModule(String module){
		return doFindCount("module = ? AND status <> ?", module, STATUS_DELETE);
	}
	
	
	
	public User findUser(){
		return User.findUserById(getUserId());
	}
	
	public int batchTrash(Long ... ids){
		if(ids != null && ids.length > 0){
			Object[] paras = new Object[ids.length + 1];
			StringBuilder sb = new StringBuilder("UPDATE content SET status=? WHERE 1<>1 ");
			paras[0] = STATUS_DELETE;
			for (int i = 0; i < ids.length; i++) {
				paras[i+1] = ids[i];
				sb.append(" OR id = ? ");
			}
			return Jdb.update(sb.toString(),paras);
		}
		return 0;
	}
	
	
//	public List<Taxonomy> findCategoryList(){
//		return findTaxonomyList(Taxonomy.TYPE_CATEGORY);
//	}
//	
//	public String findCategoryListAsString(){
//		return findTaxonomyListAsString(Taxonomy.TYPE_CATEGORY);
//	}
//	
//	public List<Taxonomy> findTagList(){
//		return findTaxonomyList(Taxonomy.TYPE_TAG);
//	}
//	
//	public String findTagListAsString(){
//		return findTaxonomyListAsString(Taxonomy.TYPE_TAG);
//	}
	
	public List<Taxonomy> findTaxonomyList(){
		return Taxonomy.DAO.findListByContentId(getId());
	}
	
	public List<Taxonomy> findTaxonomyList(String type){
		return Taxonomy.DAO.findListByTypeAndContentId(type, getId());
	}
	
	public String findTaxonomyListAsString(String type){
		StringBuilder sb = new StringBuilder();
		List<Taxonomy> list = findTaxonomyList(type);
		if(list != null && list.size() > 0){
			for(Taxonomy taxonomy : list){
				sb.append(taxonomy.getTitle()).append(",");
			}
		}
		if(sb.length() > 0){
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
	
	public String getUsername(){
		return get("username");
	}
	
	public List<Metadata> getMetadatas() {
		if(metadatas == null){
			String metadataString = get("metadatas");
			if(StringUtils.isNotBlank(metadataString)){
				metadatas = new ArrayList<Metadata>();
				String medadataStrings[] = metadataString.split(",");
				if(medadataStrings!=null && medadataStrings.length > 0){
					for(String metadataStr : medadataStrings){
						String [] propertes = metadataStr.split(":");
						//by method doPaginateByMetadata
						//propertes[0] == id
						//propertes[1] == meta_key
						//propertes[2] == meta_value
						Metadata md = new Metadata();
						md.setId(Long.parseLong(propertes[0]));
						md.setObjectType(METADATA_TYPE);
						md.setMetaKey(propertes[1]);
						md.setMetaValue(propertes[2]);
						metadatas.add(md);
					}
				}
			}
		}
		return metadatas;
	}

	public void setMetadatas(List<Metadata> metadatas) {
		this.metadatas = metadatas;
	}

	public String getTagsAsString(){
		return getTaxonomyAsString(Taxonomy.TYPE_TAG);
	}
	
	public String getCategorysAsString(){
		return getTaxonomyAsString(Taxonomy.TYPE_CATEGORY);
	}
	
	public boolean isDelete(){
		return STATUS_DELETE.equals(getStatus());
	}
	
	public String getTaxonomyAsString(String type){
		StringBuilder retBuilder = new StringBuilder();
		String taxonomyString = get("taxonomys");
		if(taxonomyString != null ){
			String[]  taxonomyStrings = taxonomyString.split(",");
			for(String taxonomyStr : taxonomyStrings){
				String [] propertes = taxonomyStr.split(":");
				//propertes[0] == id
				//propertes[1] == title
				//propertes[2] == type
				//by method doPaginateByModuleAndStatus
				if(propertes != null && propertes.length >= 3){
					if(type.equals(propertes[2])){
						retBuilder.append(propertes[1]).append(",");
					}
				}
			}
		}
		
		return retBuilder.toString();
	}
	
	
	public List<Taxonomy> getTaxonomys(){
		if(taxonomys == null){
			taxonomys = new ArrayList<Taxonomy>();
		}
		
		String taxonomyString = get("taxonomys");
		if(taxonomys != null ){
			String[]  taxonomyStrings = taxonomyString.split(",");
			
			for(String taxonomyStr : taxonomyStrings){
				String [] propertes = taxonomyStr.split(":");
				//by method doPaginateByModuleAndStatus
				Taxonomy taxonomy = new Taxonomy();
				taxonomy.setId(Long.parseLong(propertes[0]));
				taxonomy.setTitle(propertes[1]);
				taxonomy.setType(propertes[2]);
				taxonomys.add(taxonomy);
			}
		}
		return taxonomys;
	}

	
	
	@Override
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public int getLayer(){
		return layer;
	}
	
	public String getLayerString(){
		String layerString = "";
		for (int i = 0; i < layer; i++) {
			layerString += "— ";
		}
		return layerString;
	}

	@Override
	public void setParent(Content parent) {
		this.parent = parent;
	}
	
	public Content getParent() {
		return parent;
	}

	@Override
	public void addChild(Content child) {
		if(this.childList == null){
			this.childList =  new ArrayList<Content>(); 
		}
		childList.add(child);
	}

}
