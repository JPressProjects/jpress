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
package io.jpress.utils;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import com.jfinal.log.Log;

public class ImageUtils {
	private static final Log log = Log.getLog(ImageUtils.class);

	public static int[] ratio(String src) throws IOException {
		BufferedImage bufferedImage = ImageIO.read(new File(src));
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		return new int[] { width, height };
	}

	public static String ratioAsString(String src) throws IOException {
		File file = new File(src);
		if (!file.exists()) {
			return null;
		}
		BufferedImage bufferedImage = ImageIO.read(file);
		int width = bufferedImage.getWidth();
		int height = bufferedImage.getHeight();
		return String.format("%s x %s", width, height);
	}

	public static String scale(String src, int w, int h) throws IOException {
		int inserTo = src.lastIndexOf(".");
		String dest = src.substring(0, inserTo) + String.format("_%sx%s", w, h) + src.substring(inserTo, src.length());
		scale(src, dest, w, h);
		return dest;
	}

	/**
	 * 等比缩放，居中剪切
	 * 
	 * @param src
	 * @param dest
	 * @param w
	 * @param h
	 * @throws IOException
	 */
	public static void scale(String src, String dest, int w, int h) throws IOException {
		String srcSuffix = src.substring(src.lastIndexOf(".") + 1);
		Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(srcSuffix);
		ImageReader reader = (ImageReader) iterator.next();

		InputStream in = new FileInputStream(src);
		ImageInputStream iis = ImageIO.createImageInputStream(in);
		reader.setInput(iis);

		BufferedImage srcBuffered = readBuffereImage(reader, w, h);
		BufferedImage targetBuffered = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

		Graphics graphics = targetBuffered.getGraphics();
		graphics.drawImage(srcBuffered.getScaledInstance(w, h, Image.SCALE_DEFAULT), 0, 0, null); // 绘制缩小后的图

		graphics.dispose();
		srcBuffered.flush();

		ImageIO.write(targetBuffered, srcSuffix, new File(dest));
		targetBuffered.flush();
	}

	private static BufferedImage readBuffereImage(ImageReader reader, int w, int h) throws IOException {
		ImageReadParam param = reader.getDefaultReadParam();
		int srcWidth = reader.getWidth(0);
		int srcHeight = reader.getHeight(0);

		Rectangle rect = null;

		if ((float) w / h > (float) srcWidth / srcHeight) {
			h = h * srcWidth / w;
			w = srcWidth;
			rect = new Rectangle(0, (srcHeight - h) / 2, w, h);
		} else {
			w = w * srcHeight / h;
			h = srcHeight;
			rect = new Rectangle((srcWidth - w) / 2, 0, w, h);
		}
		param.setSourceRegion(rect);
		BufferedImage srcBuffered = reader.read(0, param);
		return srcBuffered;
	}

	public final static void pressImage(String watermarkImg, String srcImageFile) {
		pressImage(watermarkImg, srcImageFile, srcImageFile, 5, -1, -1, 0.2f, 1);
	}

	public final static void pressImage(String watermarkImg, String srcImageFile, String destImageFile) {
		pressImage(watermarkImg, srcImageFile, destImageFile, 5, -1, -1, 0.2f, 1);
	}

	public final static void pressImage(String watermarkImg, String srcImageFile, String destImageFile, int position,
			float alpha) {
		pressImage(watermarkImg, srcImageFile, destImageFile, position, -1, -1, 0.2f, alpha);
	}

	/**
	 * @param watermarkImg
	 *            水印图片位置
	 * @param srcImageFile
	 *            源图片位置
	 * @param destImageFile
	 *            生成的图片位置
	 * @param position
	 *            水印打印的位置： 1->左上角，2->右上角，1->居中，1->左下角，1->右下角
	 * @param xOffset
	 *            x轴偏移量，xOffset小于0，自动偏移
	 * @param yOffset
	 *            y轴偏移量，yOffset小于0，自动偏移
	 * @param radio
	 *            默认为原图的 1/4
	 * @param alpha
	 *            透明度（0~1），PNG图片建议设置为1
	 */
	public final static void pressImage(String watermarkImg, String srcImageFile, String destImageFile, int position,
			int xOffset, int yOffset, float radio, float alpha) {
		try {
			File img = new File(srcImageFile);
			Image src = ImageIO.read(img);
			int srcWidth = src.getWidth(null);
			int srcHeight = src.getHeight(null);

			BufferedImage image = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
			Graphics2D graphics = image.createGraphics();
			graphics.drawImage(src, 0, 0, srcWidth, srcHeight, null);

			// 水印文件
			Image wmImage = ImageIO.read(new File(watermarkImg));
			int wmWidth = wmImage.getWidth(null);
			int wmHeight = wmImage.getHeight(null);

			radio = radio <= 0 ? 0.2f : radio;
			int newWidth = (int) (srcWidth * radio);
			int newHeight = (int) (wmHeight * (newWidth / (float) wmWidth));

			xOffset = (xOffset < 0) ? (int) (newWidth * 0.1f) : xOffset;
			yOffset = (yOffset < 0) ? (int) (newHeight * 0.1f) : yOffset;

			int xPostion = 0;
			int yPostion = 0;

			switch (position) {
			case 1:
				xPostion = xOffset;
				yPostion = yOffset;
				break;
			case 2:
				xPostion = (int) (srcWidth * (1 - radio) - xOffset);
				yPostion = yOffset;
				break;
			case 3:
				xPostion = (int) (srcWidth - newWidth) / 2;
				yPostion = (int) (srcHeight - newHeight) / 2;
				break;
			case 4:
				xPostion = xOffset;
				yPostion = (int) (srcHeight - newHeight - yOffset);
				break;
			case 5:
				xPostion = (int) (srcWidth * (1 - radio) - xOffset);
				yPostion = (int) (srcHeight - newHeight - yOffset);
				break;
			default:
				xPostion = xOffset;
				yPostion = yOffset;
				break;
			}

			graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, alpha));
			graphics.drawImage(wmImage, xPostion, yPostion, newWidth, newHeight, null);
			// 水印文件结束
			graphics.dispose();
			ImageIO.write((BufferedImage) image, "JPEG", new File(destImageFile));
		} catch (Exception e) {
			log.warn("ImageUtils pressImage error", e);
		}
	}

}