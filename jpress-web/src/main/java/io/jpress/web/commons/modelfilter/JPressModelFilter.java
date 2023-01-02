///**
// * Copyright (c) 2016-2023, Michael Yang 杨福海 (fuhai999@gmail.com).
// * <p>
// * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// * <p>
// * http://www.gnu.org/licenses/lgpl-3.0.txt
// * <p>
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//package io.jpress.web.commons.modelfilter;
//
//import io.jboot.db.model.JbootModel;
//import io.jboot.db.model.JbootModelFilter;
//import io.jboot.utils.StrUtil;
//
//import java.util.HashSet;
//import java.util.Set;
//
///**
// * @author michael yang (fuhai999@gmail.com)
// * @Date: 2020/3/5
// */
//public class JPressModelFilter implements JbootModelFilter {
//
//    @Override
//    public void filter(JbootModel model, int filterBy) {
//        Set<String> ignoreAttrs = new HashSet<>();
//
//        if (model._getTable().hasColumnLabel("options")) {
//            ignoreAttrs.add("options");
//        }
//
//        if (model._getTable().hasColumnLabel("content")) {
//            ignoreAttrs.add("content");
//        }
//
//        if (model._getTable().hasColumnLabel("summary")) {
//            ignoreAttrs.add("summary");
//        }
//
//        StrUtil.escapeModel(model, ignoreAttrs.toArray(new String[]{}));
//    }
//}
