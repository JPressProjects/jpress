/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.admin.controller;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.PathKit;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.upload.UploadFile;

import io.jpress.core.JBaseCRUDController;
import io.jpress.core.interceptor.ActionCacheClearInterceptor;
import io.jpress.model.Attachment;
import io.jpress.model.User;
import io.jpress.model.query.AttachmentQuery;
import io.jpress.model.query.OptionQuery;
import io.jpress.model.vo.Archive;
import io.jpress.router.RouterMapping;
import io.jpress.router.RouterNotAllowConvert;
import io.jpress.template.TemplateManager;
import io.jpress.template.Thumbnail;
import io.jpress.utils.AttachmentUtils;
import io.jpress.utils.FileUtils;
import io.jpress.utils.ImageUtils;

@RouterMapping(url = "/admin/attachment", viewPath = "/WEB-INF/admin/attachment")
@Before(ActionCacheClearInterceptor.class)
@RouterNotAllowConvert
public class _AttachmentController extends JBaseCRUDController<Attachment> {
	private static final Log log = Log.getLog(_AttachmentController.class);

	@Override
	public void index() {
		keepPara();
		Page<Attachment> page = AttachmentQuery.me().paginate(getPageNumber(), getPageSize(), null, null, null, null,
				getPara("k", "").trim(), getPara("dm"), getPara("mime"), null);

		setAttr("page", page);

		List<Archive> archives = AttachmentQuery.me().findArchives();
		setAttr("archives", archives);

		render("index.html");
	}

	public void detail_layer() {
		BigInteger id = getParaToBigInteger("id");
		Attachment attachment = AttachmentQuery.me().findById(id);
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
			log.error("detail_layer ratioAsString error", e);
		}
	}

	public void choose_layer() {
		keepPara();
		Page<Attachment> page = AttachmentQuery.me().paginate(getPageNumber(), getPageSize(), null, null, null, null,
				getPara("k", "").trim(), getPara("dm"), getPara("mime"), null);
		setAttr("page", page);
		render("choose_layer.html");
	}

	@Override
	protected int getPageSize() {
		return 18;
	}

	public void upload() {
		keepPara();
	}

	public void doUpload() {
		UploadFile uploadFile = getFile();
		if (null != uploadFile) {
			String newPath = AttachmentUtils.moveFile(uploadFile);
			User user = getLoginedUser();

			Attachment attachment = new Attachment();
			attachment.setUserId(user.getId());
			attachment.setCreated(new Date());
			attachment.setTitle(uploadFile.getOriginalFileName());
			attachment.setPath(newPath.replace("\\", "/"));
			attachment.setSuffix(FileUtils.getSuffix(uploadFile.getFileName()));
			attachment.setMimeType(uploadFile.getContentType());
			attachment.save();

			processImage(newPath);

			JSONObject json = new JSONObject();
			json.put("success", true);
			json.put("src", getRequest().getContextPath() + attachment.getPath());
			renderJson(json.toString());
		} else {
			renderJson("success", false);
		}
	}

	private void processImage(String newPath) {
		if (!AttachmentUtils.isImage(newPath))
			return;

		if (".gif".equalsIgnoreCase(FileUtils.getSuffix(newPath))) {
			// 过滤 .gif 图片
			return;
		}

		try {
			// 由于内存不够等原因可能会出未知问题
			processThumbnail(newPath);
		} catch (Throwable e) {
			log.error("processThumbnail error", e);
		}
		try {
			// 由于内存不够等原因可能会出未知问题
			processWatermark(newPath);
		} catch (Throwable e) {
			log.error("processWatermark error", e);
		}
	}

	private void processThumbnail(String newPath) {
		List<Thumbnail> tbs = TemplateManager.me().currentTemplate().getThumbnails();
		if (tbs != null && tbs.size() > 0) {
			for (Thumbnail tb : tbs) {
				try {
					String newSrc = ImageUtils.scale(PathKit.getWebRootPath() + newPath, tb.getWidth(), tb.getHeight());
					processWatermark(FileUtils.removeRootPath(newSrc));
				} catch (IOException e) {
					log.error("processWatermark error", e);
				}
			}
		}
	}

	public void processWatermark(String newPath) {
		Boolean watermark_enable = OptionQuery.me().findValueAsBool("watermark_enable");
		if (watermark_enable != null && watermark_enable) {

			int position = OptionQuery.me().findValueAsInteger("watermark_position");
			String watermarkImg = OptionQuery.me().findValue("watermark_image");
			String srcImageFile = newPath;

			Float transparency = OptionQuery.me().findValueAsFloat("watermark_transparency");
			if (transparency == null || transparency < 0 || transparency > 1) {
				transparency = 1f;
			}

			srcImageFile = PathKit.getWebRootPath() + srcImageFile;

			File watermarkFile = new File(PathKit.getWebRootPath(), watermarkImg);
			if (!watermarkFile.exists()) {
				return;
			}

			ImageUtils.pressImage(watermarkFile.getAbsolutePath(), srcImageFile, srcImageFile, position, transparency);
		}
	}

}