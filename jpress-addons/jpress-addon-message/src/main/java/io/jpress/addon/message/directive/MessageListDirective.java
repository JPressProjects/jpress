package io.jpress.addon.message.directive;

import com.jfinal.aop.Inject;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.db.model.Columns;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.addon.message.model.JpressAddonMessage;
import io.jpress.addon.message.service.JpressAddonMessageService;

import java.util.List;


@JFinalDirective("messageList")
public class MessageListDirective extends JbootDirectiveBase {

    @Inject
    private JpressAddonMessageService service;

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        Columns columns = new Columns();
        columns.eq("show", true);
        List<JpressAddonMessage> jpressAddonMessageList = service.findListByColumns(columns);
        scope.setLocal("messageList", jpressAddonMessageList);
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
