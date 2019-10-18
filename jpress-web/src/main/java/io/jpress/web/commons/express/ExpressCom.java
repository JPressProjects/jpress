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
package io.jpress.web.commons.express;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * 快递公司
 */
public class ExpressCom {

    public static final ExpressCom SHUNFENG = new ExpressCom("顺丰快递", "shunfeng");
    public static final ExpressCom YUANTONG = new ExpressCom("圆通快递", "yuantong");
    public static final ExpressCom ZHONGTONG = new ExpressCom("中通快递", "zhongtong");
    public static final ExpressCom SHENTONG = new ExpressCom("申通快递", "shentong");
    public static final ExpressCom YUNDA = new ExpressCom("韵达快递", "yunda");
    public static final ExpressCom ZHAIJISONG = new ExpressCom("宅急送", "zhaijisong");
    public static final ExpressCom EMS = new ExpressCom("邮政EMS", "ems");
    public static final ExpressCom YOUZHENG = new ExpressCom("邮政快递", "youzheng");
    public static final ExpressCom TIANTIAN = new ExpressCom("天天快递", "tiantian");
    public static final ExpressCom UPS = new ExpressCom("UPS", "ups");
    public static final ExpressCom SHUNDA = new ExpressCom("顺达快递", "shunda");
    public static final ExpressCom DEBANG = new ExpressCom("德邦", "debang");
    public static final ExpressCom BAISHI = new ExpressCom("百世快递", "baishi");
    public static final ExpressCom OTHER = new ExpressCom("其他快递", "other");


    public static final List<ExpressCom> EXPRESS_COMS = Lists.newArrayList(
            SHUNFENG,
            YUANTONG,
            ZHONGTONG,
            SHENTONG,
            YUNDA,
            ZHAIJISONG,
            EMS,
            YOUZHENG,
            TIANTIAN,
            UPS,
            SHUNDA,
            DEBANG,
            BAISHI
    );


    ExpressCom(String name, String code) {
        this.name = name;
        this.code = code;
    }

    private String name;
    private String code;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
