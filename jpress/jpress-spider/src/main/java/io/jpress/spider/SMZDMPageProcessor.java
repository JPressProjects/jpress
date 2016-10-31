package io.jpress.spider;

import com.jayway.jsonpath.JsonPath;
import com.jfinal.log.Log;
import io.jpress.message.Actions;
import io.jpress.message.MessageKit;
import io.jpress.spider.bean.ContentSpider;
import io.jpress.spider.inter.SpriderInterface;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 * @since 0.5.1
 */
public class SMZDMPageProcessor implements PageProcessor, SpriderInterface {

    private Log logger = Log.getLog(getClass());
    public static final String SMZDM_URL = "http://m.smzdm.com/ajax_home_list_show?time_sort=";
    public static final String SMZDM_P_URL = "http://m.smzdm.com/p/";
    private Site site = Site.me().setRetryTimes(3).setSleepTime(0);
    private int count;
    private int page_size = 20;
    private Spider mSpider;

    @Override
    public void process(Page page) {
        synchronized (SMZDMPageProcessor.class) {
            String url = page.getUrl().toString();
            if (url != null && url.startsWith(SMZDM_URL)) {
                Json json = page.getJson();
                String time_s = JsonPath.read(json.get(), "$.time_sort");
                int message = JsonPath.read(json.get(), "$.message");
                if (message == 1) {
                    logger.info(time_s);
                    logger.info("============");
                    String data = JsonPath.read(json.get(), "$.data").toString();
//                logger.info(data);
                    Html html = new Html(data);
                    List<String> b = html.xpath("li/h2/text()").all();
                    List<String> a = html.xpath("li/a/@href|//h2/text()|//div/em/text()").all();
                    int size = a.size();
                    if (size % page_size == 0)
                        for (int i = 0; i < size / 3; i++) {
                            logger.info("url" + a.get(i) + "标题" + a.get(i + page_size) + "价格" + a.get(i + page_size * 2));
                            page.addTargetRequest(a.get(i));
                        }
                    if (count < 3) {
                        page.addTargetRequest(SMZDM_URL + time_s);
                    }
                    count++;
                }
            } else if (url != null && url.startsWith(SMZDM_P_URL)) {
                Html html = page.getHtml();
                /**
                 * 图片
                 * 标题
                 * url详情
                 * 详情
                 */
                html.xpath("//div[@class='owl-carousel']/div/img/@src").get();
                html.xpath("//div[@class='details-title']/div[@class='title']/text()").get();
                html.xpath("//div[@class='details-title']/a/@href").get();
                html.xpath("//article/p/text()").get();

                List<String> detail = html.xpath("//div[@class='details-title']/div[@class='title']/text()" +
                        "|//div[@class='details-title']/div[@class='title']/span/text()" +
                        "|//div[@class='details-title']/a/@href" +
                        "|//div[@class='owl-carousel']/div/img/@src" +
                        "|//article/p").all();
                List imgs = new ArrayList<String>();
                ContentSpider contentSpider = new ContentSpider();
                contentSpider.setImg(imgs);
                for (int i = 0, size = detail.size(); i < size; i++) {
                    String str = detail.get(i);
                    switch (i) {
                        case 0://标题
                            contentSpider.setTitle(str);
                            break;
                        case 1://子标题
                            contentSpider.setSubTitle(str);
                            break;
                        case 2://链接
                            contentSpider.setLink(str);
                            break;
                        default://图片&文本
                            if (str.startsWith("http")) {
                                imgs.add(str);
                            } else {
                                contentSpider.setText(contentSpider.getText() + str);
                            }
                            break;

                    }
                    if (i == size - 1) {
                        MessageKit.sendMessage(Actions.SPIDER_GET, contentSpider);
                    }
                }
                for (String info : detail) {
                    logger.info(info);
                }
            }
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
//        Spider.create(new SMZDMPageProcessor()).addUrl("http://m.smzdm.com/p/6542544/").thread(1).run();
        new SMZDMPageProcessor().spriderStart();
    }

    @Override
    public void spriderStart() {
        mSpider = Spider.create(this).addUrl(SMZDM_URL + System.currentTimeMillis() / 1000).thread(1);
        mSpider.run();
    }

    @Override
    public boolean isRunning() {
        return mSpider.getStatus() == Spider.Status.Running;
    }
}
