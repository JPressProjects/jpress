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

import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.IDataLoader;

import io.jpress.Consts;
import io.jpress.model.ModelSorter.ISortModel;
import io.jpress.model.base.BaseContent;
import io.jpress.model.core.Table;
import io.jpress.model.query.CommentQuery;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.TaxonomyQuery;
import io.jpress.model.query.UserQuery;
import io.jpress.model.utils.ContentRouter;
import io.jpress.model.utils.PageRouter;
import io.jpress.model.utils.TaxonomyRouter;
import io.jpress.template.TemplateManager;
import io.jpress.template.Thumbnail;
import io.jpress.utils.JsoupUtils;
import io.jpress.utils.StringUtils;

@Table(tableName = "content", primaryKey = "id")
public class Content extends BaseContent<Content> implements ISortModel<Content> {

	public static String STATUS_DELETE = "delete";
	public static String STATUS_DRAFT = "draft";
	public static String STATUS_NORMAL = "normal";

	public static String COMMENT_STATUS_OPEN = "open";
	public static String COMMENT_STATUS_CLOSE = "close";

	private static final long serialVersionUID = 1L;

	private List<Taxonomy> taxonomys;

	private int layer = 0;
	private List<Content> childList;
	private Content parent;
	private List<Metadata> metadatas;
	private User user;
	private Object object;

	public <T> T getTemp(Object key, IDataLoader dataloader) {
		return CacheKit.get("content_temp", key, dataloader);
	}

	public void clearTemp() {
		CacheKit.removeAll("content_temp");
	}

	@Override
	public boolean update() {
		if (getId() != null) {
			removeCache(getId());
		}
		if (getSlug() != null) {
			removeCache(getSlug());
		}

		clearTemp();

		return super.update();
	}

	@Override
	public boolean delete() {
		if (getId() != null) {
			removeCache(getId());
		}
		if (getSlug() != null) {
			removeCache(getSlug());
		}
		return super.delete();
	}

	@Override
	public boolean save() {
		if (getId() != null) {
			removeCache(getId());
		}

		if (getSlug() != null) {
			removeCache(getSlug());
		}

		clearTemp();

		return super.save();
	}

	public boolean updateCommentCount() {
		long count = CommentQuery.me().findCountByContentIdInNormal(getId());
		if (count > 0) {
			setCommentCount(count);
			return this.update();
		}
		return false;
	}

	public String getUsername() {
		return get("username");
	}

	public String getNickame() {
		return get("nickname");
	}

	public User getUser() {
		if (user != null)
			return user;

		if (getUserId() == null)
			return null;

		user = UserQuery.me().findById(getUserId());
		return user;
	}

	public Object getObject() {
		if (object != null) {
			return object;
		}

		if (getObjectId() == null) {
			return null;
		}

		object = ContentQuery.me().findById(getObjectId());
		return object;
	}

	public Object getUserObject() {
		if (object != null) {
			return object;
		}

		if (getObjectId() == null) {
			return null;
		}

		object = UserQuery.me().findById(getObjectId());
		return object;
	}

	public Object getTaxonomyObject() {
		if (object != null) {
			return object;
		}

		if (getObjectId() == null) {
			return null;
		}

		object = TaxonomyQuery.me().findById(getObjectId());
		return object;
	}

	public String getNicknameOrUsername() {
		return StringUtils.isNotBlank(getNickame()) ? getNickame() : getUsername();
	}

	public List<Metadata> getMetadatas() {
		if (metadatas == null) {
			String metadataString = get("metadatas");
			if (StringUtils.isNotBlank(metadataString)) {
				metadatas = new ArrayList<Metadata>();
				String medadataStrings[] = metadataString.split(",");
				for (String metadataStr : medadataStrings) {
					String[] propertes = metadataStr.split(":");
					// by method paginateByMetadata
					// propertes[0] == id
					// propertes[1] == meta_key
					// propertes[2] == meta_value
					Metadata md = new Metadata();
					md.setId(new BigInteger(propertes[0]));
					md.setObjectType(METADATA_TYPE);
					md.setMetaKey(propertes[1]);
					md.setMetaValue(propertes[2]);
					metadatas.add(md);
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

	public String getTagsAsUrl(String attrs) {
		return getTaxonomyAsUrl(Taxonomy.TYPE_TAG, attrs);
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
					if (type == null) {
						retBuilder.append(propertes[2]).append(",");
					} else if (type.equals(propertes[3])) {
						retBuilder.append(propertes[2]).append(",");
					}
				}
			}
		}

		if (retBuilder.length() > 0) {
			retBuilder.deleteCharAt(retBuilder.length() - 1);
		}
		return retBuilder.toString();
	}

	public String getTaxonomyAsUrl(String type) {
		return getTaxonomyAsUrl(type, null);
	}

	public String getTaxonomyAsUrl(String type, String attrs) {
		StringBuilder retBuilder = null;
		String taxonomyString = get("taxonomys");
		if (taxonomyString != null) {
			String[] taxonomyStrings = taxonomyString.split(",");
			if (StringUtils.isBlank(attrs))
				attrs = "";
			for (String taxonomyStr : taxonomyStrings) {
				if (retBuilder == null) {
					retBuilder = new StringBuilder();
				}
				String[] propertes = taxonomyStr.split(":");
				// propertes[0] == id
				// propertes[1] == slug
				// propertes[2] == title
				// propertes[3] == type
				// by method doPaginateByModuleAndStatus
				if (propertes != null && propertes.length == 4) {
					if (type.equals(propertes[3])) {
						String url = JFinal.me().getContextPath() + TaxonomyRouter.getRouter(getModule(), propertes[1]);
						String string = String.format("<a href=\"%s\" %s >%s</a>", url, attrs, propertes[2]);
						retBuilder.append(string).append(",");
					}
				}
			}
		}

		if (retBuilder != null) {
			if (retBuilder.length() > 0) {
				retBuilder.deleteCharAt(retBuilder.length() - 1);
			}
			return retBuilder.toString();
		} else {
			return null;
		}
	}

	public List<Taxonomy> getTaxonomys() {
		if (taxonomys == null) {
			taxonomys = new ArrayList<Taxonomy>();
		}

		String taxonomyString = get("taxonomys");
		if (taxonomys != null && taxonomyString != null) {
			String[] taxonomyStrings = taxonomyString.split(",");

			for (String taxonomyStr : taxonomyStrings) {
				String[] propertes = taxonomyStr.split(":");
				// by method doPaginateByModuleAndStatus
				Taxonomy taxonomy = new Taxonomy();
				taxonomy.setId(new BigInteger(propertes[0]));
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

	@Override
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

	public List<Content> getChildList() {
		return childList;
	}

	public boolean hasChild() {
		return childList != null && !childList.isEmpty();
	}

	public String getUrl() {
		String baseUrl = null;
		if (Consts.MODULE_PAGE.equals(this.getModule())) {
			baseUrl = PageRouter.getRouter(this);
		} else {
			baseUrl = ContentRouter.getRouter(this);
		}
		return JFinal.me().getContextPath() + baseUrl;
	}

	public String getUrlWithPageNumber(int pagenumber) {
		return ContentRouter.getRouter(this, pagenumber);
	}

	public String getFirstImage() {
		return JsoupUtils.getFirstImageSrc(getText());
	}

	public String firstImageByName(String name) {
		String imageSrc = getFirstImage();
		return imageByName(name, imageSrc);
	}

	public String getImage() {
		String image = getThumbnail();
		if (StringUtils.isBlank(image)) {
			image = getFirstImage();
		}
		return image;
	}

	private String imageByName(String name, String imageSrc) {
		if (StringUtils.isBlank(imageSrc)) {
			return null;
		}

		Thumbnail thumbnail = TemplateManager.me().currentTemplateThumbnail(name);
		if (thumbnail == null) {
			return imageSrc;
		}

		String nameOfImageSrc = thumbnail.getUrl(imageSrc);

		if (new File(PathKit.getWebRootPath(), nameOfImageSrc.substring(JFinal.me().getContextPath().length()))
				.exists()) {
			return nameOfImageSrc;
		}

		return imageSrc;
	}

	public String imageByIndex(int index, String name) {
		String imageSrc = imageByIndex(index);
		return imageByName(name, imageSrc);
	}

	public String thumbnailByName(String name) {
		String thumbnailSrc = getThumbnail();
		return imageByName(name, thumbnailSrc);
	}

	public int getImageCount() {
		List<String> list = JsoupUtils.getImageSrcs(getText());
		return list == null ? 0 : list.size();
	}

	public String imageByIndex(int index) {
		List<String> list = JsoupUtils.getImageSrcs(getText());
		if (list != null && list.size() > index - 1) {
			return list.get(index);
		}
		return null;
	}

	public String summaryWithLen(int len) {
		if (getText() == null)
			return null;
		String text = JsoupUtils.getText(getText());
		if (text != null && text.length() > len) {
			return text.substring(0, len);
		}
		return text;
	}

	public String getSummary() {
		String summary = super.getSummary();
		if (StringUtils.isBlank(summary)) {
			summary = summaryWithLen(100);
		}
		return summary;
	}

	public boolean isCommentEnable() {
		return !COMMENT_STATUS_CLOSE.equals(getCommentStatus());
	}

	public boolean isCommentClose() {
		return COMMENT_STATUS_CLOSE.equals(getCommentStatus());
	}

	@Override
	public void setSlug(String slug) {
		if (StringUtils.isNotBlank(slug)) {
			slug = slug.trim();
			if (StringUtils.isNumeric(slug)) {
				slug = "c" + slug; // slug不能为全是数字,随便添加一个字母，c代表content好了
			} else {
				slug = slug.replaceAll("(\\s+)|(\\.+)|(。+)|(…+)|[\\$,，？\\-?、；;:!]", "_");
				slug = slug.replaceAll("(?!_)\\pP|\\pS", "");
			}
		}
		super.setSlug(slug);
	}

}
