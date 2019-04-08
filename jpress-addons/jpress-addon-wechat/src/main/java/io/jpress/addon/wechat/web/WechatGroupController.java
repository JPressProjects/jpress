package io.jpress.addon.wechat.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.google.zxing.*;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import io.jboot.components.http.JbootHttpRequest;
import io.jboot.components.http.JbootHttpResponse;
import io.jboot.utils.CookieUtil;
import io.jboot.utils.HttpUtil;
import io.jboot.utils.RequestUtil;
import io.jboot.utils.StrUtil;
import io.jpress.addon.wechat.util.BufferedImageLuminanceSource;
import io.jpress.addon.wechat.util.JSUtil;
import io.jpress.addon.wechat.util.MatcherUtil;
import io.jpress.addon.wechat.vo.TuLingVO;
import io.jpress.web.base.AdminControllerBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.Instant;
import java.util.*;

/**
 * 微信群组，消息等后台管理
 *
 * @author Eric.Huang
 * @date 2019-03-14 09:38
 * @package io.jpress.addon.wechat
 **/

public class WechatGroupController extends AdminControllerBase {

    private static final Logger logger = LoggerFactory.getLogger(WechatGroupController.class);

    /** 获取UUID */
    private static final String UUID_URL = "https://login.wx.qq.com/jslogin";
    /** 显示二维码 */
    private static final String SHOW_QRCODE_IMAGE_URL = "https://login.wx.qq.com/qrcode/{uuid}?t=webwx";

    /** 轮询登录 */
    private static final String LOGIN_URL = "https://login.wx.qq.com/cgi-bin/mmwebwx-bin/login";

    private String base_uri, redirect_uri;
    private String webpush_url = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";
    private String skey, synckey, wxsid, wxuin, pass_ticket, deviceId;
    private JSONObject SyncKey, User, BaseRequest;

    /** 微信联系人列表 **/
    private JSONArray MemberList;
    /** 可聊天的联系人列表 **/
    public JSONArray ContactList;

    /** 发送给指定的群 **/
    public String sendToGroup;
    /** 是否登录 */
    private boolean isLogin;
    private String uuid;
    private String cookie;

    /** 微信特殊账号 **/
    private List<String> SpecialUsers = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail",
            "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote",
            "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip",
            "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11",
            "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c",
            "wxitil", "userexperience_alarm", "notification_messages");

    public String getUUID() {

        logger.info("获取二维码UUID");

        Map requestParam = new HashMap<String,Object>();
        requestParam.put("appid","wx782c26e4c19acffb");
        requestParam.put("fun","new");
        requestParam.put("lang","zh_CN");

        String result = HttpUtil.httpGet(UUID_URL, requestParam);
        logger.info("[获取UUID返回结果]  " + result);

        if(!StrUtil.isBlank(result)){
            String code = MatcherUtil.match("window.QRLogin.code = (\\d+);", result);
            if(null != code && "200".equals(code)){
                this.uuid = MatcherUtil.match("window.QRLogin.uuid = \"(.*)\";", result);
                return this.uuid;
            } else {
                logger.info("[*] 获取UUID错误，错误码: %s", code);
            }
        }
        return null;
    }

    public void showQrImage(String uuid) {
        String uid  = null != uuid ? uuid : this.uuid;
        String url = "https://login.weixin.qq.com/qrcode/" + uid + "?t=webwx";
        final File qrImage = new File("temp_qrcode.png");

        // 下载二维码
        HttpUtil.download(url, qrImage);

        // 控制台显示二维码
        Map<EncodeHintType, Object> hintMap = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hintMap.put(EncodeHintType.MARGIN, 1);
        hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);

        String  qrContent  = readQRCode(qrImage, hintMap);
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(qrContent, BarcodeFormat.QR_CODE, 10, 10, hintMap);
            System.out.println(toAscii(bitMatrix));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 轮询登录
     * 尝试登录。若此时用户手机已完成扫码并点击登录，则返回一个真正用于登录的url地址。否则接口大概10s后返回未扫码或未登录的状态码
     * @author
     * @date  2019-03-13 23:22
     */
    public String loginState() {

        Map<String, Object> requestParam = Maps.newHashMap();
        requestParam.put("tip", "1");
        requestParam.put("uuid", this.uuid);
        String res = HttpUtil.httpGet(LOGIN_URL, requestParam);

        if (null == res) {
            logger.info("[*] 扫描二维码验证失败");
            return "";
        }

        String code = MatcherUtil.match("window.code=(\\d+);", res);
        if (null == code) {
            logger.info("[*] 扫描二维码验证失败");
            return "";
        } else {
            if (code.equals("201")) {
                logger.info("[*] 成功扫描,请在手机上点击确认以登录");
            } else if(code.equals("200")) {
                logger.info("[*] 正在登录...");
                String pm = MatcherUtil.match("window.redirect_uri=\"(\\S+?)\";", res);
                String redirectHost = "wx.qq.com";

                try {
                    URL pmURL = new URL(pm);
                    redirectHost = pmURL.getHost();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                String pushServer = JSUtil.getPushServer(redirectHost);
                webpush_url = "https://" + pushServer + "/cgi-bin/mmwebwx-bin";
                this.redirect_uri = pm + "&fun=new";
                logger.info("[*] redirect_uri=%s", this.redirect_uri);
                this.base_uri = this.redirect_uri.substring(0, this.redirect_uri.lastIndexOf("/"));
                logger.info("[*] base_uri=%s", this.base_uri);
            } else if (code.equals("408")) {
                logger.info("[*] 登录超时");
            } else {
                logger.info("[*] 扫描code=%s", code);
            }
        }
        return code;
    }

    public boolean login() {

        if(isLogin){
            logger.info("已经登录");
            return true;
        }
        this.isLogin = true;

        String res = HttpUtil.httpGet(this.redirect_uri);
        // TODO cookie未处理
        this.cookie = getCookie(getRequest());

        if (StrUtil.isBlank(res)) {
            return false;
        }

        this.skey = MatcherUtil.match("<skey>(\\S+)</skey>", res);
        this.wxsid = MatcherUtil.match("<wxsid>(\\S+)</wxsid>", res);
        this.wxuin = MatcherUtil.match("<wxuin>(\\S+)</wxuin>", res);
        this.pass_ticket = MatcherUtil.match("<pass_ticket>(\\S+)</pass_ticket>", res);

        this.BaseRequest = new JSONObject();
        BaseRequest.put("Uin", this.wxuin);
        BaseRequest.put("Sid", this.wxsid);
        BaseRequest.put("Skey", this.skey);
        BaseRequest.put("DeviceID", this.deviceId);

        return true;
    }

    /**
     * 微信初始化
     * 初始化微信首页栏的联系人、公众号等（不是通讯录里的联系人），初始化登录者自己的信息（包括昵称等），初始化同步消息所用的SycnKey
     * @author Eric Huang 黄鑫（ninemm@126.com）
     * @date  2019-03-13 23:24
     */
    public boolean initWechat() {

        String url = this.base_uri + "/webwxinit?r=" + getCurrentUnixTime() + "&pass_ticket=" + this.pass_ticket + "&skey=" + this.skey;

        JSONObject body = new JSONObject();
        body.put("BaseRequest", this.BaseRequest);

        Map<String, String> headers = getRequestHeaders();
        String res = HttpUtil.httpPost(url, null, headers, body.toString());

        if(StrUtil.isBlank(res)){
            return false;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(res);
            if(null != jsonObject){
                JSONObject baseResponse = jsonObject.getJSONObject("BaseResponse");
                if (null != baseResponse) {
                    Integer _ret = baseResponse.getIntValue("Ret");
                    int ret = _ret == null ? -1 : _ret.intValue();
                    if (ret == 0) {

                        this.SyncKey = jsonObject.getJSONObject("SyncKey");
                        this.User = jsonObject.getJSONObject("User");
                        StringBuffer synckey = new StringBuffer();
                        JSONArray list = SyncKey.getJSONArray("List");

                        for(int i=0, len=list.size(); i<len; i++){
                            JSONObject item = list.getJSONObject(i);
                            Integer _key = item.getInteger("Key");
                            Integer _value = item.getInteger("Val");
                            int key = _key == null ? 0 : _key;
                            int value = _value == null ? 0 : _value;

                            synckey.append("|" + key + "_" + value);
                        }
                        this.synckey = synckey.substring(1);
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * 发送文本消息（包括表情），不能发送图片或文件。
     * @author Eric Huang 黄鑫（ninemm@126.com）
     * @date  2019-03-13 23:30
     */
    public void sendMsg(String content, String to) {

        String url = this.base_uri + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + this.pass_ticket;

        JSONObject body = new JSONObject();

        String clientMsgId = getCurrentUnixTime() + getRandomNumber(5);
        JSONObject Msg = new JSONObject();
        Msg.put("Type", 1);
        Msg.put("Content", content);
        Msg.put("FromUserName", User.getString("UserName"));
        Msg.put("ToUserName", to);
        Msg.put("LocalID", clientMsgId);
        Msg.put("ClientMsgId", clientMsgId);

        body.put("BaseRequest", this.BaseRequest);
        body.put("Msg", Msg);

        Map<String, String> headers = getRequestHeaders();
        String res = HttpUtil.httpPost(url, null, headers, body.toString());
        /*HttpRequest request = HttpRequest.post(url)
                .header("Content-Type", "application/json;charset=utf-8")
                .header("Cookie", this.cookie)
                .send(body.toString());*/

//        logger.info("[*] " + request);
        /*request.body();
        request.disconnect();*/
    }

    public boolean wxStatusNotify() {

        String url = this.base_uri + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + this.pass_ticket;

        JSONObject body = new JSONObject();
        body.put("Code", 3);
        body.put("BaseRequest", this.BaseRequest);
        body.put("FromUserName", this.User.getString("UserName"));
        body.put("ToUserName", this.User.getString("UserName"));
        body.put("ClientMsgId", getCurrentUnixTime());

        Map<String, String> headers = getRequestHeaders();
        String res = HttpUtil.httpPost(url, null, headers, body.toString());

        if (StrUtil.isBlank(res)) {
            return false;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(res);
            JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
            if (null != BaseResponse) {
                Integer _ret = BaseResponse.getInteger("Ret");
                int ret = _ret == null ? -1 : _ret.intValue();
                return ret == 0;
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return false;
    }

    /**
     * 获取联系人列表
     * 获取手机通讯录中的所有联系人（包括人、群、公众号等）
     * @author Eric Huang 黄鑫 （ninemm@126.com）
     * @date  2019-03-13 23:25
     */
    public boolean loadContact() {
        String url = this.base_uri + "/webwxgetcontact?pass_ticket=" + this.pass_ticket + "&skey=" + this.skey + "&r=" + getCurrentUnixTime();

        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);

        Map<String, String> headers = getRequestHeaders();
        String res = HttpUtil.httpPost(url, null, headers, body.toString());

        if(StrUtil.isBlank(res)){
            return false;
        }

        try {
            JSONObject jsonObject = JSON.parseObject(res);
            JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
            if (null != BaseResponse) {
                Integer _ret = BaseResponse.getInteger("Ret");
                int ret = _ret == null ? -1 : _ret.intValue();
                if (ret == 0) {
                    this.MemberList = jsonObject.getJSONArray("MemberList");
                    this.ContactList = new JSONArray();
                    if(null != MemberList){
                        for(int i=0, len=MemberList.size(); i<len; i++){
                            JSONObject contact = this.MemberList.getJSONObject(i);
                            //公众号/服务号
                            if(contact.getIntValue("VerifyFlag") == 8){
                                continue;
                            }
                            //特殊联系人
                            if(SpecialUsers.contains(contact.getString("UserName"))){
                                continue;
                            }
                            //群聊
                            if(contact.getString("UserName").indexOf("@@") != -1){
                                continue;
                            }
                            //自己
                            if(contact.getString("UserName").equals(this.User.getString("UserName"))){
                                continue;
                            }
                            ContactList.add(contact);
                        }
                        return true;
                    }
                }
            }
        } catch (Exception e) {
        }
        return false;
    }

    public void syncMsg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                logger.info("[*] 进入消息监听模式 ...");
                while(true){

                    int[] arr = syncCheck();
                    logger.info("[*] retcode=%s,selector=%s", arr[0], arr[1]);

                    if(arr[0] == 0){
                        if(arr[1] == 2){
                            JSONObject data = webwxsync();
                            handleMsg(data);
                        } else if(arr[1] == 6){
                            JSONObject data = webwxsync();
                            handleMsg(data);
                        } else if(arr[1] == 3){
                        } else if(arr[1] == 0){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }, "listenMsgMode").start();
    }

    /**
     * 读取二维码信息
     *
     * @param filePath 文件路径
     * @param hintMap  hintMap
     * @return 二维码内容
     */
    private static String readQRCode(File filePath, Map hintMap) {
        try {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(
                    new BufferedImageLuminanceSource(ImageIO.read(new FileInputStream(filePath)))));
            Result qrCodeResult = new MultiFormatReader().decode(binaryBitmap, hintMap);
            return qrCodeResult.getText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将二维码输出为 ASCII
     *
     * @param bitMatrix
     * @return
     */
    private static String toAscii(BitMatrix bitMatrix) {
        StringBuilder sb = new StringBuilder();
        for (int rows = 0; rows < bitMatrix.getHeight(); rows++) {
            for (int cols = 0; cols < bitMatrix.getWidth(); cols++) {
                boolean x = bitMatrix.get(rows, cols);
                if (!x) {
                    // white
                    sb.append("\033[47m  \033[0m");
                } else {
                    sb.append("\033[30m  \033[0;39m");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * 同步消息检查
     * 这里只做检查不做同步，如果检查出有新消息，再掉具体同步的接口
     */
    public int[] syncCheck(){

        int[] arr = new int[2];
        String url = this.webpush_url + "/synccheck";
        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);

        Map<String, Object> params = Maps.newHashMap();
        params.put("r", getCurrentUnixTime() + getRandomNumber(5));
        params.put("skey", this.skey);
        params.put("uin", this.wxuin);
        params.put("sid", this.wxsid);
        params.put("deviceid", this.deviceId);
        params.put("synckey", this.synckey);
        params.put("_", System.currentTimeMillis());

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Cookie", this.cookie);
        String res = HttpUtil.httpGet(url, params, headers);

        /*HttpRequest request = HttpRequest.get(url, true,
                "r", getCurrentUnixTime() + StringKit.getRandomNumber(5),
                "skey", this.skey,
                "uin", this.wxuin,
                "sid", this.wxsid,
                "deviceid", this.deviceId,
                "synckey", this.synckey,
                "_", System.currentTimeMillis())
                .header("Cookie", this.cookie);

        logger.info("[*] " + request);
        String res = null;
        try {
            res = request.body();
        } catch (HttpRequestException e) {
            e.printStackTrace();
        }
        request.disconnect();*/

        if(StrUtil.isBlank(res)){
            return arr;
        }

        String retcode = MatcherUtil.match("retcode:\"(\\d+)\",", res);
        String selector = MatcherUtil.match("selector:\"(\\d+)\"}", res);
        if(null != retcode && null != selector){
            arr[0] = Integer.parseInt(retcode);
            arr[1] = Integer.parseInt(selector);
            return arr;
        }
        return arr;
    }


    /**
     * 获取最新消息
     * 当同步检查接口显示有新消息时，调用该接口获取具体的新消息。此处的新消息为广义的，包括消息，修改群名，群内成员变化，加好友等
     */
    public JSONObject webwxsync(){

        String url = this.base_uri + "/webwxsync?lang=zh_CN&pass_ticket=" + this.pass_ticket
                + "&skey=" + this.skey + "&sid=" + this.wxsid + "&r=" + getCurrentUnixTime();

        JSONObject body = new JSONObject();
        body.put("BaseRequest", BaseRequest);
        body.put("SyncKey", this.SyncKey);
        body.put("rr", getCurrentUnixTime());


        JbootHttpRequest request = JbootHttpRequest.create(url,"POST");
        request.setPostContent(body.toString());
        Map<String, String> headers = getRequestHeaders();
        request.addHeaders(headers);

        request.setConnectTimeOut(30000);
        JbootHttpResponse response = HttpUtil.handle(request);
        String result = response.getContent();

        if(StrUtil.isBlank(result)){
            return null;
        }

        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
        if(null != BaseResponse){
            int ret = BaseResponse.getIntValue("Ret");
            if (ret == 0) {
                this.SyncKey = jsonObject.getJSONObject("SyncKey");
                StringBuffer synckey = new StringBuffer();
                JSONArray list = SyncKey.getJSONArray("List");

                for(int i=0, len=list.size(); i<len; i++){
                    JSONObject item = list.getJSONObject(i);
                    Integer _key = item.getInteger("Key");
                    Integer _value = item.getInteger("Val");
                    int key = _key == null ? 0 : _key;
                    int value = _value == null ? 0 : _value;

                    synckey.append("|" + key + "_" + value);
                }
                this.synckey = synckey.substring(1);
            }
        }
        return jsonObject;
    }

    /**
     * 处理最新消息
     */
    public void handleMsg(JSONObject data){
        if (null == data) {
            return;
        }

        JSONArray AddMsgList = data.getJSONArray("AddMsgList");

        for (int i = 0, len = AddMsgList.size(); i < len; i++) {
            JSONObject msg = AddMsgList.getJSONObject(i);
            int msgType = msg.getIntValue("MsgType");
            String name = getUserRemarkName(msg.getString("FromUserName"));
            String content = msg.getString("Content");

            if (msgType == 51) {
                logger.info("[*] 成功截获微信初始化消息");
            } else if (msgType == 1) {
                if (SpecialUsers.contains(msg.getString("ToUserName"))) {
                    continue;
                } else if (msg.getString("FromUserName").equals(User.getString("UserName"))) {
                    continue;
                } else if (msg.getString("ToUserName").indexOf("@@") != -1) {
                    String[] peopleContent = content.split(":<br/>");
                    logger.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
                } else {
                    logger.info(name + ": " + content + ": " );
                    String[] peopleContent = content.split(":<br/>");
                    logger.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
                    logger.info("发送者：" + msg.getString("FromUserName"));

                    if (name.equals("wistbean和他的朋友们")) {
                        if (this.sendToGroup == null) {
                            this.sendToGroup = msg.getString("FromUserName");
                        }
                    }

                    if (content.contains("wistbean的小三")){

                        String sendMsg ;
                        if(peopleContent.length==2){
                            sendMsg = getMsg(peopleContent[1].replace("@wistbean的小三", ""));
                        }else{
                            sendMsg = "别随便@我，我比较娇贵~";
                        }

                        sendMsg(sendMsg , msg.getString("FromUserName"));
                    }
                }
            } else if(msgType == 3){
                //收到图片信息
            } else if(msgType == 34){
                //收到语音信息
            } else if(msgType == 42){
                //名片信息
            }
        }
    }

    /**
     * 图灵机器人获取消息回复
     * @param content
     * @return
     */
    private String getMsg(String content) {

        Map<String, Object> params = Maps.newHashMap();
        params.put("key","80a7ba9246814892ad0836b6561be745");
        params.put("info",content);
        String res = HttpUtil.httpPost("http://www.tuling123.com/openapi/api", params);

        TuLingVO tuLingVO = JSON.parseObject(res, TuLingVO.class);
        return tuLingVO.getText();

    }

    private String getUserRemarkName(String id) {
        String name = "这个人物名字未知";
        for(int i=0, len=MemberList.size(); i<len; i++){
            JSONObject member = this.MemberList.getJSONObject(i);
            if(member.getString("UserName").equals(id)){
                if(StrUtil.isNotBlank(member.getString("RemarkName"))){
                    name = member.getString("RemarkName");
                } else {
                    name = member.getString("NickName");
                }
                return name;
            }
        }
        return name;
    }

    private long getCurrentUnixTime() {
        return Instant.now().getEpochSecond();
    }

    private String getRandomNumber(int size) {
        String num = "";

        for(int i = 0; i < size; ++i) {
            double a = Math.random() * 9.0D;
            a = Math.ceil(a);
            int randomNum = (new Double(a)).intValue();
            num = num + randomNum;
        }

        return num;
    }

    private Map<String, String> getRequestHeaders() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", JbootHttpRequest.CONTENT_TYPE_JSON);
        headers.put("Cookie", this.cookie);
        return headers;
    }

    public static String getCookie(HttpServletRequest request) {



//        Enumeration headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String key = (String) headerNames.nextElement();
//            String value = request.getHeader(key);
//            // map.put(key, value);
//        }

//        HttpURLConnection conn = request.getConnection();
//        Map<String, List<String>> resHeaders = conn.getHeaderFields();
        StringBuffer sBuffer = new StringBuffer();
        Map<String, List<String>> resHeaders = Maps.newHashMap();
        for (Map.Entry<String, List<String>> entry : resHeaders.entrySet()) {
            String name = entry.getKey();
            if (name == null) {
                continue; // http/1.1 line
            }
            List<String> values = entry.getValue();
            if (name.equalsIgnoreCase("Set-Cookie")) {
                for (String value : values) {
                    if (value == null) {
                        continue;
                    }
                    String cookie = value.substring(0, value.indexOf(";") + 1);
                    sBuffer.append(cookie);
                }
            }
        }
        if(sBuffer.length() > 0){
            return sBuffer.toString();
        }
        return sBuffer.toString();
    }
}
