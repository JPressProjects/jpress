package io.jpress.commons.aliyun;

import com.aliyun.tearpc.models.Config;
import com.aliyun.vod20170321.models.*;
import io.jpress.JPressOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AliyunVideoUtil {




    /**
     * 获取阿里云的视频上传凭证
     *
     * @param fileName 文件名，必须带有文件后缀，支持的后缀如下（不区分大消息）
     *                 MPEG格式：MP4、TS、3GP、MPG、MPEG、MPE、DAT、VOB、ASF等。
     *                 AVI格式：AVI。
     *                 Windows Media Video格式：WMV、ASF。
     *                 Flash Video格式：FLV、F4V。
     *                 Real Video格式：RM、RMVB。
     *                 QuickTime格式：MOV。
     *                 Matroska格式：MKV。
     *                 HLS格式：M3U8。
     *                 其它格式：DV、GIF、M2T、M4V、MJ2、MJPEG、MTS、OGG、QT、SWF、WEBM。
     * @param title    文件标题
     * @return --
     * {
     * "RequestId" : "25818875-5F78-4AF6-04D5-D7393642****",
     * "UploadAddress" : "eyJTZWN1cml0a2VuIjoiQ0FJU3p3TjF****",
     * "VideoId" : "93ab850b4f6f54b6e91d24d81d44****",
     * "UploadAuth" : "eyJFbmRw*****b2ludCI6Im"
     * }
     */
    public static Map<String, Object> getUploadVideoAuth(String fileName, String title) {
        CreateUploadVideoRequest createUploadVideoRequest = new CreateUploadVideoRequest();

        createUploadVideoRequest.fileName = fileName;
        createUploadVideoRequest.title = title;
        try {
            if(createClient() != null){
                CreateUploadVideoResponse response = createClient().createUploadVideo(createUploadVideoRequest);
                return response != null ? response.toMap() : null;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 刷新上传视频凭证
     *
     * @param videoId 视频ID
     * @return --
     * {
     * "RequestId" : "25818875-5F78-4A*****F6-D7393642CA58",
     * "UploadAuth" : "FJU3p3T*****Z0NUIyeW",
     * "UploadAddress" : "eyJTZWN1cml0eVR*****iQ0FJU3p3TjFxNkZ0NUIyeW",
     * "VideoId" : "c6a23a870c8c4ffc*****d40cbd38133"
     * }
     */
    public static Map<String, Object> refreshUploadVideoAuth(String videoId) {
        RefreshUploadVideoRequest refreshUploadVideoRequest = new RefreshUploadVideoRequest();
        refreshUploadVideoRequest.videoId = videoId;
        try {
            if(createClient() != null){
                RefreshUploadVideoResponse response = createClient().refreshUploadVideo(refreshUploadVideoRequest);
                return response != null ? response.toMap() : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取阿里云的播放凭证
     *
     * @param videoId
     * @return
     * @throws Exception
     */
    public static String getPlayAuth(String videoId) {
        GetVideoPlayAuthRequest getVideoPlayAuthRequest = new GetVideoPlayAuthRequest();
        getVideoPlayAuthRequest.videoId = videoId;
        try {
            if(createClient() != null){
                GetVideoPlayAuthResponse response = createClient().getVideoPlayAuth(getVideoPlayAuthRequest);
                return response != null ? response.playAuth : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取视频信息
     *
     * @param videoId
     * @return
     */
    public static CloudVideoInfo getVideoInfo(String videoId) {
        GetVideoInfoRequest getVideoInfoRequest = new GetVideoInfoRequest();
        getVideoInfoRequest.videoId = videoId;
        try {
            if(createClient() != null){
                GetVideoInfoResponse response = createClient().getVideoInfo(getVideoInfoRequest);
                return response != null ? new CloudVideoInfo(response.video) : null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取直播生成的点播视频信息
     *
     * @param appName
     * @param streamName
     * @return
     */
    public static List<CloudVideoInfo> getLiveVideoInfos(String appName, String streamName) {
        ListLiveRecordVideoRequest listLiveRecordVideoRequest = new ListLiveRecordVideoRequest();
        listLiveRecordVideoRequest.appName = appName;
        listLiveRecordVideoRequest.streamName = streamName;
        try {
            if(createClient() != null){
                ListLiveRecordVideoResponse listLiveRecordVideoResponse = createClient().listLiveRecordVideo(listLiveRecordVideoRequest);
                if (listLiveRecordVideoResponse != null && listLiveRecordVideoResponse.liveRecordVideoList != null) {
                    List<ListLiveRecordVideoResponse.ListLiveRecordVideoResponseLiveRecordVideoListLiveRecordVideo>
                            liveRecordVideo = listLiveRecordVideoResponse.liveRecordVideoList.liveRecordVideo;
                    if (liveRecordVideo != null) {
                        List<CloudVideoInfo> cloudVideoInfos = new ArrayList<>();
                        liveRecordVideo.forEach(item -> cloudVideoInfos.add(new CloudVideoInfo(item.video)));
                        return cloudVideoInfos;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private static com.aliyun.vod20170321.Client createClient() throws Exception {
        String accessKeyId = JPressOptions.get("attachment_aliyunvideo_accesskeyid","");
        String accessKeySecret = JPressOptions.get("attachment_aliyunvideo_accesskeysecret","");
        String endpoint = JPressOptions.get("attachment_aliyunvideo_endpoint","vod.cn-shanghai.aliyuncs.com");
//        String endpoint = Jboot.configValue("jboot.aliyun.vod.endpoint", "vod.cn-shanghai.aliyuncs.com");

        if(accessKeyId == null || accessKeySecret == null){
            return null;
        }

        Config config = new Config();
        config.accessKeyId = accessKeyId;
        config.accessKeySecret = accessKeySecret;
        config.endpoint = endpoint;
        return new com.aliyun.vod20170321.Client(config);
    }
}
