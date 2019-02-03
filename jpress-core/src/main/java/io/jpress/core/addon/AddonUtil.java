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
package io.jpress.core.addon;

import com.jfinal.kit.PathKit;
import io.jboot.utils.FileUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 插件工具类
 */
public class AddonUtil {

    private static List<String> resourceSuffix = new ArrayList<String>();

    static {
        resourceSuffix.add(".html");
        resourceSuffix.add(".htm");
        resourceSuffix.add(".css");
        resourceSuffix.add(".js");
        resourceSuffix.add(".jpg");
        resourceSuffix.add(".jpeg");
        resourceSuffix.add(".png");
        resourceSuffix.add(".bmp");
        resourceSuffix.add(".gif");
        resourceSuffix.add(".webp");
        resourceSuffix.add(".svg");
        resourceSuffix.add(".ttf");
        resourceSuffix.add(".woff");
        resourceSuffix.add(".woff2");
        resourceSuffix.add(".webp");
    }

    private static boolean isResource(String name) {
        String suffix = FileUtil.getSuffix(name);
        return suffix != null && resourceSuffix.contains(suffix.toLowerCase());
    }

    /**
     * 解压 zip 或者 jar 的资源文件
     *
     * @param addonInfo
     * @throws IOException
     */
    public static void unzipResources(AddonInfo addonInfo) throws IOException {
        String basePath = PathKit.getWebRootPath()
                + File.separator
                + "addons"
                + File.separator
                + addonInfo.getId();

        ZipFile zipFile = new ZipFile(addonInfo.buildJarFile());
        try {
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                while (entryEnum.hasMoreElements()) {
                    OutputStream os = null;
                    InputStream is = null;
                    try {
                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                        if (!zipEntry.isDirectory() && isResource(zipEntry.getName())) {
                            File targetFile = new File(basePath + File.separator + zipEntry.getName());
                            if (!targetFile.getParentFile().exists()) {
                                targetFile.getParentFile().mkdirs();
                            }
                            os = new BufferedOutputStream(new FileOutputStream(targetFile));
                            is = zipFile.getInputStream(zipEntry);
                            byte[] buffer = new byte[4096];
                            int readLen = 0;
                            while ((readLen = is.read(buffer, 0, 4096)) > 0) {
                                os.write(buffer, 0, readLen);
                            }
                        }
                    } finally {
                        quietlyClose(is, os);
                    }
                }
            }
        } finally {
            quietlyClose(zipFile);
        }
    }


    private static Map<String, AddonInfo> addonInfoCache = new ConcurrentHashMap<>();


    public static AddonInfo readAddonInfo(File addonFile) {
        AddonInfo addonInfo = addonInfoCache.get(addonFile.getAbsolutePath());
        if (addonInfo == null) {
            addonInfo = readSimpleAddonInfo(addonFile);
            if (addonInfo == null) return null;
            new AddonClassLoader(addonInfo).load();
            addonInfoCache.put(addonFile.getAbsolutePath(), addonInfo);
        }
        return addonInfo;
    }


    public static AddonInfo readSimpleAddonInfo(File addonFile) {
        ZipFile zipFile = null;
        Properties addonProp = null;
        Properties addonConfigProp = null;
        try {
            zipFile = new ZipFile(addonFile);
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                while (entryEnum.hasMoreElements()) {
                    InputStream is = null;
                    try {
                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                        if (StringUtils.equalsAnyIgnoreCase(zipEntry.getName(), "addon.txt", "addon.properties")) {
                            is = zipFile.getInputStream(zipEntry);
                            addonProp = new Properties();
                            addonProp.load(new InputStreamReader(is, "utf-8"));
                        } else if (StringUtils.equalsAnyIgnoreCase(zipEntry.getName(), "config.txt", "config.properties")) {
                            is = zipFile.getInputStream(zipEntry);
                            addonConfigProp = new Properties();
                            addonConfigProp.load(new InputStreamReader(is, "utf-8"));
                        }
                    } finally {
                        quietlyClose(is);
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            quietlyClose(zipFile);
        }

        if (addonProp == null) {
            return null;
        }

        AddonInfo addonInfo = new AddonInfo(addonProp);
        if (addonConfigProp != null) {
            addonConfigProp.forEach((o, o2) -> {
                if (o != null && o2 != null) addonInfo.addConfig(o.toString(), o2.toString());
            });
        }

        return addonInfo;
    }

    public static void quietlyClose(Closeable... closeables) {
        if (closeables != null && closeables.length != 0)
            for (Closeable c : closeables)
                if (c != null) {
                    try {
                        c.close();
                    } catch (IOException e) {
                    }
                }
    }


}
