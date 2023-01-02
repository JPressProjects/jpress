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
package io.jpress.core.module;

import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.module
 */
public class ModuleManager {
    private static final ModuleManager me = new ModuleManager();
    private List<ModuleListener> moduleListeners = new ArrayList<>();


    private ModuleManager() {
        initModuleListeners();
    }

    public static ModuleManager me() {
        return me;
    }


    public List<ModuleListener> getListeners() {
        return moduleListeners;
    }

    /**
     * 初始化 监听器
     * 其他 module 会在监听器里完成所有需要的配置
     */
    private void initModuleListeners() {

        List<Class<ModuleListener>> classes = ClassScanner.scanSubClass(ModuleListener.class, true);
        if (classes == null) {
            return;
        }

        for (Class<ModuleListener> moduleListenerClass : classes) {
            addListener(ClassUtil.newInstance(moduleListenerClass));
        }
    }

    public void addListener(ModuleListener listener){
        moduleListeners.add(listener);
    }

    public void removeListener(ModuleListener listener){
        moduleListeners.remove(listener);
    }

    public void removeListener(Class<? extends ModuleListener> clazz){
        moduleListeners.removeIf(listener -> listener.getClass() == clazz);
    }

}
