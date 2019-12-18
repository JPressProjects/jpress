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
package io.jpress.web.sharekit;

import com.jfinal.aop.Aop;
import io.jpress.model.Member;
import io.jpress.model.MemberGroup;
import io.jpress.model.Role;
import io.jpress.model.User;
import io.jpress.service.MemberService;
import io.jpress.service.PermissionService;
import io.jpress.service.RoleService;
import io.jpress.web.interceptor.UserInterceptor;

import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.core.web.sharekit
 */

public class PermissionKits {

    public static boolean hasPermission(Role role, long permissionId) {
        RoleService service = Aop.get(RoleService.class);
        return service.hasPermission(role.getId(), permissionId);
    }

    public static boolean hasPermission(User user, long permissionId) {
        PermissionService service = Aop.get(PermissionService.class);
        return service.hasPermission(user.getId(), permissionId);
    }


    public static final boolean hasRole(long userId, long roleId) {
        RoleService roleService = Aop.get(RoleService.class);
        return roleService.hasRole(userId, roleId);
    }

    public static final boolean hasRole(long roleId) {
        User user = UserInterceptor.getThreadLocalUser();
        RoleService roleService = Aop.get(RoleService.class);
        return roleService.hasRole(user.getId(), roleId);
    }

    public static final boolean hasRole(String roleFlag) {
        User user = UserInterceptor.getThreadLocalUser();
        RoleService roleService = Aop.get(RoleService.class);
        return roleService.hasRole(user.getId(), roleFlag);
    }

    public static final boolean hasRole(long userId, String roleFlag) {
        RoleService roleService = Aop.get(RoleService.class);
        return roleService.hasRole(userId, roleFlag);
    }

    public static final boolean isSupperAdmin() {
        User user = UserInterceptor.getThreadLocalUser();
        RoleService roleService = Aop.get(RoleService.class);
        return roleService.isSupperAdmin(user.getId());
    }

    public boolean hasMember(String... memberFlags) {

        User user = UserInterceptor.getThreadLocalUser();
        if (user == null) {
            return false;
        }

        if (memberFlags == null || memberFlags.length == 0) {
            throw new IllegalArgumentException("hasMember(memberFlags...) args is error.");
        }

        MemberService memberService = Aop.get(MemberService.class);
        List<Member> members = memberService.findListByUserId(user.getId());

        if (members == null || members.isEmpty()) {
            return false;
        }

        for (String flag : memberFlags) {
            boolean hasFlag = false;
            for (Member member : members) {
                MemberGroup group = member.get("group");
                if (group != null && flag.equals(group.getFlag())) {
                    hasFlag = true;
                }
            }
            if (!hasFlag) {
                return false;
            }
        }

        return true;
    }

    public boolean hasAnyMember(String... memberFlags) {

        User user = UserInterceptor.getThreadLocalUser();
        if (user == null) {
            return false;
        }

        if (memberFlags == null || memberFlags.length == 0) {
            throw new IllegalArgumentException("hasAnyMember(memberFlags...) args is error.");
        }

        MemberService memberService = Aop.get(MemberService.class);
        List<Member> members = memberService.findListByUserId(user.getId());

        if (members == null || members.isEmpty()) {
            return false;
        }

        for (String flag : memberFlags) {
            for (Member member : members) {
                MemberGroup group = member.get("group");
                if (group != null && flag.equals(group.getFlag())) {
                    return true;
                }
            }
        }

        return false;
    }

}
