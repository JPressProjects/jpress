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
package io.jpress.core.addon;

/**
 * 插件的升级接口
 */
public interface AddonUpgrader {

    /**
     * 进行升级操作
     * @param oldAddon
     * @param thisAddon
     */
    public boolean onUpgrade(AddonInfo oldAddon,AddonInfo thisAddon);

    /**
     * 升级失败的时候进行回滚
     * @param oldAddon
     * @param thisAddon
     */
    public void onRollback(AddonInfo oldAddon,AddonInfo thisAddon);

}
