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
package io.jpress.web.admin;

import com.jfinal.aop.Inject;
import com.jfinal.kit.PathKit;
import com.jfinal.kit.Ret;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressConsts;
import io.jpress.commons.utils.AttachmentUtils;
import io.jpress.commons.utils.ImageUtils;
import io.jpress.core.menu.annotation.AdminMenu;
import io.jpress.model.Attachment;
import io.jpress.service.AttachmentService;
import io.jpress.web.base.AdminControllerBase;

import java.io.File;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping(value = "/admin/attachment", viewPath = JPressConsts.DEFAULT_ADMIN_VIEW)
public class _AttachmentController extends AdminControllerBase {

    private static final Log LOG = Log.getLog(_AttachmentController.class);

    @Inject
    private AttachmentService as;

    @AdminMenu(text = "所有附件", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 0)
    public void index() {

        Page<Attachment> page = as._paginate(getPagePara(), 15, getPara("title"));
        setAttr("page", page);
        render("attachment/list.html");

    }

    @AdminMenu(text = "上传", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 1)
    public void upload() {
        render("attachment/upload.html");
    }


    public void browse() {
        Page<Attachment> page = as._paginate(getPagePara(), 10, getPara("title"));
        setAttr("page", page);
        render("attachment/browse.html");
    }


    public void detail() {
        Long id = getIdPara();

        Attachment attachment = as.findById(id);

        setAttr("attachment", attachment);

        File attachmentFile = new File(PathKit.getWebRootPath(), attachment.getPath());
        setAttr("attachmentName", attachmentFile.getName());

        long fileLen = attachmentFile.length();
        String fileLenUnit = "Byte";
        if (fileLen > 1024) {
            fileLen = fileLen / 1024;
            fileLenUnit = "KB";
        }
        if (fileLen > 1024) {
            fileLen = fileLen / 1024;
            fileLenUnit = "MB";
        }
        setAttr("attachmentSize", fileLen + fileLenUnit);
        try {
            if (AttachmentUtils.isImage(attachment.getPath())) {
                String ratio = ImageUtils.ratioAsString(attachmentFile.getAbsolutePath());
                setAttr("attachmentRatio", ratio == null ? "unknow" : ratio);
            }
        } catch (Throwable e) {
            LOG.error("detail() ratioAsString error", e);
        }

        render("attachment/detail.html");
    }

    public void doDel() {
        Long id = getParaToLong();
        if (id == null) {
            renderError(404);
            return;
        }
        as.deleteById(id);
        renderOkJson();
    }


    public void doUpdate() {
        Attachment attachment = getBean(Attachment.class);
        as.saveOrUpdate(attachment);
        renderOkJson();
    }


    @AdminMenu(text = "设置", groupId = JPressConsts.SYSTEM_MENU_ATTACHMENT, order = 2)
    public void setting() {
        render("attachment/setting.html");
    }

}
