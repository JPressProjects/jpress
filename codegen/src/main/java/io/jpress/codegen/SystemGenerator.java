package io.jpress.codegen;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.generator.TableMeta;
import io.jboot.Jboot;
import io.jboot.codegen.CodeGenHelpler;
import io.jboot.utils.StringUtils;
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

        String dbTables = "user,attachment,menu,option,payment_record,permission,role,utm";

        Jboot.setBootArg("jboot.datasource.url", "jdbc:mysql://127.0.0.1:3306/newjpress");
        Jboot.setBootArg("jboot.datasource.user", "root");

        String modelPackage = "io.jpress.model";

        String baseModelPackage = modelPackage + ".base";

        String modelDir = PathKit.getWebRootPath() + "/../jpress-model/src/main/java/" + modelPackage.replace(".", "/");
        String baseModelDir = PathKit.getWebRootPath() + "/../jpress-model/src/main/java/" + baseModelPackage.replace(".", "/");

        System.out.println("start generate...dir:" + modelDir);

        List<TableMeta> tableMetaList = new ArrayList<TableMeta>();
        Set<String> excludeTableSet = StringUtils.splitToSet(dbTables, ",");
        for (TableMeta tableMeta :  CodeGenHelpler.createMetaBuilder().build()) {
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
