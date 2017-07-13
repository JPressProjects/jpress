package com;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jfinal.plugin.activerecord.Page;
import io.jpress.plugin.search.SearcherBean;
import io.jpress.searcher.Redisearcher;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by francis on 2017/7/13.
 */
public class RedisearcherTest {
    @Test
    public void add1() {
//        Redisearcher solrSearcher = new Redisearcher();
//        SearcherBean bean = new SearcherBean();
//        bean.setContent("14年12月9日 - 当然at命令也可以使用/interactive选项在前台运行。 使用at命令要注意两点: 因为运行...下一篇在vista或win7以上版本提升bat脚本的执行权限相关文章推...");
//        bean.setCreated(new Date());
//        HashMap<String, Object> tmpData = new HashMap<>();
//        tmpData.put(Redisearcher.tagFieldName, "jasdv1a,thdsread1");
//        bean.setData(tmpData);
//        bean.setDescription("起以前在hp-ux下写的shell守护进程,这回搞个windows下的bat版守护程序吧,当时晚上思路已经很迟钝了,就叫了个兄弟让他写了,上去后运行效果不错,至少昨晚我安心...");
//        bean.setSid("100");
//        bean.setTitle("2017-08 net thrthreasdadead");
//        bean.setUrl("/c/123123123123");
//        solrSearcher.addBean(bean);
    }

    @Test
    public void delete1() {
//        Redisearcher solrSearcher = new Redisearcher();
//        solrSearcher.deleteBean("100");
    }

    @Test
    public void testSearchInTags() {
//        Redisearcher solrSearcher = new Redisearcher();
//        Page<SearcherBean> tmp = solrSearcher.search("jasdv1a", "");
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(tmp));
    }

    @Test
    public void testSearchInTitle() {
//        Redisearcher solrSearcher = new Redisearcher();
//        Page<SearcherBean> tmp = solrSearcher.search("net", "");
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(tmp));
    }

    @Test
    public void testSearchInDesc() {
//        Redisearcher solrSearcher = new Redisearcher();
//        Page<SearcherBean> tmp = solrSearcher.search("shell", "");
//        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(tmp));
    }

}
