package io.jpress.module.crawler.listener;

import com.jfinal.aop.Aop;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.components.event.annotation.EventConfig;
import io.jpress.module.crawler.model.util.CrawlerConsts;
import io.jpress.module.crawler.service.KeywordCategoryService;

/**
 * 关键词分类
 *
 * @author Eric.Huang
 * @date 2019-05-18 13:26
 * @package io.jpress.module.crawler.listener
 **/

@EventConfig(action = {CrawlerConsts.PLUS_KEYWORD_NUM_EVENT_NAME, CrawlerConsts.MINUS_KEYWORD_NUM_EVENT_NAME})
public class KeywordCategoryEventListener implements JbootEventListener {

    private static final Log _LOG = Log.getLog(KeywordCategoryEventListener.class);

    @Override
    public void onEvent(JbootEvent event) {

        Ret param = event.getData();
        Integer num = param.getInt("num");
        String type = param.getStr("type");
        Long categoryId = param.getLong("categoryId");

        String sql = null;
        if (CrawlerConsts.ADD.equals(type)) {
            sql = "update c_keyword_category set total_num = total_num + ? where id = ?";
        } else {
            sql = "update c_keyword_category set total_num = total_num - ? where id = ?";
        }

        Db.update(sql, num, categoryId);
        Aop.get(KeywordCategoryService.class).deleteCacheById(categoryId);
    }
}
