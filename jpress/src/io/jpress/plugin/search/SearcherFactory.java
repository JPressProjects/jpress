package io.jpress.plugin.search;

import io.jpress.plugin.search.searcher.SolrSearcher;

public class SearcherFactory {

	private static ISearcher mSearcher;

	public static ISearcher createSearcher() {
		mSearcher = new SolrSearcher();
		// 初始化搜索引擎
		mSearcher.init();
		return mSearcher;
	}

}
