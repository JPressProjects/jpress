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
package io.jpress.web.commons.controller;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.JsonUtil;
import io.jboot.utils.StrUtil;
import io.jboot.utils.TypeDef;
import io.jboot.web.controller.annotation.RequestMapping;
import io.jpress.JPressOptions;
import io.jpress.commons.aliyun.AliyunVideoUtil;
import io.jpress.model.AttachmentVideo;
import io.jpress.service.AttachmentVideoService;

import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web
 */
@RequestMapping("/commons/video")
public class VideoController extends Controller {

    @Inject
    private AttachmentVideoService videoService;


    /**
     * 获取视频信息
     */
    public void detail() {
        String uuid = getPara("id");

        if (StrUtil.isBlank(uuid)) {
            renderJson(Ret.fail().set("message", "传入的视频uuid为空！"));
            return;
        }

        AttachmentVideo video = videoService.findByUuid(uuid);
        if (video == null) {
            renderJson(Ret.fail().set("message", "视频信息为空！"));
            return;
        }


        if (AttachmentVideo.CLOUD_TYPE_ALIYUN.equals(video.getCloudType())) {//阿里云

            //视频云端id
            if (StrUtil.isBlank(video.getVodVid())) {
                renderJson(Ret.fail().set("message", "阿里云 视频云端id为空！"));
                return;
            }
            //阿里云视频播放凭证
            String playAuth = AliyunVideoUtil.getPlayAuth(video.getVodVid());
            if (StrUtil.isBlank(playAuth)) {
                renderJson(Ret.fail().set("message", "阿里云视频播放凭证为空！"));
                return;
            }

            renderJson(Ret.ok().set("success", true).set("vid", video.getVodVid()).set("playAuth", playAuth).set("cloudType", video.getCloudType()));


        } else if (AttachmentVideo.CLOUD_TYPE_QCLOUD.equals(video.getCloudType())) {//腾讯云

            String appId = JPressOptions.get("attachment_qcloudvideo_appid");
            if (StrUtil.isBlank(appId)) {
                renderJson(Ret.fail().set("message", "请配置腾讯云的账号id"));
                return;
            }

            //视频云端id
            if (StrUtil.isBlank(video.getVodVid())) {
                renderJson(Ret.fail().set("message", "腾讯云 视频云端id为空！"));
                return;
            }

            renderJson(Ret.ok().set("success", true).set("vid", video.getVodVid()).set("aid", appId).set("cloudType", video.getCloudType()));

        } else if (AttachmentVideo.CLOUD_TYPE_LOCAL.equals(video.getCloudType())) {//本地视频

            String options = video.getOptions();
            if (StrUtil.isNotBlank(options)) {
                Map<String, String> map = JsonUtil.get(options, "", TypeDef.MAP_STRING);
                if (map == null) {
                    renderJson(Ret.fail().set("message", "该视频类型是本地视频，请先上传本地视频！"));
                    return;
                }
                String src = map.get("local_video_url");
                renderJson(Ret.ok().set("success", true).set("src", src).set("cloudType", video.getCloudType()));

            }

        }

    }

}
