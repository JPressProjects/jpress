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
package io.jpress.modulegen;

import io.jpress.codegen.ModuleGenerator;

/**
 * @author Eric Huang 黄鑫 （ninemm@126.com）
 * @version V1.0
 * @Package io.jboot.codegen
 */
public class CrawlerModuleGenerator {


    private static String dbUrl = "jdbc:mysql://127.0.0.1:3306/jpress-2.0";
    private static String dbUser = "root";
    private static String dbPassword = "123456";

    private static String moduleName = "crawler";
    private static String dbTables = "c_keyword,c_keyword_category,c_spider,c_webpage";
    private static String modelPackage = "io.jpress.module.crawler.model";
    private static String servicePackage = "io.jpress.module.crawler.service";

    private static String tablePrefix = "c";


    public static void main(String[] args) {

        ModuleGenerator moduleGenerator = new ModuleGenerator(moduleName, dbUrl, dbUser, dbPassword, dbTables, modelPackage, servicePackage, tablePrefix, true);
        moduleGenerator.gen();

    }
}
