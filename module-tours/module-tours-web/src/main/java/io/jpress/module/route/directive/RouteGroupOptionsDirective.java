package io.jpress.module.route.directive;

import com.google.common.base.Splitter;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.TreeMultimap;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.utils.StrUtil;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 旅游线路团期的插件
 *
 * @author Eric.Huang
 * @date 2019-03-11 11:10
 * @package io.jpress.module.route.directive
 **/
@JFinalDirective("routeGroupOptions")
public class RouteGroupOptionsDirective extends JbootDirectiveBase {

    static Map<Integer, String> map = new TreeMap<Integer, String>();

    static {
        map.put(0, "天天发团");
        map.put(1, "星期一");
        map.put(2, "星期二");
        map.put(3, "星期三");
        map.put(4, "星期四");
        map.put(5, "星期五");
        map.put(6, "星期六");
        map.put(7, "星期日");
    }

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {

        String groupCycle = getPara("groupCycle", scope);
        StringBuilder htmlBuilder = new StringBuilder();

        map.forEach((k, v) -> {

            boolean isChecked = false;
            if (StrUtil.isNotBlank(groupCycle)) {
                List<String> list = Splitter.on(",")
                        .omitEmptyStrings()
                        .trimResults()
                        .splitToList(groupCycle);

                if (list.contains(k.toString())) {
                    isChecked = true;
                }
            }

            if (k.intValue() == 0) {
                htmlBuilder.append("<label class=\"text-red\">");
            } else {
                htmlBuilder.append("<label>");
            }

            if (isChecked) {
                htmlBuilder.append("<input type=\"checkbox\" name=\"group\" class=\"minimal-red\" value = \"" + k + "\" checked >");
            } else {
                htmlBuilder.append("<input type=\"checkbox\" name=\"group\" class=\"minimal-red\" value = \"" + k + "\" >");
            }

            htmlBuilder.append(v);
            htmlBuilder.append("</label>");
            htmlBuilder.append("&nbsp;");

        });

        scope.setLocal("routeGroupOptionsHtml", htmlBuilder.toString());
        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }
}
