package io.jpress.plugin.search;

import com.jfinal.plugin.IPlugin;

public class SearcherPlugin implements IPlugin {

	private ISearcher mSearcher;

	@Override
	public boolean start() {
		mSearcher = SearcherFactory.createSearcher();
		SearcherKit.init(mSearcher);
		return true;
	}

	@Override
	public boolean stop() {
		mSearcher = null;
		return true;
	}

}
