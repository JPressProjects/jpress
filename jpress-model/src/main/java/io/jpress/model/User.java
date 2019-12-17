/**
 * Copyright (c) 2016-2019, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.model;

import com.jfinal.core.JFinal;
import io.jboot.db.annotation.Table;
import io.jboot.utils.StrUtil;
import io.jpress.JPressOptions;
import io.jpress.commons.utils.CommonsUtils;
import io.jpress.model.base.BaseUser;

import java.util.HashMap;
import java.util.Map;


@Table(tableName = "user", primaryKey = "id")
public class User extends BaseUser<User> {

    public static final String SOURCE_WECHAT_WEB = "wechat_web";//来至微信网页授权
    public static final String SOURCE_WECHAT_MESSAGE = "wechat_message";//来至微信关注或者微信发送消息等
    public static final String SOURCE_WECHAT_MINIPROGRAM = "wechat_miniprogram";//来至微信小程序
    public static final String SOURCE_WEB_REGISTER = "web_register";//来至网页注册
    public static final String SOURCE_ADMIN_CREATE = "admin_create";//来至管理员的后台创建

    public static final Map<String, String> sourceMap = new HashMap<>();

    static {
        sourceMap.put(SOURCE_WECHAT_WEB, "微信网页授权");
        sourceMap.put(SOURCE_WECHAT_MESSAGE, "微信公众号");
        sourceMap.put(SOURCE_WECHAT_MINIPROGRAM, "微信小程序");
        sourceMap.put(SOURCE_WEB_REGISTER, "网页注册");
        sourceMap.put(SOURCE_ADMIN_CREATE, "后台创建");
    }

    public String getSourceString() {
        String text = sourceMap.get(getCreateSource());
        return StrUtil.isBlank(text) ? "" : text;
    }


    public static final String STATUS_LOCK = "locked";    // 锁定账号，无法做任何事情
    public static final String STATUS_REG = "registered";         // 注册、未激活
    public static final String STATUS_OK = "ok";          // 正常、已激活

    /**
     * 默认头像地址
     */
    private static final String DEFAULT_AVATAR = "/static/commons/img/avatar.png";

    public boolean isStatusOk() {
        return STATUS_OK.equals(getStatus());
    }

    public boolean isStatusReg() {
        return STATUS_REG.equals(getStatus());
    }

    public boolean isStatusLocked() {
        return STATUS_LOCK.equals(getStatus());
    }


    @Override
    public String getAvatar() {
        String avatar = super.getAvatar();
        if (avatar != null && avatar.toLowerCase().startsWith("http")) {
            return avatar;
        }

        return JFinal.me().getContextPath() +
                (StrUtil.isNotBlank(avatar) ? avatar : DEFAULT_AVATAR);
    }

    public String getOriginalAvatar() {
        return super.getAvatar();
    }

    public User keepSafe() {

        //在读取用户资料进行输出的时候
        //调用下此方法，再用于渲染json数据给客户端
        remove("password", "salt");

        return this;
    }

    public void keepUpdateSafe() {

        //用户在更新自己的资料的时候，先调用此方法
        //以下这些字段不允许用户自己更新

        remove("password", "salt",
                "username", "wx_openid",
                "wx_unionid", "qq_openid",
                "email_status", "mobile_status",
                "status", "created",
                "create_source", "logged",
                "activated");

    }


    public boolean isEmailStatusOk() {
        return STATUS_OK.equals(getEmailStatus());
    }


    public boolean isMobileStatusOk() {
        return STATUS_OK.equals(getMobileStatus());
    }

    public String getDetailUrl() {
        return JFinal.me().getContextPath() + "/admin/user/detail/" + getId();
    }

    @Override
    public boolean save() {
        CommonsUtils.escapeModel(this, "salt", "password");
        return super.save();
    }

    @Override
    public boolean update() {
        CommonsUtils.escapeModel(this, "salt", "password");
        return super.update();
    }

    public User getUser() {
        return get("user");
    }

    public String getUrl() {
        return JFinal.me().getContextPath() + "/user/" + getId() + JPressOptions.getAppUrlSuffix();
    }

}
