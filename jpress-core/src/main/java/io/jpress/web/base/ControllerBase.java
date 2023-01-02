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
package io.jpress.web.base;

import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.Table;
import com.jfinal.plugin.activerecord.TableMapping;
import com.jfinal.upload.UploadFile;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jpress.JPressConsts;
import io.jpress.SiteContext;
import io.jpress.commons.utils.JsoupUtils;
import io.jpress.model.User;
import io.jpress.web.interceptor.AdminInterceptor;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
public abstract class ControllerBase extends JbootController {


    @NotAction
    public Long getIdPara() {
        Long id = getParaToLong();
        if (id == null) {
            //renderError 会直接抛出异常，阻止程序往下执行
            renderError(404);
        }
        return id;
    }


    @NotAction
    public Long getSiteId() {
        return getAttr(JPressConsts.ATTR_SITE_ID, 0L);
    }


    @NotAction
    @Override
    public <T> T getModel(Class<T> modelClass) {
        return getModel(modelClass,true);
    }


    @NotAction
    @Override
    public <T> T getModel(Class<T> modelClass, String modelName) {
        return getModel(modelClass, modelName, true);
    }


    @NotAction
    @Override
    public <T> T getModel(Class<T> modelClass, boolean skipConvertError) {
        Model model = (Model) removeOptionsAttr(super.getModel(modelClass, skipConvertError));
        putSiteIdIfNecessary((Class<? extends Model>) modelClass, model);
        return (T) model;
    }


    @NotAction
    @Override
    public <T> T getModel(Class<T> modelClass, String modelName, boolean skipConvertError) {
        Model model = (Model) removeOptionsAttr(super.getModel(modelClass, modelName, skipConvertError));
        putSiteIdIfNecessary((Class<? extends Model>) modelClass, model);
        return (T) model;
    }


    private <T> void putSiteIdIfNecessary(Class<? extends Model> modelClass, Model model) {
        Table table = TableMapping.me().getTable(modelClass);
        if (table != null && table.hasColumnLabel("site_id")) {
            model.setOrPut("site_id", SiteContext.getSiteId());
        }
    }


    //options 应该是通过代码去覆盖的，而非通过 options 来设置
    //此处是为了保证安全
    private <T> T removeOptionsAttr(T model) {
        if (model instanceof Model) {
            ((Model<?>) model).remove("options");
        }
        return model;
    }


    /**
     * 获得当前页面的页码
     *
     * @return
     */
    protected int getPagePara() {
        return getParaToInt("page", 1);
    }

    protected int getPageSizePara() {
        int pagesize = getParaToInt("pagesize", 0);
        if (pagesize == 0) {
            try {
                pagesize = getCookieToInt("pagesize", 0);
            } catch (Exception ex) {
            }
        }

        Integer spacing = getAttr(AdminInterceptor.ATTR_PAGINATE_SPACING, 10);
        if (pagesize <= 0 || pagesize > 100) {
            pagesize = spacing;
        }

        if (pagesize > spacing && pagesize % spacing != 0) {
            pagesize = spacing;
        }
        return pagesize;
    }


    protected void render404If(boolean condition) {
        if (condition) {
            renderError(404);
        }
    }

    @NotAction
    public String getCleanedOriginalPara(String name) {
        return JsoupUtils.clean(super.getOriginalPara(name));
    }


    @NotAction
    public Set<String> getParaSet(String name) {
        String ids = getPara(name);
        if (StrUtil.isBlank(ids)) {
            return null;
        }

        return StrUtil.splitToSet(ids, ",");
    }


    protected boolean notLoginedUserModel(Model model) {
        return !isLoginedUserModel(model, "user_id");
    }

    protected boolean notLoginedUserModel(Model model, String attrName) {
        return !isLoginedUserModel(model, attrName);
    }

    protected boolean isLoginedUserModel(Model model) {
        return isLoginedUserModel(model, "user_id");
    }

    protected boolean isLoginedUserModel(Model model, String attrName) {
        if (model == null) {
            return false;
        }
        Object userId = model.get(attrName);
        if (userId == null) {
            return false;
        }
        User loginedUser = getLoginedUser();
        if (loginedUser == null) {
            return false;
        }
        return userId.equals(loginedUser.getId());
    }

    protected User getLoginedUser() {
        return getAttr(JPressConsts.ATTR_LOGINED_USER);
    }


    @NotAction
    public String getEscapeHtmlPara(String name) {
        String value = super.getPara(name);
        if (value == null || "".equals(value)) {
            return null;
        }
        return StrUtil.escapeHtml(value.trim());
    }

    protected static final Ret OK = Ret.ok();
    protected static final Ret FAIL = Ret.fail();
    protected static final Map<Object, Ret> FAIL_RETS = new ConcurrentHashMap<>();

    @NotAction
    protected void renderOkJson() {
        renderJson(OK);
    }

    @NotAction
    protected void renderFailJson() {
        renderJson(FAIL);
    }

    @NotAction
    protected void renderFailJson(Object message) {
        renderFailJson(message, false);
    }

    /**
     * 对于某些高并发的接口，useCache 应该传入 true，减少 ret 的创建
     *
     * @param message
     * @param useCache
     */
    @NotAction
    protected void renderFailJson(Object message, boolean useCache) {
        if (!useCache) {
            renderJson(Ret.fail().set("message", message));
            return;
        }

        Ret ret = FAIL_RETS.get(message);
        if (ret == null) {
            ret = Ret.fail().set("message", message);
            FAIL_RETS.put(message, ret);
        }
        renderJson(ret);
    }


    @Override
    @NotAction
    public UploadFile getFile() {
        return super.getFirstFileOnly();
    }


}
