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
package io.jpress.commons.utils;

import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

public class ImageUtils {
    private static final Log log = Log.getLog(ImageUtils.class);

    private final static String[] imgExts = new String[]{"jpg", "jpeg", "png", "bmp"};

    public static String getExtName(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index != -1 && (index + 1) < fileName.length()) {
            return fileName.substring(index + 1);
        } else {
            return null;
        }
    }


    /**
     * 过文件扩展名，判断是否为支持的图像文件
     *
     * @param fileName
     * @return 是图片则返回 true，否则返回 false
     */
    public static boolean isImageExtName(String fileName) {
        if (StrKit.isBlank(fileName)) {
            return false;
        }
        fileName = fileName.trim().toLowerCase();
        String ext = getExtName(fileName);
        if (ext != null) {
            for (String s : imgExts) {
                if (s.equals(ext)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean notImageExtName(String fileName) {
        return !isImageExtName(fileName);
    }

    public static int[] ratio(String src) throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(src));
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        return new int[]{width, height};
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

    /**
     * 等比缩放，居中剪切，自动在在当前目录下生产新图
     * 例如：aaa.jpg 宽高为100和200，自动在当前目录下生成新图 aaa_100x200.jpg 的图
     *
     * @param src
     * @param w
     * @param h
     * @return 放回新图的路径
     * @throws IOException
     */
    public static String scale(String src, int w, int h) throws IOException {
        int inserTo = src.lastIndexOf(".");
        String dest = src.substring(0, inserTo) + String.format("_%sx%s", w, h) + src.substring(inserTo);
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

        if (notImageExtName(src)) {
            throw new IllegalArgumentException("只支持如下几种图片格式：jpg、jpeg、png、bmp");
        }

        Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(getExtName(src));
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

        save(targetBuffered, dest);
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

        return reader.read(0, param);
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
     * @param watermarkImg  水印图片位置
     * @param srcImageFile  源图片位置
     * @param destImageFile 生成的图片位置
     * @param position      水印打印的位置： 1->左上角，2->右上角，3->居中，4->左下角，5->右下角
     * @param xOffset       x轴偏移量，xOffset小于0，自动偏移
     * @param yOffset       y轴偏移量，yOffset小于0，自动偏移
     * @param radio         默认为原图的 1/4
     * @param alpha         透明度（0~1），PNG图片建议设置为1
     */
    public final static void pressImage(String watermarkImg, String srcImageFile, String destImageFile, int position,
                                        int xOffset, int yOffset, float radio, float alpha) {
        if (notImageExtName(srcImageFile)) {
            throw new IllegalArgumentException("只支持如下几种图片格式：jpg、jpeg、png、bmp");
        }

        Graphics2D graphics = null;
        try {
            File img = new File(srcImageFile);
            Image src = ImageIO.read(img);
            int srcWidth = src.getWidth(null);
            int srcHeight = src.getHeight(null);

            BufferedImage image = new BufferedImage(srcWidth, srcHeight, BufferedImage.TYPE_INT_RGB);
            graphics = image.createGraphics();
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

            save(image, destImageFile);

        } catch (Exception e) {
            log.warn("ImageUtils pressImage error", e);
        } finally {
            if (graphics != null) {
                graphics.dispose();
            }
        }
    }

    public static void zoom(int maxWidth, String srcImageFile, String destImageFile) {
        try {
            BufferedImage srcImage = ImageIO.read(new File(srcImageFile));
            int srcWidth = srcImage.getWidth();
            int srcHeight = srcImage.getHeight();

            // 当宽度在 maxWidth 范围之内，直接copy
            if (srcWidth <= maxWidth) {
                FileUtils.copyFile(new File(srcImageFile), new File(destImageFile));
            }
            // 当宽度超出 maxWidth 范围，将宽度变为 maxWidth，高度按比例缩放
            else {
                float scalingRatio = (float) maxWidth / (float) srcWidth;            // 计算缩放比率
                float maxHeight = ((float) srcHeight * scalingRatio);    // 计算缩放后的高度
                BufferedImage ret = resize(srcImage, maxWidth, (int) maxHeight);
                save(ret, destImageFile);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 剪切
     *
     * @param srcImageFile  原图
     * @param destImageFile 存放的目标位置
     * @param left          起始点：左
     * @param top           起始点：上
     * @param width         宽
     * @param height        高
     */
    public static void crop(String srcImageFile, String destImageFile, int left, int top, int width, int height) {

        if (notImageExtName(srcImageFile)) {
            throw new IllegalArgumentException("只支持如下几种图片格式：jpg、jpeg、png、bmp");
        }

        try {
            BufferedImage bi = ImageIO.read(new File(srcImageFile));
            width = Math.min(width, bi.getWidth());
            height = Math.min(height, bi.getHeight());
            if (width <= 0) {
                width = bi.getWidth();
            }
            if (height <= 0) {
                height = bi.getHeight();
            }

            left = Math.min(Math.max(0, left), bi.getWidth() - width);
            top = Math.min(Math.max(0, top), bi.getHeight() - height);

            BufferedImage subimage = bi.getSubimage(left, top, width, height);
            BufferedImage resizeImage = resize(subimage, width, height);

            save(resizeImage, destImageFile);

        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


    /**
     * 高保真缩放
     */
    private static BufferedImage resize(BufferedImage bi, int toWidth, int toHeight) {
        Graphics graphics = null;
        try {
            Image scaledImage = bi.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH);
            BufferedImage bufferedImage = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
            graphics = bufferedImage.getGraphics();
            graphics.drawImage(scaledImage, 0, 0, null);
            return bufferedImage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (graphics != null) {
                graphics.dispose();
            }
        }
    }


    private static void save(BufferedImage bi, String outputImageFile) {
        try {
            ImageIO.write(bi, getExtName(outputImageFile), new File(outputImageFile));
        } catch (Exception e) {
            log.error(e.toString(), e);
        }
    }


}