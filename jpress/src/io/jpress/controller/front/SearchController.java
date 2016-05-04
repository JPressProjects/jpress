package io.jpress.controller.front;

import java.io.IOException;
import java.util.List;

import com.jfinal.log.Log;

import io.jpress.core.annotation.UrlMapping;
import io.jpress.plugin.search.ISearcher;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.plugin.search.SearcherFactory;

@UrlMapping(url = "/s")
public class SearchController extends BaseFrontController {

	private static final Log log = Log.getLog(SearchController.class);

	public void index() {

		String keyword = getPara("k");
		int pageNumber = getParaToInt("n");
		int pageSize = getParaToInt("s");
		String module = getPara("m");

		ISearcher searcher = SearcherFactory.createSearcher();
		if (searcher != null) {
			try {
				List<SearcherBean> results = searcher.search(keyword, pageNumber, pageSize);
				setAttr("result", results);
			} catch (IOException e) {
				log.error("search error ", e);
			}
		}

		render("search.html");
	}

}
