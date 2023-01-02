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
package io.jpress.service;

import io.jpress.model.Option;

import java.util.List;

public interface OptionService {



    /**
     * 查询所有的数据
     *
     * @return 所有的 Option
     */
    List<Option> findAll();



    /**
     * 根据 key 进行删除
     *
     * @param key
     * @return
     */
    boolean deleteByKey(String key);




    String findByKey(String key);


    Boolean findAsBoolByKey(String key);


    Object saveOrUpdate(String key, String value);


    Object saveOrUpdate(String key, String value, Long siteId);


}