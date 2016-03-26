package io.jpress.plugin.search;

import java.io.IOException;
import java.util.List;

import com.jfinal.plugin.activerecord.Page;

/**
 * @title 搜索者
 * @description 用来做搜索的工作者
 * @company 北京思维夫网络科技有限公司 www.siweifu.com
 * @author Michael Yang
 * @version 1.0
 * @created 2015年3月20日 下午10:56:40
 */
public interface ISearcher {

	/**
	 * 初始化搜索引擎
	 * @return void
	 */
	public void init();

	public void addBean(SearcherBean bean) throws IOException;
	public void deleteBean(String beanId) throws IOException;
	public void updateBean(SearcherBean bean) throws IOException;

	/**
	 * 执行搜索
	 * @param keyword
	 * @return List<SearcherBean>
	 */
	public List<SearcherBean> search(String keyword) throws IOException;

	/**
	 * 分页搜索
	 * @param queryString 查询的语句
	 * @param pageNum     页码
	 * @param pageSize    分页大小
	 * @return
	 * @throws IOException
	 */
	public Page<SearcherBean> search(String queryString, String type, int pageNum, int pageSize) throws IOException;
}
