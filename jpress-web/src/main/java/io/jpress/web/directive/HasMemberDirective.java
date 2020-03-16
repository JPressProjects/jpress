/**
 * Copyright (c) 2016-2020, Michael Yang 杨福海 (fuhai999@gmail.com).
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
package io.jpress.web.directive;

import com.jfinal.aop.Aop;
import com.jfinal.template.Env;
import com.jfinal.template.io.Writer;
import com.jfinal.template.stat.Scope;
import io.jboot.web.directive.annotation.JFinalDirective;
import io.jboot.web.directive.base.JbootDirectiveBase;
import io.jpress.model.Member;
import io.jpress.model.MemberGroup;
import io.jpress.model.User;
import io.jpress.service.MemberService;
import io.jpress.web.interceptor.UserInterceptor;

import java.util.List;

/**
 * @author michael yang (fuhai999@gmail.com)
 * @Date: 2020/1/5
 */
@JFinalDirective("hasMember")
public class HasMemberDirective extends JbootDirectiveBase {

    @Override
    public void onRender(Env env, Scope scope, Writer writer) {
        User user = UserInterceptor.getThreadLocalUser();
        if (user == null || !user.isStatusOk()) {
            return;
        }

        String[] memberFlags = getParas(scope);

        if (memberFlags == null || memberFlags.length == 0) {
            throw new IllegalArgumentException("#hasMember(memberFlags...) args is error." + getLocation());
        }

        MemberService memberService = Aop.get(MemberService.class);
        List<Member> members = memberService.findListByUserId(user.getId());

        if (members == null || members.isEmpty()) {
            return;
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
                return;
            }
        }

        renderBody(env, scope, writer);
    }

    @Override
    public boolean hasEnd() {
        return true;
    }

    private String[] getParas(Scope scope) {
        if (exprList == null || exprList.length() == 0) {
            return null;
        }

        String[] paras = new String[exprList.length()];
        for (int i = 0; i < paras.length; i++) {
            paras[i] = String.valueOf(exprList.getExpr(i).eval(scope));
        }

        return paras;
    }
}
