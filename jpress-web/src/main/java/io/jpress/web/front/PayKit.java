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
package io.jpress.web.front;


import com.jfinal.core.Controller;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.commons.SnowFlake;

public class PayKit {

    public static void redirect(String paytype, String trxno) {
        redirect(JbootControllerContext.get(), paytype, trxno);
    }

    public static void redirect(Controller controller, String paytype, String trxno) {
        controller.redirect("/pay/" + paytype + "/" + trxno);
    }

    private static final SnowFlake SNOW_FLAKE = new SnowFlake(1,1);
    public static String genOrderNS(){
        return String.valueOf(SNOW_FLAKE.genNextId());
    }
}
