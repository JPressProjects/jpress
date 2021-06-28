package io.jpress.web.api;


import com.jfinal.aop.Before;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Ret;
import com.jfinal.weixin.sdk.api.ApiConfigKit;
import com.jfinal.weixin.sdk.api.ApiResult;
import com.jfinal.weixin.sdk.cache.IAccessTokenCache;
import com.jfinal.wxaapp.api.WxaUserApi;
import io.jboot.apidoc.ContentType;
import io.jboot.apidoc.annotation.ApiOper;
import io.jboot.support.jwt.EnableJwt;
import io.jboot.utils.StrUtil;
import io.jboot.web.json.JsonBody;
import io.jpress.web.base.ApiControllerBase;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

@Before(WechatMiniProgramApiInterceptor.class)
@EnableJwt
public abstract class BaseWechatDecryptController extends ApiControllerBase {


    /**
     * 1、小程序端调用 wx.login() 之后，会得到 code
     * <p>
     * onLoad:function(){
     * wx.login({
     * success(res) {
     * if (res.code) {
     * sdk.code2session(res.code);
     * }
     * }
     * })
     * },
     * <p>
     * <p>
     * 详情：https://developers.weixin.qq.com/miniprogram/dev/api/open-api/login/wx.login.html
     * <p>
     * 2、小程序端得到 code 之后，需调用此接口，通过 code 来换 session_key，同时服务器存储 session_key
     * 3、为什么要存储session_key呢？目的是为了获取用户信息
     * 4、小程序调用 wx.login() 之后，接下来会调用 wx.getUserInfo() ，
     * 此时要注意：wx.getUserInfo()得到的信息是加密的，需要解密才能获取文本信息
     * <p>
     * 5、解密就用到刚才的 session_key 了，session_key 其实就是解密的钥匙 （密钥）
     */
    @ApiOper(value = "微信小程序登录")
    public void login(@NotEmpty String code) {

        // 获取SessionKey 和 openId
        // 返回{"session_key":"nzoqhc3OnwHzeTxJs+inbQ==","expires_in":2592000,"openid":"oVBkZ0aYgDMDIywRdgPW8-joxXc4"}
        ApiResult apiResult = WxaUserApi.getSessionKey(code);
        if (!apiResult.isSucceed()) {
            renderFailJson(apiResult.getErrorCode(), apiResult.getErrorMsg());
            return;
        }

        String sessionKey = apiResult.getStr("session_key");
        String sessionId = HashKit.md5(apiResult.getStr("openid"));

        //把sessionKey存储起来，接下来用户解密要用到这个sessionKey
        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        accessTokenCache.set("wxa:session:" + sessionId, sessionKey);


        onLoginSuccess(apiResult);
    }

    /**
     * 小程序端调用 wx.getUserInfo() 后，得到的是加密的用户数据
     * 需要调用此接口，才能获取到具体的数据
     * 解密用户数据，小程序的相关接口 https://developers.weixin.qq.com/miniprogram/dev/api/open-api/user-info/wx.getUserInfo.html
     */
    @ApiOper(value = "微信小程序注册当前用户", notes = "一般只有登录不成功后进行注册", contentType = ContentType.JSON)
    public void register(@JsonBody @NotEmpty Map<String, String> json) {

        //小程序端调用 login() 之后得到的 sessionId
        String sessionId = HashKit.md5(getCurrentWechatOpenId());

        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String sessionKey = accessTokenCache.get("wxa:session:" + sessionId);

        //缓存不存在，一般这种情况是缓存过期了  或者 使用内存缓存，同时服务器重启了，需要客户端重新登录
        if (StrUtil.isBlank(sessionKey)) {
            renderJson(Ret.ok().set("code", JWT_ERROR_CODE));
            return;
        }

        //不包括敏感信息的原始数据字符串，用于计算签名
        String rawData = json.get("rawData");

        //签名：使用 sha1( rawData + sessionkey ) 得到字符串，用于校验用户信息
        String signature = json.get("signature");

        //包括敏感数据在内的完整用户信息的加密数据
        //具体加密方法在：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
        String encryptedData = json.get("encryptedData");


        boolean checkSuccess = WxaUserApi.checkUserInfo(sessionKey, rawData, signature);
        if (!checkSuccess) {
            renderFailJson(500, "userInfo check fail");
            return;
        }

        //加密算法的初始向量
        String iv = json.get("iv");

        // 服务端解密用户信息，得到原始的用户信息
        ApiResult apiResult = WxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
        if (!apiResult.isSucceed()) {
            renderFailJson(apiResult.getErrorCode(), apiResult.getErrorMsg());
            return;
        }

        onGetUserInfo(apiResult);
    }


    @ApiOper(value = "获取当前用户手机号", contentType = ContentType.JSON)
    public void phoneNumber(@JsonBody @NotEmpty Map<String, String> json) {

        //小程序端调用 code2session() 之后得到的 sessionId
        String sessionId = HashKit.md5(getCurrentWechatOpenId());

        IAccessTokenCache accessTokenCache = ApiConfigKit.getAccessTokenCache();
        String sessionKey = accessTokenCache.get("wxa:session:" + sessionId);

        //缓存不存在，一般这种情况是缓存过期了  或者 使用内存缓存，同时服务器重启了，需要客户端重新登录
        if (StrUtil.isBlank(sessionKey)) {
            renderJson(Ret.ok().set("code", JWT_ERROR_CODE));
            return;
        }


        //包括敏感数据在内的完整用户信息的加密数据
        //具体加密方法在：https://developers.weixin.qq.com/miniprogram/dev/framework/open-ability/signature.html#%E5%8A%A0%E5%AF%86%E6%95%B0%E6%8D%AE%E8%A7%A3%E5%AF%86%E7%AE%97%E6%B3%95
        String encryptedData = json.get("encryptedData");

        //加密算法的初始向量
        String iv = json.get("iv");

        // 服务端解密用户信息，得到原始的用户信息
        ApiResult apiResult = WxaUserApi.getUserInfo(sessionKey, encryptedData, iv);
        if (!apiResult.isSucceed()) {
            renderFailJson(apiResult.getErrorCode(), apiResult.getErrorMsg());
            return;
        }

        onGetPhoneNumber(apiResult);
    }

    protected abstract void onLoginSuccess(ApiResult apiResult);

    protected abstract void onGetUserInfo(ApiResult apiResult);

    protected abstract void onGetPhoneNumber(ApiResult apiResult);


}
