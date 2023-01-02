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
package io.jpress.commons.authcode;


import io.jboot.utils.StrUtil;
import io.jpress.commons.utils.CommonsUtils;

import java.io.Serializable;

public class AuthCode implements Serializable {

    private String id;
    private String code;
    private long userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public static AuthCode newCode(long userId) {
        AuthCode authCode = new AuthCode();

        authCode.setId(StrUtil.uuid());
        authCode.setCode(CommonsUtils.generateCode());
        authCode.setUserId(userId);

        return authCode;
    }

    public static AuthCode newCode() {
        AuthCode authCode = new AuthCode();
        authCode.setId(StrUtil.uuid());
        authCode.setCode(CommonsUtils.generateCode());
        return authCode;
    }


    public static void save(AuthCode authCode){

    }
}
