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
package io.jpress.plugin.search;

public class SearcherBean {

	private String sid;
	private String title;
	private String description;
	private String content;

	private String otherInfo; // 保存其他信息，不是用来检索的

	public SearcherBean() {
	}

	public SearcherBean(String sid, String title, String description, String content, String otherInfo, String type) {
		this.sid = sid;
		this.title = title;
		this.description = description;
		this.content = content;
		this.otherInfo = otherInfo;
	}

	// public SearcherBean(Document doc){
	// this(doc.get("sid"), doc.get("title"), doc.get("description"),
	// doc.get("content"), doc.get("otherInfo"), doc.get("type"));
	// }

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getOtherInfo() {
		return otherInfo;
	}

	public void setOtherInfo(String otherInfo) {
		this.otherInfo = otherInfo;
	}

	// public Document toDocument() {
	// Document document = new Document();
	// FieldType idFieldType = createIdFieldType();
	// Field idField = new Field("sid", getSid(), idFieldType);
	// Field titleField = new Field("title", getTitle(), TextField.TYPE_STORED);
	// Field descriptionField = new Field("description", getDescription(),
	// TextField.TYPE_STORED);
	// Field contentField = new Field("content", getContent(),
	// TextField.TYPE_STORED);
	// Field otherInfoField = new Field("otherInfo", getContent(),
	// TextField.TYPE_STORED);
	// Field typeField = new Field("type", getType(), TextField.TYPE_STORED);
	//
	// document.add(idField);
	// document.add(titleField);
	// document.add(descriptionField);
	// document.add(contentField);
	// document.add(otherInfoField);
	// document.add(typeField);
	// return document;
	// }

	// private static FieldType createIdFieldType() {
	// FieldType fieldType = new FieldType();
	// fieldType.setIndexed(true);// 是否索引
	// fieldType.setStored(true);// 是否存储
	// fieldType.setTokenized(false);// 是否分类
	// fieldType.setOmitNorms(false);
	// return fieldType;
	// }

}
