package io.jpress.controller.admin;

import io.jpress.core.JBaseController;
import io.jpress.core.annotation.UrlMapping;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.model.Attachment;
import io.jpress.model.User;
import io.jpress.utils.AttachmentUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.jfinal.upload.UploadFile;

@UrlMapping(url = "/admin/tinymce/image")
@Before(AdminInterceptor.class)
public class _TinymceImageController extends JBaseController {

	public void proxy() {
		String url = getPara("url");
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setInstanceFollowRedirects(true);
			conn.setUseCaches(true);
			InputStream is = conn.getInputStream();

			setHeader("Content-Type", conn.getContentType());

			render(new StreamRender(is));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void upload() {
		UploadFile uploadFile = getFile();
		String newPath = AttachmentUtils.moveFile(uploadFile);
		User user = getAttr("user");
		
		Attachment attachment = new Attachment();
		attachment.setUserId(user.getId());
		attachment.setCreated(new Date());
		attachment.setTitle("");
		attachment.setPath(newPath);
		attachment.setSuffix(AttachmentUtils.getFileExt(uploadFile.getFileName()));
		attachment.setMimeType(uploadFile.getContentType());
		
		attachment.save();
		
		renderJson("location", newPath);
	}
	
	
	public class StreamRender extends Render{
		final InputStream stream;
		public StreamRender(InputStream is) {
			this.stream = is;
		}

		@Override
		public void render() {
			InputStream inputStream = null;
	        OutputStream outputStream = null;
	        try {
	            inputStream = new BufferedInputStream(stream);
	            outputStream = response.getOutputStream();
	            byte[] buffer = new byte[1024];
	            for (int len = -1; (len = inputStream.read(buffer)) != -1;) {
	                outputStream.write(buffer, 0, len);
	            }
	            outputStream.flush();
	        } catch (IOException e) {
	        	if (getDevMode()) {
	        		throw new RenderException(e);
	        	}
	        } catch (Exception e) {
	        	throw new RenderException(e);
	        } finally {
	            if (inputStream != null)
	                try {inputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
	            if (outputStream != null)
	            	try {outputStream.close();} catch (IOException e) {LogKit.error(e.getMessage(), e);}
	        }
			
		}

	}

}


