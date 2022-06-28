package io.jpress.commons.qcloud;

import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;
import com.tencentcloudapi.vod.v20180717.VodClient;
import com.tencentcloudapi.vod.v20180717.models.ProcessMediaByProcedureRequest;
import com.tencentcloudapi.vod.v20180717.models.ProcessMediaByProcedureResponse;
import io.jpress.JPressOptions;

public class QCloudVideoUtil {

    /**
     * 使用任务流模板进行视频处理
     * 任务流名称：LongVideoPreset
     * @param fileId
     * @return
     */
    public static String  setAdaptiveBitstream(String fileId) throws Exception {
        // 实例化一个认证对象，入参需要传入腾讯云账户secretId，secretKey,此处还需注意密钥对的保密
        // 密钥可前往https://console.cloud.tencent.com/cam/capi网站进行获取

        String secretId = JPressOptions.get("attachment_qcloudvideo_secretid");
        String secretKey = JPressOptions.get("attachment_qcloudvideo_secretkey");

        Credential cred = new Credential(secretId, secretKey);

        // 实例化一个http选项，可选的，没有特殊需求可以跳过
        HttpProfile httpProfile = new HttpProfile();
        httpProfile.setEndpoint("vod.tencentcloudapi.com");

        // 实例化一个client选项，可选的，没有特殊需求可以跳过
        ClientProfile clientProfile = new ClientProfile();
        clientProfile.setHttpProfile(httpProfile);

        // 实例化要请求产品的client对象,clientProfile是可选的
        VodClient client = new VodClient(cred, "", clientProfile);

        // 实例化一个请求对象,每个接口都会对应一个request对象
        ProcessMediaByProcedureRequest req = new ProcessMediaByProcedureRequest();
        req.setFileId(fileId);
        req.setProcedureName("LongVideoPreset");

        // 返回的resp是一个ProcessMediaByProcedureResponse的实例，与请求对象对应
        ProcessMediaByProcedureResponse resp = client.ProcessMediaByProcedure(req);

        // 输出json格式的字符串回包
        return ProcessMediaByProcedureResponse.toJsonString(resp);


    }

}
