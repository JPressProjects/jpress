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
package io.jpress.codegen;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.BaseModelGenerator;
import io.jpress.codegen.generator.ModelGenerator;
import io.jpress.codegen.generator.ServiceApiGenerator;
import io.jpress.codegen.generator.ServiceProviderGenerator;

import java.util.ArrayList;
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

        String dbTables = "user,attachment,menu,option,payment_record,permission,role,utm,wechat_menu,wechat_reply";

        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/newjpress");
        JbootApplication.setBootArg("jboot.datasource.user", "root");

        String modelPackage = "io.jpress.model";

        String baseModelPackage = modelPackage + ".base";

        String modelDir = PathKit.getWebRootPath() + "/../jpress-model/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/../jpress-model/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...dir:" + modelDir);

        List<TableMeta> tableMetaList = new ArrayList<>();
        Set<String> excludeTableSet = StrUtil.splitToSet(dbTables, ",");
        for (TableMeta tableMeta : CodeGenHelpler.createMetaBuilder().build()) {
            if (excludeTableSet.contains(tableMeta.name.toLowerCase())) {
                tableMetaList.add(tableMeta);
            }
        }


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetaList);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetaList);

        String servicePackage = "io.jpress.service";
        String apiPath = PathKit.getWebRootPath() + "/../jpress-service-api/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = PathKit.getWebRootPath() + "/../jpress-service-provider/src/main/java/" + servicePackage.replace(".", "/") + "/provider";


        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetaList);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetaList);

    }

}
