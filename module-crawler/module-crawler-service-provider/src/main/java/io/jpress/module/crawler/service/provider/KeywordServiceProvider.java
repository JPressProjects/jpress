package io.jpress.module.crawler.service.provider;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.jfinal.aop.Inject;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import io.jboot.aop.annotation.Bean;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.DateUtils;
import io.jpress.model.Columns;
import io.jpress.module.crawler.model.KeywordCategory;
import io.jpress.module.crawler.model.vo.SearchEngineVO;
import io.jpress.module.crawler.service.KeywordCategoryService;
import io.jpress.module.crawler.service.KeywordService;
import io.jpress.module.crawler.model.Keyword;
import io.jboot.service.JbootServiceBase;
import org.apache.commons.lang3.RandomUtils;
import org.joda.time.DateTime;

import java.awt.dnd.peer.DropTargetPeer;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Bean
public class KeywordServiceProvider extends JbootServiceBase<Keyword> implements KeywordService {

    @Inject
    KeywordCategoryService categoryService;

    @Override
    public Page<Keyword> paginate(int pageNum, int pageSize, String inputKeywords, String categoryIds, String validSearchTypes,
          String checkedSearchTypes, Integer minLength, Integer maxLength,  Integer minNum, Integer maxNum,String orderBy) {

        Columns columns = Columns.create();

        if (StrUtil.notBlank(categoryIds)) {
            if (categoryIds.contains(",")) {
                columns.in("category_id", categoryIds);
            } else {
                columns.eq("category_id", categoryIds);
            }
        }

        if (minLength != null) {
            columns.ge("num", minLength);
        }

        if (maxLength != null) {
            columns.le("num", maxLength);
        }

        if (StrUtil.notBlank(inputKeywords)) {
            columns.regexp("title", inputKeywords);
        }

        isSearchEngineValid(columns, validSearchTypes, checkedSearchTypes);

        if (StrUtil.isBlank(orderBy)) {
            orderBy = "title asc";
        }

        return DAO.paginateByColumns(pageNum, pageSize, columns, orderBy);

    }

    @Override
    public List<String> findListByPage(int pageNum, int pageSize) {
        StringBuilder sql = new StringBuilder("select name from c_keyword");
        sql.append(" where id >=");
        sql.append(" (SELECT id FROM c_keyword LIMIT ?, 1)");
        sql.append(" limit ?");
        return Db.query(sql.toString(), pageNum * pageSize, pageSize);
    }

    @Override
    public List<String> findListByParams(String inputKeywords, String categoryIds, String validSearchTypes,
         String checkedSearchTypes, Integer minLength, Integer maxLength, Integer minNum, Integer maxNum, String orderBy) {

        /** 随机导出关键词 */
        if (minNum != null && maxNum != null) {
            int randomNum = RandomUtils.nextInt((Integer) minNum, (Integer) maxNum);
            return findRandomListByRandom(randomNum);
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT name");
        sqlBuilder.append(" FROM c_keyword");
        Columns columns = (Columns) Columns.create();

        if (StrUtil.notBlank(categoryIds)) {
            if (categoryIds.contains(",")) {
                columns.in("category_id", categoryIds);
            } else {
                columns.eq("category_id", categoryIds);
            }
        }

        if (minLength != null) {
            columns.ge("num", minLength);
        }

        if (maxLength != null) {
            columns.le("num", maxLength);
        }

        if (StrUtil.notBlank(inputKeywords)) {
            columns.regexp("title", inputKeywords);
        }

        isSearchEngineValid(columns, validSearchTypes, checkedSearchTypes);

        if (StrUtil.isBlank(orderBy)) {
            orderBy = "title asc";
        }

        return Db.query(columns.toMysqlSql() + " order by " + orderBy);
    }

    private List<String> findRandomListByRandom(Integer randomNum) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT name FROM c_keyword WHERE id >= ((SELECT MAX(id) FROM c_keyword)-(SELECT MIN(id) FROM c_keyword)) * RAND() + (SELECT MIN(id) FROM c_keyword)");
        sql.append(" LIMIT ?");
        return Db.query(sql.toString(), randomNum);
    }

    public List<Record> findKeywordCountByCategoryId() {
        String sql = "select category_id, count(category_id) as totalNum from c_keyword group by category_id";
        return Db.find(sql);
    }

    @Override
    public boolean batchSave(List<String> keywordList, Object categoryId, String categoryName) {

        boolean result = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                Object tmpCategoryId = null;
                if (categoryId == null) {
                    // 创建新分类
                    KeywordCategory category = categoryService.findByName(categoryName);
                    if (category == null) {
                        tmpCategoryId = categoryService.save(createKeywordCategory(categoryName));
                    }
                } else {
                    tmpCategoryId = categoryId;
                }

                List<String> sqlList = Lists.newArrayList();
                String createDate = DateTime.now().toString(DateUtils.DEFAULT_FORMATTER);
                for (String title : keywordList) {
                    title = title.replaceAll(" ", "");
                    if (StrKit.notBlank(title)) {
                        title = clearNotChinese(title);
                        sqlList.add(getDistinctInsertSQL(title, tmpCategoryId, categoryName, createDate));
                    }
                }

                Db.batch(sqlList, 10000);
                return true;
            }
        });
        return result;
    }

    @Override
    public boolean deleteByCategoryId(Object categoryId) {
        int totalNum = Db.delete("delete from c_keyword where category_id = ?", categoryId);
        if (totalNum > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByIds(Object... ids) {
        return DAO.deleteByIds(ids);
    }

    private String getDistinctInsertSQL(String title, Object categoryId, String categoryName, String createDate) {

        StringBuilder sqlBuilder = new StringBuilder("insert ignore into c_keyword(`title`, `category_id`, `category_name`, `num`, `pinyin`, `level`, `created`) values(");
        sqlBuilder.append("'").append(title).append("'").append(", ");
        sqlBuilder.append(categoryId).append(", ");
         sqlBuilder.append("'").append(categoryName).append("'").append(", ");

        List<Pinyin> list = HanLP.convertToPinyinList(title);
        String head = list.get(0).getHeadString();
        String pinyin = "none".equals(head) ? "-" : head;
        sqlBuilder.append("'").append(pinyin).append("'").append(", ");

        sqlBuilder.append(title.length()).append(", ");
        sqlBuilder.append(1).append(", ");
        sqlBuilder.append("'").append(createDate).append("'");
        sqlBuilder.append(")");

        return sqlBuilder.toString();
    }

    private void isSearchEngineValid(Columns columns, Object searchEngineTypes, Object disabledSearchEngineTypes) {

        if (searchEngineTypes != null) {
            List<String> list = Splitter.on(",").splitToList(searchEngineTypes.toString());
            list.stream().forEach(type -> {
                if (SearchEngineVO.BAIDU.equals(type)) {
                    columns.eq("is_baidu_enabled", 1).eq("is_baidu_checked", 1);
                } else if (SearchEngineVO.SOGO.equals(type)) {
                    columns.eq("is_sogo_enabled", 1).eq("is_sogo_checked", 1);
                } else if (SearchEngineVO.SOSO.equals(type)) {
                    columns.eq("is_soso_enabled", 1).eq("is_soso_checked", 1);
                } else if (SearchEngineVO.SHENMA.equals(type)) {
                    columns.eq("is_shenma_enabled", 1).eq("is_shenma_checked", 1);
                }
            });
        }

        if (disabledSearchEngineTypes != null) {
            List<String> list = Splitter.on(",").splitToList(disabledSearchEngineTypes.toString());
            list.stream().forEach(type -> {
                if (SearchEngineVO.BAIDU.equals(type)) {
                    columns.eq("is_baidu_enabled", 0).eq("is_baidu_checked", 1);
                } else if (SearchEngineVO.SOGO.equals(type)) {
                    columns.eq("is_sogo_enabled", 0).eq("is_sogo_checked", 1);
                } else if (SearchEngineVO.SOSO.equals(type)) {
                    columns.eq("is_soso_enabled", 0).eq("is_soso_checked", 1);
                } else if (SearchEngineVO.SHENMA.equals(type)) {
                    columns.eq("is_shenma_enabled", 0).eq("is_shenma_checked", 1);
                }
            });
        }
    }

    private KeywordCategory createKeywordCategory(String name) {

        KeywordCategory category = new KeywordCategory();
        category.setName(name);
        category.setCreated(new Date());
        category.setStatus(true);

        return category;
    }

    private String clearNotChinese(String buff) {
        //去掉所有中英文符号
        String tmpString = buff.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
        char[] carr = tmpString.toCharArray();
        for(int i = 0; i<tmpString.length();i++){
            if(carr[i] < 0xFF){
                //过滤掉非汉字内容
                //carr[i] = '' ;
            }
        }
        return String.copyValueOf(carr).trim();
    }

}