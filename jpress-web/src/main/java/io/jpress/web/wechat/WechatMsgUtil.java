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
package io.jpress.web.wechat;

import com.jfinal.log.Log;
import com.jfinal.weixin.sdk.api.AccessTokenApi;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.api.CustomServiceApi;
import com.jfinal.weixin.sdk.api.MediaApi;
import com.jfinal.weixin.sdk.utils.HttpUtils;
import com.jfinal.weixin.sdk.utils.JsonUtils;
import io.jboot.utils.NamedThreadPools;
import io.jpress.commons.utils.AttachmentUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;


public class WechatMsgUtil {

    private static final Log LOG = Log.getLog(WechatMsgUtil.class);
    private static ExecutorService pool = NamedThreadPools.newFixedThreadPool(5,"wechat-msg");

    public static void sendImageAsync(String openId, String imageFilePath) {
        pool.submit(() -> {
            sendImage(openId, imageFilePath);
        });
    }

    public static void sendImageAsync(String openId, File imageFile) {
        pool.submit(() -> {
            sendImage(openId, imageFile);
        });
    }

    public static void sendTextAsync(String openId, String text) {
        pool.submit(() -> {
            sendText(openId, text);
        });
    }


    public static boolean sendText(String openId, String text) {
        ApiResult result = CustomServiceApi.sendText(openId, text);
        return result.isSucceed();
    }


    public static boolean sendImage(String openId, String imageFilePath) {
        return sendImage(openId, AttachmentUtils.file(imageFilePath));
    }

    public static boolean sendImage(String openId, File imageFile) {

        if (!imageFile.exists() || !imageFile.isFile() || !imageFile.canRead()) {
            return false;
        }

        /**
         * 上传临时素材
         * {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
         */
        ApiResult apiResult = MediaApi.uploadMedia(MediaApi.MediaType.IMAGE, imageFile);
        if (!apiResult.isSucceed()) {
            LOG.error("MediaApi.uploadMedia..." + imageFile + " \n" + apiResult.toString());
            return false;
        }

        String mediaId = apiResult.get("media_id");
        /**
         * 发送海报
         */
        ApiResult sendImageApiResult = CustomServiceApi.sendImage(openId, mediaId);
        if (!sendImageApiResult.isSucceed()) {
            LOG.error("CustomServiceApi.sendImage() is error : " + imageFile + " \n" + sendImageApiResult.getErrorMsg() + sendImageApiResult.toString());
            return false;
        }

        return true;
    }


    private static String customMessageUrl = "https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=";

    public static ApiResult sendMiniprogram(String openId,
                                            String title,
                                            String appid,
                                            String pagepath,
                                            File imageCover) {

        if (!imageCover.exists() || !imageCover.isFile() || !imageCover.canRead()) {
            return null;
        }

        /**
         * 上传临时素材
         * {"type":"TYPE","media_id":"MEDIA_ID","created_at":123456789}
         */
        ApiResult apiResult = MediaApi.uploadMedia(MediaApi.MediaType.IMAGE, imageCover);
        if (!apiResult.isSucceed()) {
            LOG.error("MediaApi.uploadMedia..." + imageCover + " \n" + apiResult.toString());
            return apiResult;
        }

        String mediaId = apiResult.get("media_id");


        Map<String, Object> jsonMap = new HashMap<String, Object>();
        jsonMap.put("touser", openId);
        jsonMap.put("msgtype", "miniprogrampage");


        Map<String, Object> miniprogrampageMap = new HashMap<String, Object>();
        miniprogrampageMap.put("title", title);
        miniprogrampageMap.put("appid", appid);
        miniprogrampageMap.put("pagepath", pagepath);
        miniprogrampageMap.put("thumb_media_id", mediaId);

        jsonMap.put("miniprogrampage", miniprogrampageMap);

        String accessToken = AccessTokenApi.getAccessTokenStr();
        String jsonResult = HttpUtils.post(customMessageUrl + accessToken, JsonUtils.toJson(jsonMap));

        return new ApiResult(jsonResult);
    }
}
