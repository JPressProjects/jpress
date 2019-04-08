package io.jpress.addon.wechat.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * JS 工具类
 *
 * @author Eric.Huang
 * @date 2019-03-13 22:25
 * @package io.jpress.addon.wechat.util
 **/

public class JSUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JSUtil.class);

    private static String eval(String script) {
        ScriptEngineManager sem = new ScriptEngineManager();
        ScriptEngine se = sem.getEngineByName("javascript");
        String res = "";

        try {
            res = String.valueOf(se.eval(script));
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        return res;
    }

    public static String getPushServer(String host) {
        String pushServer = JSUtil.eval("  var e = '" + host + "',\n" +
                "            t = 'weixin.qq.com',\n" +
                "            o = 'file.wx.qq.com',\n" +
                "            n = 'webpush.weixin.qq.com';\n" +
                "        e.indexOf('wx2.qq.com') > -1 ? (t = 'weixin.qq.com', o = 'file2.wx.qq.com', n = 'webpush2.weixin.qq.com') : e.indexOf('qq.com') > -1 ? (t = 'weixin.qq.com', o = 'file.wx.qq.com', n = 'webpush.weixin.qq.com') : e.indexOf('web1.wechat.com') > -1 ? (t = 'wechat.com', o = 'file1.wechat.com', n = 'webpush1.wechat.com') : e.indexOf('web2.wechat.com') > -1 ? (t = 'wechat.com', o = 'file2.wechat.com', n = 'webpush2.wechat.com') : e.indexOf('wechat.com') > -1 ? (t = 'wechat.com', o = 'file.wechat.com', n = 'webpush.wechat.com') : e.indexOf('web1.wechatapp.com') > -1 ? (t = 'wechatapp.com', o = 'file1.wechatapp.com', n = 'webpush1.wechatapp.com') : (t = 'wechatapp.com', o = 'file.wechatapp.com', n = 'webpush.wechatapp.com');");


        return pushServer;
    }
}
