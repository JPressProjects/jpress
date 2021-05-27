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
package io.jpress.service;

import io.jpress.model.Option;

import java.util.List;

public interface OptionService {

    /**
     * find model by primary key
     *
     * @param id
     * @return
     */
    public Option findById(Object id);


    /**
     * find all model
     *
     * @return all <Option
     */
    public List<Option> findAll();


    /**
     * delete model by primary key
     *
     * @param id
     * @return success
     */
    public boolean deleteById(Object id);


    /**
     * 根据 key 进行删除
     *
     * @param key
     * @return
     */
    public boolean deleteByKey(String key);


    /**
     * delete model
     *
     * @param model
     * @return
     */
    public boolean delete(Option model);


    /**
     * save model to database
     *
     * @param model
     * @return
     */
    public Object save(Option model);


    /**
     * save or update model
     *
     * @param model
     * @return if save or update success
     */
    public Object saveOrUpdate(Option model);


    /**
     * update data model
     *
     * @param model
     * @return
     */
    public boolean update(Option model);


    public String findByKey(String key);


    public Boolean findAsBoolByKey(String key);

    public Integer findAsIntegerByKey(String key);

    public Float findAsFloatByKey(String key);

    public Object saveOrUpdate(String key, String value);


}