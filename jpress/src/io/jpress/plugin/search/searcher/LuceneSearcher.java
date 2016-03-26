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
package io.jpress.plugin.search.searcher;
//package com.toutiaocms.plugin.search.core.lucene;
//
//import java.io.File;
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import org.apache.lucene.analysis.Analyzer;
//import org.apache.lucene.document.Document;
//import org.apache.lucene.index.DirectoryReader;
//import org.apache.lucene.index.IndexReader;
//import org.apache.lucene.index.IndexWriter;
//import org.apache.lucene.index.IndexWriterConfig;
//import org.apache.lucene.index.IndexWriterConfig.OpenMode;
//import org.apache.lucene.index.Term;
//import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
//import org.apache.lucene.queryparser.classic.QueryParser;
//import org.apache.lucene.search.BooleanClause;
//import org.apache.lucene.search.BooleanQuery;
//import org.apache.lucene.search.IndexSearcher;
//import org.apache.lucene.search.Query;
//import org.apache.lucene.search.ScoreDoc;
//import org.apache.lucene.search.TermQuery;
//import org.apache.lucene.search.TopDocs;
//import org.apache.lucene.store.Directory;
//import org.apache.lucene.store.FSDirectory;
//import org.apache.lucene.util.Version;
//import org.lionsoul.jcseg.analyzer.JcsegAnalyzer4X;
//import org.lionsoul.jcseg.core.JcsegTaskConfig;
//
//import com.jfinal.kit.PathKit;
//import com.jfinal.plugin.activerecord.Page;
//import com.toutiaocms.plugin.search.core.ISearcher;
//import com.toutiaocms.plugin.search.core.SearcherBean;
//
//public class LuceneSearcher implements ISearcher{
//
//	private String mIndexFilePath;
//
//	public LuceneSearcher() {}
//
//	@Override
//	public void init() {
//		mIndexFilePath = PathKit.getWebRootPath() + File.separator + "autocreate/lucnenes/";
//		File indexPath = new File(mIndexFilePath);
//		if(!indexPath.exists()) indexPath.mkdirs();
//
//		// 初始化
//		try ( IndexWriter indexWriter = createIndexWriter(); ){
//
//		} catch (IOException e) {
//			throw new RuntimeException("LuceneSearcher init error!", e);
//		}
//	}
//
//	public IndexSearcher getIndexSearcher() {
//		try {
//			Directory directory = FSDirectory.open(new File(mIndexFilePath));
//			IndexReader ireader = DirectoryReader.open(directory);
//			return new IndexSearcher(ireader);
//		} catch (IOException e) {
//			throw new RuntimeException("getIndexSearcher error!", e);
//		}
//	}
//
//	@Override
//	public void addBean(SearcherBean bean) throws IOException {
//		IndexWriter indexWriter = createIndexWriter();
//		indexWriter.addDocument(bean.toDocument());
//		indexWriter.commit();
//		indexWriter.close();
//	}
//
//	@Override
//	public void deleteBean(String beanId) throws IOException{
//		IndexWriter indexWriter = createIndexWriter();
//		Term term = new Term("sid", beanId);
//		indexWriter.deleteDocuments(term);
//		indexWriter.commit();
//		indexWriter.close();
//	}
//
//	@Override
//	public void updateBean(SearcherBean bean) throws IOException {
//		IndexWriter indexWriter = createIndexWriter();
//		Term term = new Term("sid", bean.getSid());
//		indexWriter.updateDocument(term, bean.toDocument());
//		indexWriter.commit();
//		indexWriter.close();
//	}
//
//	@Override
//	public List<SearcherBean> search(String keyword) {
//		IndexSearcher mIndexSearcher = getIndexSearcher();
//
//		List<SearcherBean> list = null;
//		try {
//			keyword = QueryParser.escape(keyword);
//			
//			String[] queries = { keyword, keyword ,keyword };
//			String[] fields = { "title", "description","content" };
//			BooleanClause.Occur[] flags = { BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD,BooleanClause.Occur.SHOULD };
//			
//			Query query = MultiFieldQueryParser.parse(queries, fields, flags,new JcsegAnalyzer4X(JcsegTaskConfig.COMPLEX_MODE));
//			
//			TopDocs topDocs = mIndexSearcher.search(query, 1000);//1000,最多搜索1000条结果
//			
//			if(topDocs!=null && topDocs.totalHits > 0){
//				list = new ArrayList<SearcherBean>();
//				
//				ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//				for (int i = 0; i < scoreDocs.length; i++) {
//					int docId = scoreDocs[i].doc;
//					Document doc = mIndexSearcher.doc(docId);
//					list.add(new SearcherBean(doc));
//				}
//			}
//			
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return list;
//	}
//
//	public IndexWriter createIndexWriter() throws IOException {
//		if(mIndexFilePath == null){
//			throw new NullPointerException("please invoke init() method first!");
//		}
//		Directory directory = FSDirectory.open(new File(mIndexFilePath));
//		Analyzer analyzer = new JcsegAnalyzer4X(JcsegTaskConfig.COMPLEX_MODE);
//
//		// 非必须(用于修改默认配置): 获取分词任务配置实例
//		JcsegAnalyzer4X jcseg = (JcsegAnalyzer4X) analyzer;
//		// 追加同义词到分词结果中, 需要在jcseg.properties中配置jcseg.loadsyn=1
//		JcsegTaskConfig config = jcseg.getTaskConfig();
//		// 追加拼音到分词结果中, 需要在jcseg.properties中配置jcseg.loadpinyin=1
//		config.setAppendCJKSyn(true);
//		// 更多配置, 请查看com.webssky.jcseg.core.JcsegTaskConfig类
//		config.setAppendCJKPinyin(true);
//
//		IndexWriterConfig iwConfig = new IndexWriterConfig(Version.LUCENE_4_10_4, jcseg);
//		iwConfig.setOpenMode(OpenMode.CREATE_OR_APPEND);
//		IndexWriter indexWriter = new IndexWriter(directory, iwConfig);
//		return indexWriter;
//	}
//
//	@Override
//	public Page<SearcherBean> search(String keyWord, String type, int pageNum, int pageSize) throws IOException {
//		IndexSearcher mIndexSearcher = getIndexSearcher();
//
//		TermQuery titleQuery   = new TermQuery(new Term("title", keyWord));
//		TermQuery desQuery     = new TermQuery(new Term("description", keyWord));
//		TermQuery contentQuery = new TermQuery(new Term("content", keyWord));
//		TermQuery typeQuery    = new TermQuery(new Term("type", type));
//
//		BooleanQuery query = new BooleanQuery();
//		query.add(titleQuery, BooleanClause.Occur.SHOULD);
//		query.add(desQuery, BooleanClause.Occur.SHOULD);
//		query.add(contentQuery, BooleanClause.Occur.SHOULD);
//		query.add(typeQuery, BooleanClause.Occur.SHOULD);
//
//		// 1000,最多搜索1000条结果
//		TopDocs topDocs = mIndexSearcher.search(query, 1000);
//		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
//
//		//查询的总记录数
//		int totalRow = scoreDocs.length;
//
//		//查询起始记录位置
//		int begin = pageSize * (pageNum - 1);
//		//查询终止记录位置
//		int end = Math.min(begin + pageSize, totalRow);
//
//		List<SearcherBean> list = new ArrayList<SearcherBean>();
//		for (int i = begin; i < end; i++) {
//			int docID = scoreDocs[i].doc;
//			Document doc = mIndexSearcher.doc(docID);
//			list.add(new SearcherBean(doc));
//		}
//
//		// 计算出总页数
//		int totalPage = (totalRow / pageSize);
//		if (totalRow % pageSize != 0) {
//			totalPage++;
//		}
//		return new Page<SearcherBean>(list, pageNum, pageSize, totalPage, totalRow);
//	}
//
//}
