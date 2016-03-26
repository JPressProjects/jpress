package io.jpress.plugin.search;

import java.io.IOException;
import java.util.List;

import com.jfinal.log.Log;

public class SearcherKit {

	private static final Log logger = Log.getLog(SearcherKit.class);

	private static ISearcher mSearcher;

	static void init(ISearcher mSearcher) {
		SearcherKit.mSearcher = mSearcher;
	}

	static void add(SearcherBean bean) throws IOException {
		mSearcher.addBean(bean);
	}

	public static void delete(String beanId) throws IOException {
		mSearcher.deleteBean(beanId);
	}

	public static void update(SearcherBean bean) throws IOException {
		mSearcher.deleteBean(bean.getSid());
		mSearcher.addBean(bean);
	}

	/**
	 * 执行搜索
	 * 
	 * @param keyword
	 * @return List<SearcherBean>
	 */
	public static List<SearcherBean> search(String keyword) {
		try {
			return mSearcher.search(keyword);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		return null;
	}

}
