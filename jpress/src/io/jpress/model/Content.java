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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.jfinal.core.JFinal;
import com.jfinal.plugin.activerecord.Page;

@Table(tableName = "content", primaryKey = "id")
public class Content extends BaseContent<Content> implements ISortModel<Content> {

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
	


	public Page<Content> doPaginateByMetadata(int page, int pagesize, String meta_key, String meta_value) {
		return paginate(page, pagesize, true, "select * ",
				"FROM (select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,"
						+ "GROUP_CONCAT(m.id ,':',m.meta_key,':',m.meta_value SEPARATOR ',') metadatas , u.username"
						+ " FROM content c" 
						+ " left join mapping m on c.id = m.`content_id`"
						+ " left join taxonomy  t on m.`taxonomy_id` = t.id"
						+ " left join user u on c.user_id = u.id"
						+ " left join metadata md on c.id = md.`object_id` and md.`object_type`='content'"
						+ " GROUP BY c.id" 
						+ " ORDER BY c.created DESC) c "
						+ "where c.`metadatas` like ?",
				"%:" + meta_key + ":" + meta_value);
	}
	
	
	public Page<Content> doPaginateByModule(int page, int pagesize, String module) {
		return doPaginate(pagesize, pagesize, module, null, 0, 0, null);
	}

	public Page<Content> doPaginateByModuleAndStatus(int page, int pagesize, String module, String status) {
		return doPaginate(pagesize, pagesize, module, status, 0, 0, null);
	}

	
	public Page<Content> doPaginateByModuleInNormal(int page, int pagesize, String module) {
		return doPaginate(pagesize, pagesize, module, STATUS_NORMAL, 0, 0, null);
	}
	
	public Page<Content> doPaginate(int page, int pagesize, String module, String status, long taxonomyId,long userId,String orderBy) {
		
		String select = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username";
		
		StringBuilder fromBuilder = new StringBuilder(" from content c");
		fromBuilder.append(" left join mapping m on c.id = m.`content_id`");
		fromBuilder.append(" left join taxonomy  t on  m.`taxonomy_id` = t.id");
		fromBuilder.append(" left join user u on c.user_id = u.id");
		fromBuilder.append(" group by c.id");
		
		LinkedList<Object> params = new LinkedList<Object>();
		
		boolean hasWhere = false;
		hasWhere = appendIfNotNull(fromBuilder, "t.module", module, params, hasWhere);
		hasWhere = appendIfNotNull(fromBuilder, "c.status", status, params, hasWhere);
		hasWhere = appendIfNotNull(fromBuilder, "t.id", taxonomyId, params, hasWhere);
		hasWhere = appendIfNotNull(fromBuilder, "u.id", userId, params, hasWhere);
		
		if (null != orderBy && !"".equals(orderBy)) {
			fromBuilder.append(" ORDER BY ?");
			params.add(orderBy);
		} else {
			fromBuilder.append(" ORDER BY c.created");
		}
		
		return paginate(page, pagesize, true,select, fromBuilder.toString() ,params.toArray());
	}


	
	public List<Content> findListInNormal(int page, int pagesize, long taxonomyId,String orderBy){
		return findListInNormal(page, pagesize, orderBy, null, new Long[]{taxonomyId}, null, null, null, null, null, null, null);
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
	public List<Content> findListInNormal(int page, int pagesize, String orderBy, String keyword, Long[] typeIds,
			String[] typeSlugs, String[] modules, String[] styles, String[] slugs, Integer[] userIds,
			Integer[] parentIds, String[] tags) {

		String sql = "select c.*,GROUP_CONCAT(t.id ,':',t.slug,':',t.title,':',t.type SEPARATOR ',') as taxonomys,u.username"
				+ " from content c" 
				+ " left join mapping m on c.id = m.`content_id`"
				+ " left join taxonomy  t on  m.`taxonomy_id` = t.id"
				+ " left join user u on c.user_id = u.id"
				+ " where c.status = 'normal' ";

		StringBuilder sqlBuilder = new StringBuilder(sql);

		LinkedList<Object> params = new LinkedList<Object>();

		appendIfNotEmpty(sqlBuilder, "m.taxonomy_id",typeIds, params);
		appendIfNotEmpty(sqlBuilder, "c.module", modules, params);
		appendIfNotEmpty(sqlBuilder, "c.style",styles, params);
		appendIfNotEmpty(sqlBuilder, "c.slug",slugs, params);
		appendIfNotEmpty(sqlBuilder, "c.user_id", userIds, params);
		appendIfNotEmpty(sqlBuilder, "c.parent_id",parentIds, params);

		appendIfNotEmpty(sqlBuilder,"t.slug",typeSlugs, params);

		if (null != tags && tags.length > 0) {
			appendIfNotEmpty(sqlBuilder, "t.name",tags, params);
			sqlBuilder.append(" AND t.taxonomy_module='tag' ");
		}

		if (null != keyword && !"".equals(keyword.trim())) {
			sqlBuilder.append(" AND c.title like ?");
			params.add("'%"+keyword+"%'");
		}

		sqlBuilder.append("GROUP BY c.id");

		if (null != orderBy && !"".equals(orderBy)) {
			sqlBuilder.append(" ORDER BY ?");
			params.add(orderBy);
		} else {
			sqlBuilder.append(" ORDER BY ").append("c.created");
		}
		
		sqlBuilder.append(" LIMIT ?, ?");
		params.add( page -1);
		params.add(pagesize);

		return find(sqlBuilder.toString(), params.toArray());
	}

	/**
	 * @param array
	 * @param builder
	 * @param params
	 * @param colName
	 */
	private void appendIfNotEmpty(StringBuilder builder, String colName, Object[] array, LinkedList<Object> params) {
		if (null != array && array.length > 0) {
			builder.append(" AND (");
			for (int i = 0; i < array.length; i++) {
				if (i == 0) {
					builder.append(String.format(" %s = ? ", colName));
				} else {
					builder.append(String.format(" OR %s = ? ", colName));
				}
				params.add(array[i]);
			}
			builder.append(" ) ");
		}
	}

	public List<Content> findMenuList() {
		return doFind("module = ? order by order_id ASC", "menu");
	}

	public Content findBySlug(String slug) {
		return doFindFirst("slug = ?", slug);
	}

	public long findCountByModule(String module) {
		return doFindCount("module = ?", module);
	}

	public long findCountByModuleAndStatus(String module, String status) {
		return doFindCount("module = ? AND status = ?", module, status);
	}

	public long findCountInNormalByModule(String module) {
		return doFindCount("module = ? AND status <> ?", module, STATUS_DELETE);
	}

	public User findUser() {
		return User.findUserById(getUserId());
	}

	public int batchTrash(Long... ids) {
		if (ids != null && ids.length > 0) {
			Object[] paras = new Object[ids.length + 1];
			StringBuilder sb = new StringBuilder("UPDATE content SET status=? WHERE 1<>1 ");
			paras[0] = STATUS_DELETE;
			for (int i = 0; i < ids.length; i++) {
				paras[i + 1] = ids[i];
				sb.append(" OR id = ? ");
			}
			return Jdb.update(sb.toString(), paras);
		}
		return 0;
	}

	public List<Taxonomy> findTaxonomyList() {
		return Taxonomy.DAO.findListByContentId(getId());
	}

	public List<Taxonomy> findTaxonomyList(String type) {
		return Taxonomy.DAO.findListByTypeAndContentId(type, getId());
	}

	public String findTaxonomyListAsString(String type) {
		StringBuilder sb = new StringBuilder();
		List<Taxonomy> list = findTaxonomyList(type);
		if (list != null && list.size() > 0) {
			for (Taxonomy taxonomy : list) {
				sb.append(taxonomy.getTitle()).append(",");
			}
		}
		if (sb.length() > 0) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	public String getUsername() {
		return get("username");
	}

	public List<Metadata> getMetadatas() {
		if (metadatas == null) {
			String metadataString = get("metadatas");
			if (StringUtils.isNotBlank(metadataString)) {
				metadatas = new ArrayList<Metadata>();
				String medadataStrings[] = metadataString.split(",");
				if (medadataStrings != null && medadataStrings.length > 0) {
					for (String metadataStr : medadataStrings) {
						String[] propertes = metadataStr.split(":");
						// by method doPaginateByMetadata
						// propertes[0] == id
						// propertes[1] == meta_key
						// propertes[2] == meta_value
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

	public String getTagsAsString() {
		return getTaxonomyAsString(Taxonomy.TYPE_TAG);
	}
	
	public String getTagsAsUrl() {
		return getTaxonomyAsUrl(Taxonomy.TYPE_TAG);
	}

	public String getCategorysAsString() {
		return getTaxonomyAsString(Taxonomy.TYPE_CATEGORY);
	}

	public boolean isDelete() {
		return STATUS_DELETE.equals(getStatus());
	}

	public String getTaxonomyAsString(String type) {
		StringBuilder retBuilder = new StringBuilder();
		String taxonomyString = get("taxonomys");
		if (taxonomyString != null) {
			String[] taxonomyStrings = taxonomyString.split(",");
			for (String taxonomyStr : taxonomyStrings) {
				String[] propertes = taxonomyStr.split(":");
				// propertes[0] == id
				// propertes[1] == slug
				// propertes[2] == title
				// propertes[3] == type
				// by method doPaginateByModuleAndStatus
				if (propertes != null && propertes.length == 4) {
					if (type.equals(propertes[3])) {
						retBuilder.append(propertes[2]).append(",");
					}
				}
			}
		}

		return retBuilder.toString();
	}
	
	public String getTaxonomyAsUrl(String type) {
		StringBuilder retBuilder = new StringBuilder();
		String taxonomyString = get("taxonomys");
		if (taxonomyString != null) {
			String[] taxonomyStrings = taxonomyString.split(",");
			for (String taxonomyStr : taxonomyStrings) {
				String[] propertes = taxonomyStr.split(":");
				// propertes[0] == id
				// propertes[1] == slug
				// propertes[2] == title
				// propertes[3] == type
				// by method doPaginateByModuleAndStatus
				System.out.println("--->>>type:"+type);
				System.out.println(Arrays.toString(propertes));
				if (propertes != null && propertes.length == 4) {
					if (type.equals(propertes[3])) {
						String string = String.format("<a href=\"/%s/%s\" >%s</a>",getModule(),propertes[1],propertes[2]);
						retBuilder.append(string).append(",");
					}
				}
			}
		}
		
		if(retBuilder.length() > 0){
			retBuilder.deleteCharAt(retBuilder.length()-1);
		}
		
		return retBuilder.toString();
	}

	public List<Taxonomy> getTaxonomys() {
		if (taxonomys == null) {
			taxonomys = new ArrayList<Taxonomy>();
		}

		String taxonomyString = get("taxonomys");
		if (taxonomys != null) {
			String[] taxonomyStrings = taxonomyString.split(",");

			for (String taxonomyStr : taxonomyStrings) {
				String[] propertes = taxonomyStr.split(":");
				// by method doPaginateByModuleAndStatus
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

	public int getLayer() {
		return layer;
	}

	public String getLayerString() {
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
		if (this.childList == null) {
			this.childList = new ArrayList<Content>();
		}
		childList.add(child);
	}
	
	public String getUrl(){
		return JFinal.me().getContextPath()+"/c/"+(getSlug()==null?getId():getSlug());
	}

}
