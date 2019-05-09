/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.module.route.controller;

import com.jfinal.aop.Aop;
import com.jfinal.aop.Inject;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jboot.web.validate.EmptyValidate;
import io.jboot.web.validate.Form;
import io.jpress.JPressConsts;
import io.jpress.commons.layer.SortKit;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.core.template.Template;
import io.jpress.core.template.TemplateManager;
import io.jpress.module.article.model.ArticleCategory;
import io.jpress.module.article.service.ArticleCategoryService;
import io.jpress.module.route.model.TGroup;
import io.jpress.module.route.model.TRoute;
import io.jpress.module.route.service.TGroupService;
import io.jpress.module.route.service.TRouteCategoryService;
import io.jpress.module.route.service.TRouteService;
import io.jpress.service.OptionService;
import io.jpress.web.base.AdminControllerBase;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Eric.Huang （ninemm@126.com）
 * @version V1.0
 * @Package io.jpress.module.page.controller.admin
 */
@RequestMapping(value = "/admin/route", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _TRouteController extends AdminControllerBase {//

    @Inject
    private TRouteService routeService;
    @Inject
    private TGroupService groupService;
    @Inject
    private ArticleCategoryService categoryService;
    @Inject
    private TRouteCategoryService routeCategoryService;

    @AdminMenu(text = "线路列表", groupId = "tours", order = 10)
    public void index() {

        String status = getPara("status");
        String title = getPara("title");
        String code = getPara("code");
        Long categoryId = getParaToLong("categoryId");

        Page<TRoute> page =
                StringUtils.isBlank(status)
                        ? routeService._paginateWithoutTrash(getPagePara(), 10, title, code, categoryId)
                        : routeService._paginateByStatus(getPagePara(), 10, title, code, categoryId, status);

        setAttr("page", page);

        int draftCount = routeService.findCountByStatus(TRoute.STATUS_DRAFT);
        int trashCount = routeService.findCountByStatus(TRoute.STATUS_TRASH);
        int normalCount = routeService.findCountByStatus(TRoute.STATUS_NORMAL);
        setAttr("draftCount", draftCount);
        setAttr("trashCount", trashCount);
        setAttr("normalCount", normalCount);
        setAttr("totalCount", draftCount + trashCount + normalCount);

        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        flagCheck(categories, categoryId);

        render("tours/route_list.html");
    }

    @AdminMenu(text = "添加线路", groupId = "tours", order = 20)
    public void edit() {

        List<ArticleCategory> categories = categoryService.findListByType(ArticleCategory.TYPE_CATEGORY);
        SortKit.toLayer(categories);
        setAttr("categories", categories);

        Integer routeId = getParaToInt(0, 0);
        TRoute route = null;
        if (routeId > 0) {
            route = routeService.findById(routeId);
            if (route == null) {
                renderError(404);
                return;
            }

            Long[] categoryIds = routeCategoryService.findCategoryIdsByRouteId(routeId);
            flagCheck(categories, categoryIds);
        } else {
            route = new TRoute();
            Long code = routeService.findMaxRouteCode();
            route.setCode(code);
            route.setSlug(code.toString());
        }
        setAttr("route", route);

        String editMode = route == null ? getCookie(JPressConsts.COOKIE_EDIT_MODE) : route.getEditMode();
        setAttr("editMode", JPressConsts.EDIT_MODE_MARKDOWN.equals(editMode)
                ? JPressConsts.EDIT_MODE_MARKDOWN
                : JPressConsts.EDIT_MODE_HTML);

        set("now",new Date());
        initStylesAttr("route_");
        render("tours/route_edit.html");
    }

    @EmptyValidate({
        @Form(name = "route.code", message = "线路编码不能为空"),
        @Form(name = "route.title", message = "线路标题不能为空"),
        @Form(name = "route.total_days", message = "线程天数不能为空"),
        @Form(name = "route.expire_date", message = "过期日期不能为空"),
        @Form(name = "route.child_price", message = "儿童价格不能为空"),
        @Form(name = "route.market_price", message = "市场价格不能为空"),
        @Form(name = "route.price", message = "成人价格不能为空")
    })
    public void doSave() {
        TRoute route = getModel(TRoute.class,"route");

        if (!validateSlug(route)) {
            renderJson(Ret.fail("message", "slug不能全是数字且不能包含字符：- "));
            return;
        }

        if (StrUtil.isNotBlank(route.getSlug())) {
            TRoute slugRoute = routeService.findFirstBySlug(route.getSlug());
            if (slugRoute != null && slugRoute.getId().equals(route.getId()) == false) {
                renderJson(Ret.fail("message", "该slug已经存在"));
                return;
            }
        }

        // 线路团期
        Integer[] groups = getParaValuesToInt("group");
        String groupCycle = StringUtils.join(groups, ",");
        route.setGroupCycle(groupCycle);

        // 线路积分
        Integer rate = Aop.get(OptionService.class).findAsIntegerByKey("route_price_score_rate");
        if (rate == null) {
            rate = 1;
        }
        route.setScore(route.getPrice() * rate);

        // TODO 生成线路二维码

        Long id = (Long) routeService.saveOrUpdate(route);
        setAttr("routeId",id);

        Long[] categoryIds = getParaValuesToLong("category");
        routeService.doUpdateCategorys(id, categoryIds);
        if (categoryIds != null && categoryIds.length > 0) {
            for (Long categoryId : categoryIds) {
                categoryService.updateCount(categoryId);
            }
        }

        // String calendarStr = getPara("calendarStr");
        groupService.doUpdateGroups(route, groups, null);
        TGroup group = groupService.findFirstGroupByRouteId(id);
        if (group != null) {
            route.setGroupId(group.getId());
            route.setDepartureDate(group.getLeaveDate());
        }

        route.saveOrUpdate();
        setAttr("route", route);
        Ret ret = id > 0 ? Ret.ok().set("id", id) : Ret.fail();
        renderJson(ret);
    }


    public void doDel() {
        Long id = getIdPara();
        render(routeService.deleteById(id) ? Ret.ok() : Ret.fail());
    }

    @EmptyValidate(@Form(name = "ids"))
    public void doDelByIds() {
        Set<String> idsSet = getParaSet("ids");
        render(routeService.deleteByIds(idsSet.toArray()) ? OK : FAIL);
    }

    public void copy() {
        Long id = getIdPara();

        TRoute route = routeService.findById(id);
        route.setId(null);

        Long maxCode = routeService.findMaxRouteCode();
        route.setCode(maxCode);
        route.setSlug(maxCode.toString());
        route.setCreated(new Date());

        Long newId = (Long) routeService.save(route);
        Long[] categoryIds = routeCategoryService.findCategoryIdsByRouteId(id);
        routeService.doUpdateCategorys(newId, categoryIds);

        List<TGroup> list = groupService.findGroupsByRouteId(id);
        groupService.doAddGroups(newId, list);

        if (list != null && list.size() > 0) {
            route.setGroupId(list.get(0).getId());
            route.setDepartureDate(list.get(0).getLeaveDate());
        }
        route.saveOrUpdate();

        redirect("/admin/route/");
    }

    public void doTrash() {
        Long id = getIdPara();
        render(routeService.doChangeStatus(id, TRoute.STATUS_TRASH) ? OK : FAIL);
    }

    public void doDraft() {
        Long id = getIdPara();
        render(routeService.doChangeStatus(id, TRoute.STATUS_DRAFT) ? OK : FAIL);
    }

    public void doNormal() {
        Long id = getIdPara();
        render(routeService.doChangeStatus(id, TRoute.STATUS_NORMAL) ? OK : FAIL);
    }

    private void initStylesAttr(String prefix) {
        Template template = TemplateManager.me().getCurrentTemplate();
        if (template == null){
            return;
        }
        List<String> styles = template.getSupportStyles(prefix);
        if (styles != null && !styles.isEmpty()) {
            setAttr("styles", styles);
        }
    }

    private void flagCheck(List<ArticleCategory> categories, Long... checkIds) {
        if (checkIds == null || checkIds.length == 0
                || categories == null || categories.size() == 0) {
            return;
        }

        for (ArticleCategory category : categories) {
            for (Long id : checkIds) {
                if (id != null && id.equals(category.getId())) {
                    category.put("isCheck", true);
                }
            }
        }
    }
}