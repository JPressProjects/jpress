/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
 * <p>
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.gnu.org/licenses/lgpl-3.0.txt
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.web.admin;

import com.google.common.collect.Sets;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.service.OptionService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/option", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _OptionController extends AdminControllerBase {

//    private static final Set<String> allowHtmlTagKeys = Sets.newHashSet("wechat_reply_user_subscribe"
//            , "wechat_reply_user_scan"
//            , "wechat_reply_unknow"
//            , "web_copyright"
//    );

    private static final Set<String> ignoreKeys = Sets.newHashSet("csrf_token");

    @Inject
    private OptionService service;

    public void doSave() {

        Enumeration<String> paraNames = getParaNames();
        if (paraNames == null || !paraNames.hasMoreElements()) {
            renderJson(Ret.fail("msg", "para is empty"));
            return;
        }


        HashMap<String, String> datasMap = new HashMap<>();
        while (paraNames.hasMoreElements()) {
            String key = paraNames.nextElement();
            if (ignoreKeys.contains(key)){
                continue;
            }
            String value = getPara(key);
            datasMap.put(key, value);
        }


        for (Map.Entry<String, String> entry : datasMap.entrySet()) {
            //Mysql 对于字符串不区分大小写，所以保持统一
            String key = entry.getKey().trim();
            service.saveOrUpdate(key, entry.getValue());
            JPressOptions.set(key, entry.getValue());
        }

        renderOkJson();
    }

    /**
     * 通过key值删除数据
     *
     * @Author Mr.xu
     * @CreateDate: 2019/4/28
     */
    public void doDeleteByKey(String key) {
        if (service.deleteByKey(key)) {
            JPressOptions.set(key, null);
            renderOkJson();
        } else {
            renderFailJson();
        }
    }

    /**
     * 通过key保存或更新数据
     *
     * @Author Mr.xu
     * @CreateDate: 2019/4/28
     */
    public void doSaveOrUpdate(String key, String value) {
        service.saveOrUpdate(key, value);
        JPressOptions.set(key, value);
        renderOkJson();
    }

}
