package com;

import io.jpress.plugin.search.SearcherBean;
import io.jpress.searcher.SolrSearcher;
import io.jpress.searcher.common.SearchEmum;
import org.junit.Assert;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by liwei.b on 2017/6/27.
 */
public class SolrSearcherTest {
    @Test
    public void formatKeywords0() {
        Assert.assertEquals("我看看", SolrSearcher.formatKeywords("我看看"));
    }

    @Test
    public void formatKeywords01() {
        Assert.assertEquals("我看看", SolrSearcher.formatKeywords("我看看 "));
    }

    @Test
    public void formatKeywords02() {
        Assert.assertEquals("我看看", SolrSearcher.formatKeywords("我看看 \t    \t"));
    }

    @Test
    public void formatKeywords1() {
        Assert.assertEquals("hi 我看看", SolrSearcher.formatKeywords("hi,我看看"));
    }

    @Test
    public void formatKeywords2() {
        Assert.assertEquals("hi 我看看", SolrSearcher.formatKeywords("hi，我看看"));
    }

    @Test
    public void formatKeywords3() {
        Assert.assertEquals("hi 我看看", SolrSearcher.formatKeywords("hi\t我看看"));
    }

    @Test
    public void formatKeywords4() {
        String str = "hi\t\t\t\t我看看";
        Assert.assertEquals("hi 我看看", SolrSearcher.formatKeywords(str));
    }

    @Test
    public void formatKeywords5() {
        Assert.assertEquals("hi 我看看", SolrSearcher.formatKeywords("hi   我看看"));
    }

    @Test
    public void formatKeywords6() {
        Assert.assertEquals("hi 我看看", SolrSearcher.formatKeywords("hi \t    \t  我看看"));
    }

    @Test
    public void formatKeywords7() {
        Assert.assertEquals(null, SolrSearcher.formatKeywords(null));
    }

    @Test
    public void formatKeywords8() {
        Assert.assertEquals(null, SolrSearcher.formatKeywords(""));
    }

    @Test
    public void formatKeywords9() {
        Assert.assertEquals(null, SolrSearcher.formatKeywords("       "));
    }

    @Test
    public void formatKeywords10() {
        Assert.assertEquals(null, SolrSearcher.formatKeywords("  \t   \t  "));
    }

    @Test
    public void add1() {
//        SolrSearcher solrSearcher = new SolrSearcher();
//        SearcherBean bean = new SearcherBean();
//        bean.setContent("14年12月9日 - 当然at命令也可以使用/interactive选项在前台运行。 使用at命令要注意两点: 因为运行...下一篇在vista或win7以上版本提升bat脚本的执行权限相关文章推...");
//        bean.setCreated(new Date());
//        HashMap<String, Object> tmpData = new HashMap<>();
//        tmpData.put(SearchEmum.ArtileTags, "jasdv1a,thdsread1");
//        bean.setData(tmpData);
//        bean.setDescription("起以前在hp-ux下写的shell守护进程,这回搞个windows下的bat版守护程序吧,当时晚上思路已经很迟钝了,就叫了个兄弟让他写了,上去后运行效果不错,至少昨晚我安心...");
//        bean.setSid("100");
//        bean.setTitle("2017-08 net thrthreasdadead");
//        bean.setUrl("/c/123123123123");
//        solrSearcher.addBean(bean);
    }
    //相同id 标示同一条记录
    @Test
    public void update2() {
//        SolrSearcher solrSearcher = new SolrSearcher();
//        SearcherBean bean = new SearcherBean();
//        bean.setContent("14年12月9日 - 当然at命令也可以使用/interactive选项在前台运行。 使用at命令要注意两点: 因为运行...下一篇在vista或win7以上版本提升bat脚本的执行权限相关文章推...");
//        bean.setCreated(new Date());
//        HashMap<String, Object> tmpData = new HashMap<>();
//        tmpData.put(SearchEmum.ArtileTags, "jasdv1a,thdsread1");
//        bean.setData(tmpData);
//        bean.setDescription("起以前在hp-ux下写的shell守护进程,这回搞个windows下的bat版守护程序吧,当时晚上思路已经很迟钝了,就叫了个兄弟让他写了,上去后运行效果不错,至少昨晚我安心...");
//        bean.setSid("100");
//        bean.setTitle("2017-08 net thrthreasdadead");
//        bean.setUrl("/c/123123123123");
//        solrSearcher.addBean(bean);
    }

    //每次更新的id不同的时候， 标示都是添加数据
    @Test
    public void update3() {
//        SolrSearcher solrSearcher = new SolrSearcher();
//        SearcherBean bean = new SearcherBean();
//        bean.setContent("14年12月9日 - 当然at命令也可以使用/interactive选项在前台运行。 使用at命令要注意两点: 因为运行...下一篇在vista或win7以上版本提升bat脚本的执行权限相关文章推...");
//        bean.setCreated(new Date());
//        HashMap<String, Object> tmpData = new HashMap<>();
//        tmpData.put(SearchEmum.ArtileTags, "jasdv1a,thdsread1");
//        bean.setData(tmpData);
//        bean.setDescription("起以前在hp-ux下写的shell守护进程,这回搞个windows下的bat版守护程序吧,当时晚上思路已经很迟钝了,就叫了个兄弟让他写了,上去后运行效果不错,至少昨晚我安心...");
//        String id = String.valueOf(System.currentTimeMillis());
//        System.out.println(id);
//        bean.setSid(id);
//        bean.setTitle("2017-08 net thrthreasdadead");
//        bean.setUrl("/c/123123123123");
//        solrSearcher.updateBean(bean);
    }

    @Test
    public void delete1(){
//        SolrSearcher solrSearcher = new SolrSearcher();
//        solrSearcher.deleteBean("1499138330801");
    }
}
