package io.jpress.commons.aliyun;

import com.aliyun.vod20170321.models.GetVideoInfoResponse;
import com.aliyun.vod20170321.models.ListLiveRecordVideoResponse;

public class CloudVideoInfo {

    private String videoId;
    private String name;
    private Long size;
    private Double duration;
    private String status;

    public CloudVideoInfo() {
    }

    public CloudVideoInfo(GetVideoInfoResponse.GetVideoInfoResponseVideo video) {
        this.videoId = video.videoId;
        this.name = video.title;
        this.size = video.size;
        this.duration = video.duration;
        this.status = video.status;
    }

    public CloudVideoInfo(ListLiveRecordVideoResponse.ListLiveRecordVideoResponseLiveRecordVideoListLiveRecordVideoVideo video) {
        this.videoId = video.videoId;
        this.name = video.title;
        this.size = video.size;
        this.duration = video.duration;
        this.status = video.status;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 文档：https://help.aliyun.com/document_detail/52835.html
     * Uploading：上传中。
     * UploadFail：上传失败。
     * UploadSucc：上传完成。
     * Transcoding：转码中。
     * TranscodeFail：转码失败。
     * Blocked：屏蔽。
     * Normal：正常。
     *
     * @return
     */
    public boolean isNormal() {
        return "Normal".equalsIgnoreCase(status);
    }
}
