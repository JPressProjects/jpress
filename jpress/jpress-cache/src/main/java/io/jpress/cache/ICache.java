/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress.cache;

import java.util.List;

import com.jfinal.plugin.ehcache.IDataLoader;

public interface ICache extends com.jfinal.plugin.activerecord.cache.ICache {

	public List<?> getKeys(String cacheName);

	public void remove(String cacheName, Object key);

	public void removeAll(String cacheName);

	public <T> T get(String cacheName, Object key, IDataLoader dataLoader);
}
