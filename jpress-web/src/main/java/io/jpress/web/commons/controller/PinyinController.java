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
package io.jpress.web.commons.controller;

import com.github.stuxuhai.jpinyin.PinyinException;
import com.github.stuxuhai.jpinyin.PinyinFormat;
import com.github.stuxuhai.jpinyin.PinyinHelper;
import com.jfinal.core.Controller;
import com.jfinal.kit.Ret;
import io.jboot.utils.StrUtils;
import io.jboot.web.controller.annotation.RequestMapping;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 首页
 * @Package io.jpress.web.admin
 */
@RequestMapping("/commons/pinyin")
public class PinyinController extends Controller {


    public void doGetPinyin() {
        String para = getPara();
        if (StrUtils.isBlank(para)) {
            renderJson(Ret.fail());
            return;
        }

        try {
            String pinyin = PinyinHelper.convertToPinyinString(StrUtils.urlDecode(para), "", PinyinFormat.WITHOUT_TONE);
            renderJson(Ret.ok().set("data", pinyin));
            return;
        } catch (PinyinException e) {
            e.printStackTrace();
        }

        renderJson(Ret.fail());
    }
}
