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
package io.jpress.core.menu.annotation;

import java.lang.annotation.*;

/**
 * 用来给给Controller的方法进行标注，申明此方法为一个后台菜单
 * 后台菜单被包含在 group里，而 group 是由module来定义的，jpress系统也内置了几个group
 * <p>
 * groupId 用来标识 这个方法被放在哪个group里
 */
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface AdminMenu {

    String groupId();

    String text();

    String icon() default "";

    String target() default "";

    int order() default 100; //越小在越前面
}