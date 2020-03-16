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
package io.jpress.commons;

import java.io.Serializable;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 一个类似 JFinal 的 Ret 的类
 * @Package io.jpress.commons
 */
public class Msg implements Serializable {

    private static final String STATE_OK = "ok";
    private static final String STATE_FAIL = "fail";

    private int errorCode = -1; //未知状态
    private String state;
    private String message;
    private Object data;

    public Msg() {
    }

    public static Msg create() {
        return new Msg();
    }

    public static Msg ok() {
        return new Msg().setOk();
    }

    public static Msg ok(String message) {
        return new Msg().setOk().setMessage(message);
    }

    public static Msg fail() {
        return new Msg().setFail();
    }

    public static Msg fail(int errorCode) {
        return new Msg().setFail().setErrorCode(errorCode);
    }

    public static Msg fail(String message) {
        return new Msg().setFail().setMessage(message);
    }

    public static Msg fail(int errorCode, String message) {
        return new Msg()
                .setFail()
                .setErrorCode(errorCode)
                .setMessage(message);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public Msg setErrorCode(int errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public Msg setOk() {
        this.setState(STATE_OK);
        this.setErrorCode(0);
        return this;
    }

    public Msg setFail() {
        this.setState(STATE_FAIL);
        return this;
    }

    public String getState() {
        return state;
    }

    public Msg setState(String state) {
        this.state = state;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Msg setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Msg setData(Object data) {
        this.data = data;
        return this;
    }

    public boolean isOk() {
        return STATE_OK.equals(state);
    }

    public boolean isFail() {
        return STATE_FAIL.endsWith(state);
    }

}
