package io.jpress.core.template;

import com.jfinal.kit.HashKit;
import com.jfinal.kit.LogKit;
import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.CommonsUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TemplateUtil {


    /**
     * 读取模板定义的 #blockContainer 容器配置
     *
     * @param templateFile
     * @return
     */
    public static Set<BlockContainerDef> readBlockContainers(File templateFile) {
        Set<BlockContainerDef> containers = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new FileReader(templateFile))) {
            String str;
            while ((str = in.readLine()) != null) {
                int indexOf = str.indexOf("#blockContainer");
                if (indexOf >= 0) {
                    int firstIndexOf = str.indexOf("(", indexOf) + 1;
                    int lastIndexOf = str.indexOf(")", indexOf);

                    String parasString = str.substring(firstIndexOf, lastIndexOf);
                    String[] paras = parasString.split(",");

                    BlockContainerDef container = new BlockContainerDef();
                    container.setId(paras[0].substring(1, paras[0].length() - 1));

                    container.setTemplateFile(templateFile.getName());

                    if (paras.length > 1) {
                        for (int i = 1; i < paras.length; i++) {
                            String blockId = paras[i].trim();
                            if (blockId.startsWith("\"") || blockId.startsWith("'")) {
                                blockId = blockId.substring(1);
                            } else if (blockId.endsWith("\"") || blockId.endsWith("'")) {
                                blockId = blockId.substring(0, blockId.length() - 1);
                            }
                            container.addSupportBlockId(blockId.trim());
                        }
                    }
                    containers.add(container);
                }
            }
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }
        return containers;
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
        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String lineStr;
            while ((lineStr = in.readLine()) != null) {
                parseLineString(block, lineStr);
            }
        } catch (IOException e) {
            LogKit.error(e.toString(), e);
        }

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
                    para = para.trim();
                    if (para.startsWith("\"") || para.startsWith("'")) {
                        para = para.substring(1, para.length() - 1);
                    }
                    if (index == 0) {
                        optionDef.setName(para);
                    } else if (index == 1) {
                        optionDef.setDefaultValue(para);
                    }
                    index++;
                }

                block.addOptionDef(optionDef);

                indexOf = lineStr.indexOf("blockOption", lastIndexOf);
            }
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
        return md5.substring(0, 6);
    }


    /**
     * 读取模板的 ID
     *
     * @param templateZipFile
     * @return
     */
    public static String readTemplateId(File templateZipFile) {
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(templateZipFile);
            Enumeration<?> entryEnum = zipFile.entries();
            while (entryEnum.hasMoreElements()) {
                InputStream is = null;
                try {
                    ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                    if (StringUtils.equalsAnyIgnoreCase(zipEntry.getName(), "template.properties")) {
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
}
