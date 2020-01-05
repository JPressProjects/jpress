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
package io.jpress.web.commons.express;

/**
 * 快递公司
 *
 * @author michael
 */
public enum ExpressCompany {

    /**
     * 快递公司列表
     */
    SHUNFENG("顺丰快递", "shunfeng"),
    YUANTONG("圆通快递", "yuantong"),
    ZHONGTONG("中通快递", "zhongtong"),
    SHENTONG("申通快递", "shentong"),
    YUNDA("韵达快递", "yunda"),
    ZHAIJISONG("宅急送", "zhaijisong"),
    EMS("邮政EMS", "ems"),
    YOUZHENG("邮政快递（挂号信）", "youzheng"),
    TIANTIAN("天天快递", "tiantian"),
    UPS("UPS", "ups"),
    FEDEX("Fedex国际", "fedex"),
    SHUNDA("顺达快递", "shunda"),
    DEBANG("德邦", "debang"),
    BAISHI("汇通（百世快递）", "baishi"),
    JINGDONG("京东快递", "jingdong"),
    OTHER("其他快递", "other");


    public static ExpressCompany getByCode(String code) {
        for (ExpressCompany com : values()) {
            if (code.equals(com.code)) {
                return com;
            }
        }
        return null;
    }


    ExpressCompany(String name, String code) {
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
