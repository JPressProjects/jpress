package io.jpress.plugin.search.searcher;

import io.jpress.plugin.search.ISearcher;
import io.jpress.plugin.search.SearcherBean;

import java.io.IOException;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

public class DbSearcher implements ISearcher {

	@Override
	public void init() {

	}

	@Override
	public void addBean(SearcherBean bean) {
		
	}

	@Override
	public void deleteBean(String beanId) {
		
	}

	@Override
	public void updateBean(SearcherBean bean) {
		
	}

	@Override
	public List<SearcherBean> search(String keyword) {
		return null;
	}

	@Override
	public Page<SearcherBean> search(String queryString, String type, int pageNum, int pageSize) throws IOException {
		return null;
	}

}
