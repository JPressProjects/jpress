package io.jpress.service.provider;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.aop.annotation.Bean;
import io.jboot.db.model.Columns;
import io.jpress.commons.utils.SqlUtils;
import io.jpress.service.WechatReplyService;
import io.jpress.model.WechatReply;
import io.jboot.service.JbootServiceBase;

import javax.inject.Singleton;

@Bean
@Singleton
public class WechatReplyServiceProvider extends JbootServiceBase<WechatReply> implements WechatReplyService {

    @Override
    public boolean deleteByIds(Object... ids) {
        return Db.update("delete from wechat_reply where id in  " + SqlUtils.buildInSqlPara(ids)) > 0;
    }

    @Override
    public Page<WechatReply> _paginate(int page, int pageSize, String keyword, String content) {
        Columns columns = new Columns();
        SqlUtils.likeAppend(columns, "keyword", keyword);
        SqlUtils.likeAppend(columns, "content", content);

        return DAO.paginateByColumns(page, pageSize, columns, "id desc");
    }
}