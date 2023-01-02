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


import io.jboot.Jboot;

import java.io.Serializable;

public class AuthCodeKit implements Serializable {

    private static final String CACHE_NAME = "authCode";

    public static void save(AuthCode authCode) {
        //有效期24小时
        Jboot.getCache().put(CACHE_NAME, authCode.getId(), authCode, 60 * 60 * 24);

    }


    public static AuthCode get(String id) {
        return Jboot.getCache().get(CACHE_NAME, id);
    }
}
