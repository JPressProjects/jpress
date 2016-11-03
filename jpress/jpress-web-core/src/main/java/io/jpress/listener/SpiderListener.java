package io.jpress.listener;

import com.jfinal.plugin.activerecord.Page;
import io.jpress.Consts;
import io.jpress.message.Actions;
import io.jpress.message.Message;
import io.jpress.message.MessageListener;
import io.jpress.message.annotation.Listener;
import io.jpress.model.Content;
import io.jpress.model.query.ContentQuery;
import io.jpress.model.query.MappingQuery;
import io.jpress.spider.bean.ContentSpider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by fjw on 10/31/16.
 */
@Listener(action = Actions.SPIDER_GET)
public class SpiderListener implements MessageListener {
    public static final int PAGESIZE = 50;
    private List<Content> mContent = new ArrayList<>();

    @Override
    public void onMessage(Message message) {
        synchronized (SpiderListener.class) {
            ContentSpider contentSpider = message.getData();
            if (mContent == null || mContent.isEmpty()) {
                Page<Content> page = ContentQuery.me().paginate(1, PAGESIZE, new String[]{"article"}, null, null, null, null, null);
                List<Content> temp = page.getList();
                if (temp != null) {
                    mContent.addAll(temp);
                }
            }
            int size = (mContent == null || mContent.isEmpty()) ? 0 : mContent.size();
            for (int i = 0; i < size; i++) {
                if (mContent.get(i).getTitle().equals(contentSpider.getTitle())) {
                    return;
                }
            }

            Content content = new Content();
            content.setTitle(contentSpider.getTitle());
            content.setSummary(contentSpider.getSubTitle());
//            content.setThumbnail(contentSpider.getImg().get(0));
            content.setCreated(new Date());
            content.setSlug(contentSpider.getTitle());
            content.setStatus(Content.STATUS_NORMAL);
            content.setModule(Consts.MODULE_ARTICLE);
            content.setUserId(BigInteger.valueOf(1));

            String text = "";
            String img = "<script>" +
                    "function showImg( url ) {" +
                    "var frameid = 'frameimg' + Math.random();" +
                    "window.img = '<img id=\"img\" src=\\''+url+'?'+Math.random()+'\\'  style=\"display: block; margin-left: auto; margin-right: auto;\" height=\"100%\"/>';" +
                    "document.write('<iframe id=\"'+frameid+'\" src=\"javascript:parent.img;\" frameBorder=\"0\" scrolling=\"no\" ></iframe>');}" +
                    "showImg('%s');</script>";
            String imgChildren = "<script>showImg('%s');</script>";
//            String img = "<p><img style=\"display: block; margin-left: auto; margin-right: auto;\" src=\"%s\" alt=\"\"></p>";
            String goAddress = "<p><strong><a href=\"%s\" target=\"_blank\">火速直达</a></strong></p>";
            List<String> imgs = contentSpider.getImg();
            for (int i = 0; i < imgs.size(); i++) {
                switch (i) {
                    case 0:
                        text += String.format(img, imgs.get(i)) + String.format(goAddress, contentSpider.getLink()) + contentSpider.getText();
                        break;
                    default:
                        text += String.format(imgChildren, imgs.get(i));
                        break;
                }
            }
            content.setText(text);

            if (!content.saveOrUpdate()) {
                return;
            }
            content.updateCommentCount();

            List<BigInteger> ids = new ArrayList<>();
            ids.add(BigInteger.valueOf(1));//tag
            ids.add(BigInteger.valueOf(2));//categroy
            MappingQuery.me().doBatchUpdate(content.getId(), ids.toArray(new BigInteger[0]));

            if (size >= PAGESIZE) {
                mContent.remove(PAGESIZE - 1);
            }

            if (mContent.isEmpty()) {
                mContent.add(content);
            } else {
                mContent.add(0, content);
            }

        }
    }
}
