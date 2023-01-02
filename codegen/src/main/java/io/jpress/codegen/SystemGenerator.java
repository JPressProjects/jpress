/**
 * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
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

import com.jfinal.plugin.activerecord.generator.MetaBuilder;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.app.JbootApplication;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StrUtil;
import io.jpress.codegen.generator.*;

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

        String dbTables = "attachment,attachment_category,attachment_video,attachment_video_category," +
                "menu,option,permission,role," +
                "site_info," +
                "user,user_openid,user_tag," +
                "template_block_option," +
                "utm,";

        String optionsTables = "user_openid,user_tag,attachment_video";

        String sortTables = "menu,wechat_menu";
        String sortOptionsTables = "";

        JbootApplication.setBootArg("jboot.datasource.url", "jdbc:mysql://192.168.3.2:3306/jpress");
        JbootApplication.setBootArg("jboot.datasource.user", "root");
        JbootApplication.setBootArg("jboot.datasource.password", "123456");

        String modelPackage = "io.jpress.model";

        String baseModelPackage = modelPackage + ".base";

        String modelDir = CodeGenKit.getProjectRootPath() + "/jpress-model/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = CodeGenKit.getProjectRootPath() + "/jpress-model/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...dir:" + modelDir);

        Set<String> genTableNames = StrUtil.splitToSet(dbTables, ",");
        MetaBuilder metaBuilder = CodeGenHelpler.createMetaBuilder();
        metaBuilder.setGenerateRemarks(true);
        List<TableMeta> tableMetas = metaBuilder.build();
        tableMetas.removeIf(tableMeta -> genTableNames != null && !genTableNames.contains(tableMeta.name.toLowerCase()));


        new BaseModelGenerator(baseModelPackage, baseModelDir).generate(tableMetas);
        new ModelGenerator(modelPackage, baseModelPackage, modelDir).generate(tableMetas);

        String servicePackage = "io.jpress.service";
        String apiPath = CodeGenKit.getProjectRootPath() + "/jpress-service/src/main/java/" + servicePackage.replace(".", "/");
        String providerPath = CodeGenKit.getProjectRootPath() + "/jpress-service-provider/src/main/java/" + servicePackage.replace(".", "/") + "/provider";


        new ServiceApiGenerator(servicePackage, modelPackage, apiPath).generate(tableMetas);
        new ServiceProviderGenerator(servicePackage, modelPackage, providerPath).generate(tableMetas);

        Set<String> optionsTableNames = StrUtil.splitToSet(optionsTables, ",");
        if (optionsTableNames != null && optionsTableNames.size() > 0) {
            new BaseOptionsModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,optionsTableNames));
        }


        //SortTables
        Set<String> sortTableNames = StrUtil.splitToSet(sortTables, ",");
        if (sortTableNames != null && sortTableNames.size() > 0) {
            new BaseSortModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,sortTableNames));
        }

        //SortOptionsTables
        Set<String> sortOptionsTableNames = StrUtil.splitToSet(sortOptionsTables, ",");
        if (sortOptionsTableNames != null && sortOptionsTableNames.size() > 0) {
            new BaseSortOptionsModelGenerator(baseModelPackage, baseModelDir).generate(copyTableMetasByNames(tableMetas,sortOptionsTableNames));
        }

    }


    private  static List<TableMeta> copyTableMetasByNames(List<TableMeta> tableMetas,Set<String> names){
        List<TableMeta> retList = new ArrayList<>();
        tableMetas.forEach(tableMeta -> {
            if (names.contains(tableMeta.name.toLowerCase())){
                retList.add(tableMeta);
            }
        });
        return retList;
    }

}
