package io.jpress.web.wechat;

import com.jfinal.weixin.sdk.api.ApiResult;
import io.jboot.utils.StrUtils;
import io.jpress.model.User;
import io.jpress.service.UserService;

import java.util.Date;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: (请输入文件名称)
 * @Description: (用一句话描述该文件做什么)
 * @Package io.jpress.web.wechat
 */
public class WechatKit {

    public static Long doGetOrCreateUser(ApiResult apiResult, UserService userService) {

        /**
         * 文档：https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
         * apiResult的数据格式如下
         * {
         "openId": "OPENID",
         "nickName": "NICKNAME",
         "gender": GENDER,
         "city": "CITY",
         "province": "PROVINCE",
         "country": "COUNTRY",
         "avatarUrl": "AVATARURL",
         "unionId": "UNIONID",
         "watermark": {
         "appid": "APPID",
         "timestamp": TIMESTAMP
         }
         }
         */

        String openId = apiResult.get("openId");
        String unionId = apiResult.get("unionId");


        User user = null;

        //优先根据 unioinId 进行查询
        if (StrUtils.isNotBlank(unionId)) {
            user = userService.findFistByWxUnionid(unionId);
            if (user != null) return user.getId();
        }

        //之后根据 openId 进行查询
        if (StrUtils.isNotBlank(openId)) {
            user = userService.findFistByWxOpenid(openId);
            if (user != null) return user.getId();
        }

        // 都查询不到，说明该用户是一个新的用户，创建一个新的用户
        String nickName = apiResult.get("nickName");
        String gender = apiResult.get("gender");
        String city = apiResult.get("city");
        String province = apiResult.get("province");
        String country = apiResult.get("country");
        String avatarUrl = apiResult.get("avatarUrl");

        user = new User();
        user.setNickname(nickName);
        user.setAddress(country + province + city);
        user.setWxUnionid(unionId);
        user.setWxOpenid(openId);
        user.setAvatar(avatarUrl);
        user.setCreated(new Date());
        user.setLogged(new Date());
        user.setCreateSource("wechat_miniprogram");
        user.setStatus(User.STATUS_OK);

        return userService.saveAndGetId(user);
    }
}
