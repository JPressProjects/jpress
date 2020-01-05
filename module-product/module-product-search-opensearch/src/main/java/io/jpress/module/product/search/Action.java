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
package io.jpress.module.product.search;

import com.jfinal.kit.JsonKit;
import com.jfinal.plugin.activerecord.CPI;
import io.jpress.module.product.model.Product;
import io.jpress.module.product.model.Product;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于构造 OpenSearch 的 Json
 * 文档：https://help.aliyun.com/document_detail/52287.html
 */
public class Action {

    private String cmd;
    private Map fields;


    public static Action addAction(Product product) {
        Action action = new Action();
        action.cmd = "ADD";
        action.fields = new HashMap();
        action.fields.putAll(CPI.getAttrs(product));
        return action;
    }


    public static Action delAction(Object id) {
        Action action = new Action();
        action.cmd = "DELETE";
        action.fields = new HashMap();
        action.fields.put("id", id);
        return action;
    }

    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public Map getFields() {
        return fields;
    }

    public void setFields(Map fields) {
        this.fields = fields;
    }

    public String toJson() {
        Action[] actions = new Action[]{this};
        return JsonKit.toJson(actions);
    }

    public static void main(String[] args) {
        System.out.println(delAction(123).toJson());
    }
}
