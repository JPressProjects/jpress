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
package io.jpress.codegen;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.*;

import java.util.List;
import java.util.Set;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: 根据数据库信息，生成 JPress 系统代码
 * @Package io.jpress.core.code
 */
public class SystemGenerator {

    public static void main(String[] args) {

        String dbTables = "user,attachment,menu,option,payment_record,permission,role,utm," +
                "wechat_menu,wechat_reply," +
                "member,member_group,member_dist_amount,member_joined_record,member_price," +
                "user_address,user_amount,user_amount_statement,user_amount_payout," +
                "coupon,coupon_code,coupon_used_record," +
                "user_cart,user_order,user_order_item,user_order_delivery,user_order_invoice," +
                "user_openid,user_favorite,user_tag," +
                "payment_record";

        String optionsTables = "coupon,member,member_group,product,product_category,user_address," +
                "user_amount_statement,user_amount_payout,user_cart,user_order,user_order_item,user_order_delivery,user_order_invoice," +
                "payment_record,user_openid,member_joined_record,user_favorite,user_tag";

        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/jpress3");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");

        String modelPackage = "io.jpress.model";

        String baseModelPackage = modelPackage + ".base";

        String modelDir = PathKit.getWebRootPath() + "/../jpress-model/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/../jpress-model/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...dir:" + modelDir);

        Set<String> genTableNames = StrUtil.splitToSet(dbTables, ",");
        MetaBuilder metaBuilder = CodeGenHelpler.createMetaBuilder();
        metaBuilder.setGenerateRemarks(true);
        List<TableMeta> tableMetas = metaBuilder.build();
        tableMetas.removeIf(tableMeta -> genTableNames != null && !genTableNames.contains(tableMeta.name.toLowerCase()));


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetas);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetas);

        String servicePackage = "io.jpress.service";
        String apiPath = PathKit.getWebRootPath() + "/../jpress-service-api/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = PathKit.getWebRootPath() + "/../jpress-service-provider/src/main/java/" + servicePackage.replace(".", "/") + "/provider";


        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetas);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetas);

        Set<String> optionsTableNames = StrUtil.splitToSet(optionsTables, ",");
        if (optionsTableNames != null && optionsTableNames.size() > 0) {
            tableMetas.removeIf(tableMeta -> !optionsTableNames.contains(tableMeta.name.toLowerCase()));
            new BaseOptionsModelGenerator(baseModelPackage, baseModelDir).generate(tableMetas);
        }
    }

}
