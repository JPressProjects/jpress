package io.jpress.core.template;

import com.jfinal.core.JFinal;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import com.jfinal.render.RenderManager;
import com.jfinal.template.Engine;
import io.jboot.utils.FileUtil;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.CommonsUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TemplateUtil {


    /**
     * 读取模板定义的 #blockContainer 容器配置
     *
     * @param templateFile
     * @return
     */
    public static Set<BlockContainerDef> readContainerDefs(File templateFile) {
        Set<BlockContainerDef> containerDefs = new HashSet<>();
        try (BufferedReader in = getFileBufferedReader(templateFile)) {
            String str;
            while ((str = in.readLine()) != null) {
                int indexOf = str.indexOf("#blockContainer");
                if (indexOf >= 0) {
                    int firstIndexOf = str.indexOf("(", indexOf) + 1;
                    int lastIndexOf = str.indexOf(")", indexOf);

                    String parasString = str.substring(firstIndexOf, lastIndexOf);
                    String[] paras = parasString.split(",");

                    BlockContainerDef containerDef = new BlockContainerDef();
                    containerDef.setId(paras[0].substring(1, paras[0].length() - 1));

                    containerDef.setTemplateFile(templateFile.getName());

                    if (paras.length > 1) {
                        for (int i = 1; i < paras.length; i++) {
                            String blockId = paras[i].trim();
                            if (blockId.startsWith("\"") || blockId.startsWith("'")) {
                                blockId = blockId.substring(1);
                            } else if (blockId.endsWith("\"") || blockId.endsWith("'")) {
                                blockId = blockId.substring(0, blockId.length() - 1);
                            }
                            containerDef.addSupportBlockId(blockId.trim());
                        }
                    }
                    containerDefs.add(containerDef);
                }
            }
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }
        return containerDefs;
    }


    /**
     * 读取 block_***.html 文件的 title 和 icon 配置
     * 以及
     *
     * @param html
     * @param block
     */
    public static void readAndFillHtmlBlock(String html, HtmlBlock block) {
        String[] lineStrings = html.split("\n");
        for (String lineStr : lineStrings) {
            parseLineString(block, lineStr);
        }
    }


    /**
     * 读取 block_***.html 文件的 title 和 icon 配置
     * 以及
     *
     * @param file
     * @param block
     */
    public static void readAndFillHtmlBlock(File file, HtmlBlock block) {
        try (BufferedReader in = getFileBufferedReader(file)) {
            String lineStr;
            while ((lineStr = in.readLine()) != null) {
                parseLineString(block, lineStr);
            }
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }

    }


    private static BufferedReader getFileBufferedReader(File file) throws IOException {
        return new BufferedReader(
                new InputStreamReader(new FileInputStream(file), "UTF-8"));
    }


    private static void parseLineString(HtmlBlock block, String lineStr) {
        lineStr = lineStr.trim();
        if (lineStr.startsWith("<!--") && lineStr.endsWith("-->")) {
            lineStr = lineStr.substring(4, lineStr.length() - 3).trim();

            if (lineStr.startsWith("name:")) {
                block.setName(lineStr.substring(5));
            } else if (lineStr.startsWith("icon:")) {
                block.setIcon(lineStr.substring(5));
            } else if (lineStr.startsWith("type:")) {
                block.setType(lineStr.substring(5));
            } else if (lineStr.startsWith("index:")) {
                block.setIndex(lineStr.substring(6));
            }
        } else {
            block.addTemplateLine(lineStr);

            int indexOf = lineStr.indexOf("blockOption");

            while (indexOf >= 0) {
                int firstIndexOf = lineStr.indexOf("(", indexOf) + 1;
                int lastIndexOf = lineStr.indexOf(")", indexOf);

                String parasString = lineStr.substring(firstIndexOf, lastIndexOf);
                String[] paras = parasString.split(",");


                int index = 0;
                HtmlBlockOptionDef optionDef = new HtmlBlockOptionDef();
                for (String para : paras) {
                    if (index == 0) {
                        optionDef.setName(evalOptionDefName(para));
                    } else if (index == 1) {
                        optionDef.setDefaultValue(evalDefaultValue(para));
                    }
                    index++;
                }

                block.addOptionDef(optionDef);

                indexOf = lineStr.indexOf("blockOption", lastIndexOf);
            }
        }
    }


    private static String evalOptionDefName(String para) {
        para = para.trim();
        if (para.startsWith("\"") || para.startsWith("'")) {
            para = para.substring(1, para.length() - 1);
        }
        return para;
    }


    private static String evalDefaultValue(String text) {
        try {
            Engine engine = RenderManager.me().getEngine();
            Map paras = new HashMap();
            paras.put("CPATH", JFinal.me().getContextPath());
            return engine.getTemplateByString("#(" + text + " ??)").renderToString(paras);
        } catch (Exception e) {
            LogKit.error(e.toString(), e);
            return null;
        }
    }


    /**
     * 读取模板的 短ID （ md5(id).substring(0, 6) ）
     *
     * @param templateZipFile
     * @return
     */
    public static String readTemplateShortId(File templateZipFile) {
        String orignalId = readTemplateId(templateZipFile);
        if (StrUtil.isBlank(orignalId)) {
            return null;
        }

        String md5 = HashKit.md5(orignalId.trim());
        return md5.substring(0, 8);
    }


    /**
     * 读取模板的 ID
     *
     * @param templateZipFile
     * @return
     */
    public static String readTemplateId(File templateZipFile) {
        try {
            return readTemplateId(templateZipFile, "UTF-8");
        } catch (IllegalArgumentException e) {
            return readTemplateId(templateZipFile, "GBK");
        }

    }


    public static String readTemplateId(File templateZipFile, String charset) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(templateZipFile, Charset.forName(charset));
            Enumeration<?> entryEnum = zipFile.entries();
            while (entryEnum.hasMoreElements()) {
                InputStream is = null;
                try {
                    ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (StringUtils.endsWith(zipEntry.getName(), "template.properties")) {
                        is = zipFile.getInputStream(zipEntry);
                        Properties properties = new Properties();
                        properties.load(new InputStreamReader(is, StandardCharsets.UTF_8));
                        return properties.getProperty("id");
                    }
                } finally {
                    CommonsUtils.quietlyClose(is);
                }
            }
        } catch (IOException ex) {
            LogKit.error(ex.toString(), ex);
        } finally {
            CommonsUtils.quietlyClose(zipFile);
        }
        return null;
    }


    public static void unzip(String zipFilePath, String targetPath, String charset) throws IOException {
        targetPath = FileUtil.getCanonicalPath(new File(targetPath));
        ZipFile zipFile = new ZipFile(zipFilePath, Charset.forName(charset));
        try {
            Enumeration<?> entryEnum = zipFile.entries();
            while (entryEnum.hasMoreElements()) {
                OutputStream os = null;
                InputStream is = null;
                try {
                    ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (!zipEntry.isDirectory()) {
                        if (isNotSafeFile(zipEntry.getName())) {
                            //Unsafe
                            continue;
                        }

                        File targetFile = new File(targetPath, zipEntry.getName());

                        if (!targetFile.toPath().normalize().startsWith(targetPath)) {
                            throw new IOException("Bad zip entry");
                        }

                        FileUtil.ensuresParentExists(targetFile);

                        os = new BufferedOutputStream(new FileOutputStream(targetFile));
                        is = zipFile.getInputStream(zipEntry);
                        byte[] buffer = new byte[4096];
                        int readLen = 0;
                        while ((readLen = is.read(buffer, 0, 4096)) > 0) {
                            os.write(buffer, 0, readLen);
                        }
                    }
                } finally {
                    FileUtil.close(is, os);
                }
            }
        } finally {
            FileUtil.close(zipFile);
        }
    }


    private static boolean isNotSafeFile(String name) {
        name = name.toLowerCase();
        return name.endsWith(".jsp") || name.endsWith(".jspx") || name.contains("..");
    }


}
