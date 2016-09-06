package io.jpress.model.vo;

import java.util.ArrayList;
import java.util.List;

import io.jpress.model.Content;

public class NavigationMenu {

	private String url;
	private String title;
	private boolean active;
	private String activeClass;
	private String icon;

	private List<NavigationMenu> childList;

	public NavigationMenu(Content content, String activeClass) {
		this.url = content.getText();
		this.title = content.getTitle();
		this.icon = content.getFlag();
		this.active = "active".equals(content.getStr("active"));
		

		if (active) {
			this.activeClass = activeClass;
		}

		if (content.hasChild()) {
			List<Content> tempList = content.getChildList();
			for (Content c : tempList) {
				addChild(new NavigationMenu(c, activeClass));
			}
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void addChild(NavigationMenu child) {
		if (this.childList == null) {
			this.childList = new ArrayList<NavigationMenu>();
		}
		childList.add(child);
	}

	public String getActiveClass() {
		return activeClass;
	}

	public void setActiveClass(String activeClass) {
		this.activeClass = activeClass;
	}

	public List<NavigationMenu> getChildList() {
		return childList;
	}

	public boolean hasChild() {
		return childList != null && !childList.isEmpty();
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}
	
}
