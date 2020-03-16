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
package io.jpress.web.front;


import com.jfinal.core.JFinal;
import com.jfinal.kit.Base64Kit;
import io.jboot.web.controller.JbootControllerContext;
import io.jpress.commons.SnowFlake;
import io.jpress.commons.utils.CommonsUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

public class PayKit {

    public static void redirect(String paytype, String trxno) {
        JbootControllerContext.get().redirect("/pay/" + paytype + "/" + trxno);
    }


    public static String buildPayUrl(String paytype, String trxno) {
        return JFinal.me().getContextPath() + "/pay/" + paytype + "/" + trxno;
    }

    public static void redirectError(String backAction) {
        JbootControllerContext.get().redirect("/pay/error?gotoUrl=" + backAction);
    }

    private static final SnowFlake SNOW_FLAKE = new SnowFlake(1, 1);

    public static String genOrderNS() {
        return String.valueOf(SNOW_FLAKE.genNextId());
    }

    public static String image2base64Str(BufferedImage image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", baos);
            String base64Str = Base64Kit.encode(baos.toByteArray());
            return "data:image/jpg;base64," + base64Str;
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            CommonsUtils.quietlyClose(baos);
        }
        return null;
    }
}
