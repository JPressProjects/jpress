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
package io.jpress.module.page.controller;

import com.google.common.collect.Sets;
import com.jfinal.aop.Inject;
import com.jfinal.core.NotAction;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtil;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.wordsfilter.WordFilterUtil;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.model.User;
import io.jpress.module.page.PageNotifyKit;
import io.jpress.module.page.model.SinglePage;
import io.jpress.module.page.model.SinglePageCategory;
import io.jpress.module.page.model.SinglePageComment;
import io.jpress.module.page.service.SinglePageCategoryService;
import io.jpress.module.page.service.SinglePageCommentService;
import io.jpress.module.page.service.SinglePageService;
import io.jpress.service.OptionService;
import io.jpress.web.base.TemplateControllerBase;
import io.jpress.web.handler.JPressHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 */
@RequestMapping("/page")
public class PageController extends TemplateControllerBase {

    @Inject
    private SinglePageService pageService;

    @Inject
    private SinglePageCategoryService pageCategoryService;

    @Inject
    private SinglePageCommentService commentService;

    @Inject
    private OptionService optionService;

    private static final Set<String> excludePage = Sets.newHashSet("setting", "setting_v4", "layout", "header", "footer");

    public void index() {

        String idOrSlug = getIdOrSlug();

        SinglePage singlePage = StrUtil.isNumeric(idOrSlug)
                ? pageService.findById(idOrSlug)
                : pageService.findFirstBySlug(idOrSlug);

        if (singlePage == null || !singlePage.isNormal()) {
            renderTemplateView(idOrSlug);
        } else {
            renderPage(singlePage, idOrSlug);
        }

    }

    @Override
    @NotAction
    public String getIdOrSlug() {
        String idOrSlug = StrUtil.urlDecode(JPressHandler.getCurrentTarget()).substring(1);
        if (StrUtil.isBlank(idOrSlug)) {
            return idOrSlug;
        }

        int indexOf = idOrSlug.lastIndexOf("-");
        if (indexOf == -1) {
            return idOrSlug;
        }

        String lastString = idOrSlug.substring(indexOf + 1);
        if (StrUtil.isNumeric(lastString)) {
            return idOrSlug.substring(0, indexOf);
        } else {
            return idOrSlug;
        }
    }


    private void renderPage(SinglePage page, String slugOrId) {
        pageService.doIncViewCount(page.getId());

        //设置SEO信息
        setSeoInfos(page);

        //设置菜单高亮
        setMenuActive(menu -> menu.getUrl().indexOf("/") <= 1 && menu.isUrlStartWidth("/" + slugOrId));

        setAttr("page", page);

        String pageStyle = page.getStyle();
        if (StrUtil.isNotBlank(pageStyle)) {
            render(page.getHtmlView());
            return;
        }


        SinglePageCategory pageCategory = pageCategoryService.findById(page.getCategoryId());
        if (pageCategory != null) {
            render(pageCategory.getPageView());
        } else {
            render(page.getHtmlView());
        }
    }


    private void renderTemplateView(String slugOrId) {
        if (excludePage.contains(slugOrId)) {
            renderError(404);
            return;
        }

        String htmlView = slugOrId + ".html";
        if (hasTemplate(htmlView)) {
            //设置菜单高亮
            setMenuActive(menu -> menu.isUrlStartWidth("/" + slugOrId));
            render(htmlView);
        } else {
            renderError(404);
        }
    }


    private void setSeoInfos(SinglePage page) {
        setSeoTitle(StrUtil.isBlank(page.getMetaTitle()) ? page.getTitle() : page.getMetaTitle());
        setSeoKeywords(page.getMetaKeywords());
        setSeoDescription(StrUtil.isBlank(page.getMetaDescription())
                ? CommonsUtils.maxLength(page.getText(), 100)
                : page.getMetaDescription());
    }


    /**
     * 发布评论
     */
    public void postComment() {

        Long pageId = getParaToLong("pageId");
        Long pid = getParaToLong("pid");
        String nickname = getPara("nickname");
        String content = getPara("content");
        String email = getPara("email");
        String wechat = getPara("wechat");
        String qq = getPara("qq");

        if (pageId == null || pageId <= 0) {
            renderFailJson();
            return;
        }

        if (StrUtil.isBlank(content)) {
            renderJson(Ret.fail().set("message", "评论内容不能为空"));
            return;
        } else {
            content = StrUtil.escapeHtml(content);
        }

        //是否对用户输入验证码进行验证
        boolean vCodeEnable = JPressOptions.isTrueOrEmpty("page_comment_vcode_enable");
        if (vCodeEnable && !validateCaptcha("captcha")) {
            renderJson(Ret.fail().set("message", "验证码错误").set("errorCode", 2));
            return;
        }

        if (WordFilterUtil.isMatchedFilterWords(content)) {
            renderJson(Ret.fail().set("message", "非法内容，无法发布评论信息"));
            return;
        }


        SinglePage page = pageService.findById(pageId);
        if (page == null) {
            renderFailJson();
            return;
        }


        //是否开启评论功能
        boolean commentEnable = JPressOptions.isTrueOrEmpty("page_comment_enable");
        if (!commentEnable) {
            renderJson(Ret.fail().set("message", "评论功能已关闭"));
            return;
        }


        //是否允许未登录用户参与评论
        Boolean unLoginEnable = optionService.findAsBoolByKey("page_comment_unlogin_enable");
        if (unLoginEnable == null || unLoginEnable == false) {
            if (getLoginedUser() == null) {
                renderJson(Ret.fail().set("message", "未登录用户不能评论").set("errorCode", 9));
                return;
            }
        }

        SinglePageComment comment = new SinglePageComment();

        comment.setPageId(pageId);
        comment.setContent(content);
        comment.setAuthor(nickname);
        comment.setPid(pid);
        comment.setEmail(email);
        comment.setWechat(wechat);
        comment.setQq(qq);

        User user = getLoginedUser();
        if (user != null) {
            comment.setUserId(user.getId());
            comment.setAuthor(user.getNickname());
        }

        //是否是管理员必须审核
        Boolean reviewEnable = optionService.findAsBoolByKey("page_comment_review_enable");
        if (reviewEnable != null && reviewEnable == true) {
            comment.setStatus(SinglePageComment.STATUS_UNAUDITED);
        }
        /**
         * 无需管理员审核、直接发布
         */
        else {
            comment.setStatus(SinglePageComment.STATUS_NORMAL);
        }


        commentService.saveOrUpdate(comment);

        if (pid != null) {
            //记录评论的回复数量
            commentService.doIncCommentReplyCount(pid);

            SinglePageComment parent = commentService.findById(pid);
            if (parent != null && parent.isNormal()) {
                comment.put("parent", parent);
            }
        }

        Ret ret = Ret.ok().set("code", 0);


        Map<String, Object> paras = new HashMap<>();
        paras.put("comment", comment);
        paras.put("page", page);
        if (user != null) {
            paras.put("user", user.keepSafe());
            comment.put("user", user.keepSafe());
        }

        renderHtmltoRet("/WEB-INF/views/commons/page/defaultPageCommentItem.html", paras, ret);

        PageNotifyKit.notify(page, comment, user);

        if (isAjaxRequest()) {
            renderJson(ret);
        } else {
            redirect(getReferer());
        }
    }

}
