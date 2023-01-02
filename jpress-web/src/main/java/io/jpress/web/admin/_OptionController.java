/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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
import com.jfinal.kit.Func;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.JPressOptions;
import io.jpress.SiteContext;
import io.jpress.service.OptionService;
import io.jpress.web.base.AdminControllerBase;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 后台选项配置
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/option", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _OptionController extends AdminControllerBase {


    private static final Set<String> ignoreKeys = Sets.newHashSet("csrf_token");
    private static final String siteIdParaName = "__siteId";

    @Inject
    private OptionService optionService;


    /**
     * 保存（更新）配置信息
     */
    public void doSave() {

        Long siteId = getParaToLong(siteIdParaName);
        if (siteId == null) {
            siteId = SiteContext.getSiteId();
        }


        Enumeration<String> paraNames = getParaNames();
        if (paraNames == null || !paraNames.hasMoreElements()) {
            renderJson(Ret.fail("msg", "para is empty"));
            return;
        }

        HashMap<String, String> datasMap = new HashMap<>();
        while (paraNames.hasMoreElements()) {
            String key = paraNames.nextElement();
            if (ignoreKeys.contains(key) || StrUtil.isBlank(key)) {
                continue;
            }
            String value = getPara(key);
            datasMap.put(key, value);
        }

        //remove siteIdName
        datasMap.remove(siteIdParaName);

        Long finalSiteId = siteId;
        SiteContext.execInSite((Func.F00) () -> {
            for (Map.Entry<String, String> entry : datasMap.entrySet()) {
                //Mysql 对于字符串不区分大小写，所以保持统一
                String key = entry.getKey().trim();
                optionService.saveOrUpdate(key, entry.getValue(), finalSiteId);
                JPressOptions.set(key, entry.getValue(), finalSiteId);
            }
        },siteId);


        renderOkJson();
    }

    /**
     * 通过key值删除数据
     *
     * @Author Mr.xu
     * @CreateDate: 2019/4/28
     */
    public void doDeleteByKey(String key) {
        if (optionService.deleteByKey(key)) {
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
        optionService.saveOrUpdate(key, value);
        JPressOptions.set(key, value);
        renderOkJson();
    }

}
