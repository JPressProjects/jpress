/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.captcha;

import com.jfinal.captcha.Captcha;
import com.jfinal.captcha.ICaptchaCache;
import io.jboot.Jboot;
import io.jboot.components.cache.JbootCache;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.captcha
 */
public class JPressCaptchaCache implements ICaptchaCache {

    //验证码缓存的名称
    private static final String CAPTCHA_CACHE_NAME = "captchaCache";

    private JbootCache cache;


    public JPressCaptchaCache() {
        cache = Jboot.getCache();
    }

    @Override
    public void put(Captcha captcha) {
        cache.put(CAPTCHA_CACHE_NAME, captcha.getKey(), captcha);
    }

    @Override
    public Captcha get(String key) {
        return cache.get(CAPTCHA_CACHE_NAME, key);
    }

    @Override
    public void remove(String key) {
        cache.remove(CAPTCHA_CACHE_NAME, key);
    }

    @Override
    public void removeAll() {
        cache.removeAll(CAPTCHA_CACHE_NAME);
    }
}
