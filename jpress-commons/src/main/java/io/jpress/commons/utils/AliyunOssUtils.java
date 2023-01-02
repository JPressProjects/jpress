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

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.GetObjectRequest;
import com.jfinal.kit.LogKit;
import com.jfinal.log.Log;
import io.jboot.utils.NamedThreadPools;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;

import java.io.File;
import java.util.concurrent.ExecutorService;


public class AliyunOssUtils {

    static Log log = Log.getLog(AliyunOssUtils.class);


    private static final String KEY_ENABLE = "attachment_aliyunoss_enable";
    private static final String KEY_ENDPOINT = "attachment_aliyunoss_endpoint";
    private static final String KEY_ACCESSKEYID = "attachment_aliyunoss_accesskeyid";
    private static final String KEY_ACCESSKEYSECRET = "attachment_aliyunoss_accesskeysecret";
    private static final String KEY_BUCKETNAME = "attachment_aliyunoss_bucketname";
    private static final String KEY_OSS_DEL = "attachment_aliyunoss_del";


    private static ExecutorService fixedThreadPool = NamedThreadPools.newFixedThreadPool(3,"aliyun-oss-upload");

    /**
     * 同步本地文件到阿里云OSS
     *
     * @param path
     * @param file
     * @return
     */
    public static void upload(String path, File file) {
        fixedThreadPool.execute(() -> {
            uploadsync(path, file);
        });
    }

    /**
     * 同步本地文件到阿里云OSS
     *
     * @param path
     * @param file
     * @return
     */
    public static boolean uploadsync(String path, File file) {

        boolean enable = JPressOptions.getAsBool(KEY_ENABLE);

        if (!enable || StrUtil.isBlank(path)) {
            return false;
        }

        path = removeFileSeparator(path);
        path = path.replace('\\', '/');

        String ossBucketName = JPressOptions.get(KEY_BUCKETNAME);
        OSSClient ossClient = createOSSClient();

        try {
            ossClient.putObject(ossBucketName, path, file);
            boolean success = ossClient.doesObjectExist(ossBucketName, path);
            if (!success) {
                LogKit.error("aliyun oss upload error! path:" + path + "\nfile:" + file);
            }
            return success;

        } catch (Throwable e) {
            log.error("aliyun oss upload error!!!", e);
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 如果文件以 / 或者 \ 开头，去除 / 或 \ 符号
     */
    private static String removeFileSeparator(String path) {
        while (path.startsWith("/") || path.startsWith("\\")) {
            path = path.substring(1, path.length());
        }
        return path;
    }

    /**
     * 同步 阿里云OSS 到本地
     *
     * @param path
     * @param toFile
     * @return
     */
    public static boolean download(String path, File toFile) {
        boolean enable = JPressOptions.getAsBool(KEY_ENABLE);

        if (!enable || StrUtil.isBlank(path)) {
            return false;
        }

        path = removeFileSeparator(path);
        String ossBucketName = JPressOptions.get(KEY_BUCKETNAME);
        OSSClient ossClient = createOSSClient();
        try {

            if (!toFile.getParentFile().exists()) {
                toFile.getParentFile().mkdirs();
            }

            if (!toFile.exists()) {
                toFile.createNewFile();
            }
            ossClient.getObject(new GetObjectRequest(ossBucketName, path), toFile);
            return true;
        } catch (Throwable e) {
            log.error("aliyun oss download error!!!  path:" + path + "   toFile:" + toFile, e);
            if (toFile.exists()) {
                toFile.delete();
            }
            return false;
        } finally {
            ossClient.shutdown();
        }
    }

    private static OSSClient createOSSClient() {
        String endpoint = JPressOptions.get(KEY_ENDPOINT);
        String accessId = JPressOptions.get(KEY_ACCESSKEYID);
        String accessKey = JPressOptions.get(KEY_ACCESSKEYSECRET);
        return new OSSClient(endpoint, new DefaultCredentialProvider(accessId, accessKey), null);
    }
    /**
     * 删除一个OSS中的文件
     * @param objectName
     */
    public static void delete(String objectName){
        boolean ossDelEnable = JPressOptions.getAsBool(KEY_OSS_DEL);
        if (ossDelEnable){
            OSSClient ossClient = createOSSClient();
            try {
                ossClient.deleteObject(JPressOptions.get(KEY_BUCKETNAME), objectName);
            }catch (Exception e){

            }finally {
                ossClient.shutdown();
            }
        }
    }

}
