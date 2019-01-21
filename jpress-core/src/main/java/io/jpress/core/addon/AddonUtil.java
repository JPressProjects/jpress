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
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * 插件工具类
 */
public class AddonUtil {

    /**
     * 解压 zip 或者 jar 的资源文件
     *
     * @param addonFile
     * @throws IOException
     */
    public static void unzipResources(File addonFile) throws IOException {
        ZipFile zipFile = new ZipFile(addonFile);
        try {
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                while (entryEnum.hasMoreElements()) {
                    OutputStream os = null;
                    InputStream is = null;
                    try {
                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                        if (!zipEntry.isDirectory() && zipEntry.getName().startsWith("addons")) {
                            File targetFile = new File(PathKit.getWebRootPath() + File.separator + zipEntry.getName());
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
                        if (is != null)
                            is.close();
                        if (os != null)
                            os.close();
                    }
                }
            }
        } finally {
            zipFile.close();
        }
    }


    private static Map<String, AddonInfo> addonInfoCache = new ConcurrentHashMap<>();


    public static AddonInfo readAddonInfo(File addonFile) {
        AddonInfo addonInfo = addonInfoCache.get(addonFile.getAbsolutePath());
        if (addonInfo == null) {
            addonInfo = readAddonFromFile(addonFile);
            addonInfoCache.put(addonFile.getAbsolutePath(), addonInfo);
        }
        return addonInfo;
    }


    public static AddonInfo readAddonFromFile(File addonFile) {
        ZipFile zipFile = null;
        try {
            new ZipFile(addonFile);
            Enumeration<?> entryEnum = zipFile.entries();
            if (null != entryEnum) {
                while (entryEnum.hasMoreElements()) {
                    InputStream is = null;
                    try {
                        ZipEntry zipEntry = (ZipEntry) entryEnum.nextElement();
                        if (StringUtils.equalsAnyIgnoreCase(zipEntry.getName(), "addon.txt", "addon.properties")) {
                            is = zipFile.getInputStream(zipEntry);
                            Properties properties = new Properties();
                            properties.load(new InputStreamReader(is, "utf-8"));
                            return new AddonInfo(properties);
                        }
                    } finally {
                        if (is != null)
                            is.close();
                    }
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                zipFile.close();
            } catch (IOException e) {
            }
        }

        return null;
    }


}
