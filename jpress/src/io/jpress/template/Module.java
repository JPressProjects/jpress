package io.jpress.template;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class Module {
	public static final String ARTICLE = "article";
	public static final String PAGE = "page";
	public static final String FORUM = "forum";
	public static final String MENU = "menu";
	public static final String QA = "qa";
	public static final String GOODS = "goods";
	public static final String GOODS_ORDER = "goods_order";
	public static final String USER_COLLECTION = "user_collection";

	private String title;
	private String name;
	private String listTitle;
	private String addTitle;
	private String commentTitle;
	private List<TaxonomyType> taxonomyTypes;

	public List<TaxonomyType> getTaxonomyTypes() {
		return taxonomyTypes;
	}

	public void setTaxonomyTypes(List<TaxonomyType> taxonomys) {
		this.taxonomyTypes = taxonomys;
	}

	public List<String> getStyles() {
		List<String> moduleStyles = null;

		File f = new File(TemplateUtils.getTemplatePath());
		String[] fileNames = f.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String fileName) {
				return fileName.startsWith("content_" + name + "_");
			}
		});
		if(fileNames!=null && fileNames.length > 0){
			moduleStyles = new ArrayList<String>();
			int start = ("content_" + name + "_").length();
			for (String fileName : fileNames) {
				moduleStyles.add(fileName.substring(start,fileName.lastIndexOf(".")));
			}
		}

		return moduleStyles;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getListTitle() {
		return listTitle;
	}

	public void setListTitle(String listTitle) {
		this.listTitle = listTitle;
	}

	public String getAddTitle() {
		return addTitle;
	}

	public void setAddTitle(String addTitle) {
		this.addTitle = addTitle;
	}

	public String getCommentTitle() {
		return commentTitle;
	}

	public void setCommentTitle(String commentTitle) {
		this.commentTitle = commentTitle;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TaxonomyType getTaxonomyTypeByType(String name) {
		List<TaxonomyType> tts = taxonomyTypes;
		if (null != tts && tts.size() > 0) {
			for (TaxonomyType type : tts) {
				if (type.getName().equals(name))
					return type;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Module [title=" + title + ", name=" + name + ", listTitle="
				+ listTitle + ", addTitle=" + addTitle + ", commentTitle="
				+ commentTitle + ", taxonomyTypes=" + taxonomyTypes + "]";
	}

	public static class TaxonomyType {
		public static final String TYPE_INPUT = "input";
		public static final String TYPE_SELECT = "select";

		private String title;
		private String name;
		private String formType = TYPE_SELECT;
		private Module module;

		public TaxonomyType(Module module) {
			this.module = module;
		}

		public String getTitle() {
			return title;
		}

		public void setTitle(String title) {
			this.title = title;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public Module getModule() {
			return module;
		}

		public String getFormType() {
			return formType;
		}

		public void setFormType(String formType) {
			this.formType = formType;
		}

		@Override
		public String toString() {
			return "TaxonomyType [title=" + title + ", name=" + name
					+ ", formType=" + formType + "]";
		}

	}

}
