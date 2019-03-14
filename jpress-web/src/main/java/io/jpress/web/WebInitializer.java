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
package io.jpress.web;

import com.jfinal.template.Engine;
import io.jboot.core.listener.JbootAppListenerBase;
import io.jpress.web.sharekit.JPressShareFunctions;
import io.jpress.web.sharekit.PermissionKits;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Title: JPress 初始化工具
 * @Package io.jpress
 */
public class WebInitializer extends JbootAppListenerBase {


    @Override
    public void onEngineConfig(Engine engine) {

        try {
            engine.addSharedFunction("/WEB-INF/views/admin/_layout/_layout.html");
            engine.addSharedFunction("/WEB-INF/views/admin/_layout/_layer.html");
            engine.addSharedFunction("/WEB-INF/views/admin/_layout/_paginate.html");
            engine.addSharedFunction("/WEB-INF/views/ucenter/_layout/_layout.html");

            engine.addSharedStaticMethod(JPressShareFunctions.class);
            engine.addSharedStaticMethod(PermissionKits.class);

        } catch (Exception ex) {
            printErrorInfoAndExit();
        }
    }

    private void printErrorInfoAndExit() {
        System.err.println("\n\r错误：无法找到必须的资源文件，启动失败! ");
        System.err.println("请您先使用 maven 编译后，再运行 jpress，编译命令: mvn clean install ");
        System.exit(-1);
    }


    @Override
    public void onStartBefore() {

        OptionInitializer.me().init();

    }

}
