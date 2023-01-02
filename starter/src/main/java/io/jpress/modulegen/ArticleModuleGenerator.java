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
package io.jpress.modulegen;

import io.jpress.codegen.ModuleGenerator;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jboot.codegen
 */
public class ArticleModuleGenerator {


    private static String dbUrl = "jdbc:mysql://192.168.3.2:3306/jpress";
    private static String dbUser = "root";
    private static String dbPassword = "";

    private static String moduleName = "article";
    private static String dbTables = "article,article_category,article_comment";
    private static String optionsTables = "";
    private static String sortTables = "article_category";
    private static String sortOptionsTables = "";
    private static String modelPackage = "io.jpress.module.article.model";
    private static String servicePackage = "io.jpress.module.article.service";


    public static void main(String[] args) {

        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleName, dbUrl, dbUser, dbPassword, dbTables, optionsTables, sortTables, sortOptionsTables, modelPackage, servicePackage);
        moduleGenerator.gen();

    }
}
