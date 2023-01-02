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
package io.jpress.web.admin.kits;

import io.jpress.model.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.web.admin.kits
 */
public class PermissionKits {

    public static Map<String, List<Permission>> groupPermission(List<Permission> permissions) {

        Map<String, List<Permission>> map = new HashMap<>();

        for (Permission permission : permissions) {
            List<Permission> permissionList = map.get(permission.getShortNode());
            if (permissionList == null) {
                permissionList = new ArrayList<>();
                map.put(permission.getShortNode(), permissionList);
            }
            permissionList.add(permission);
        }

        return map;
    }


}
