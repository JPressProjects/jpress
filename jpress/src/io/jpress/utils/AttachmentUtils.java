package io.jpress.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import com.jfinal.kit.PathKit;
import com.jfinal.upload.UploadFile;

public class AttachmentUtils {

	static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

	/**
	 * @param uploadFile
	 * @return new file relative path
	 */
	public static String moveFile(UploadFile uploadFile) {

		File file = uploadFile.getFile();
		String webRoot = PathKit.getWebRootPath();

		String uuid = UUID.randomUUID().toString().replace("-", "");

		StringBuilder newFileName = new StringBuilder(webRoot)
				.append(File.separator).append("attachment")
				.append(File.separator).append(dateFormat.format(new Date()))
				.append(File.separator).append(uuid)
				.append(getFileExt(file.getName()));

		File newfile = new File(newFileName.toString());

		if (!newfile.getParentFile().exists()) {
			newfile.getParentFile().mkdirs();
		}

		file.renameTo(newfile);

		return newfile.getAbsolutePath().substring(webRoot.length());

	}

	public static String getFileExt(String fileName) {
		return fileName.substring(fileName.lastIndexOf('.'));
	}

}
