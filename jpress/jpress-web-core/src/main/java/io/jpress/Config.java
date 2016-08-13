/**
 * Copyright (c) 2015-2016, Michael Yang 杨福海 (fuhai999@gmail.com).
 *
 * Licensed under the GNU Lesser General Public License (LGPL) ,Version 3.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.jpress;

import com.jfinal.config.Interceptors;
import com.jfinal.kit.PropKit;

import io.jpress.core.Jpress;
import io.jpress.core.JpressConfig;
import io.jpress.core.db.DbDialectFactory;
import io.jpress.interceptor.AdminInterceptor;
import io.jpress.plugin.message.Actions;
import io.jpress.plugin.message.MessageKit;
import io.jpress.plugin.search.SearcherFactory;
import io.jpress.ui.freemarker.function.OptionChecked;
import io.jpress.ui.freemarker.function.OptionValue;
import io.jpress.ui.freemarker.function.TaxonomyBox;
import io.jpress.ui.freemarker.tag.ArchivesTag;
import io.jpress.ui.freemarker.tag.ContentTag;
import io.jpress.ui.freemarker.tag.ContentsTag;
import io.jpress.ui.freemarker.tag.ModulesTag;
import io.jpress.ui.freemarker.tag.TagsTag;
import io.jpress.ui.freemarker.tag.TaxonomysTag;
import io.jpress.utils.StringUtils;

public class Config extends JpressConfig {

	@Override
	public void configInterceptor(Interceptors interceptors) {
		super.configInterceptor(interceptors);
		interceptors.add(new AdminInterceptor());
	}

	@Override
	public void onJfinalStartBefore() {
		dbDialectConfig();
	}

	@Override
	public void onJfinalStartAfter() {

		Jpress.addTag("jp_contents", new ContentsTag());
		Jpress.addTag("jp_content", new ContentTag());
		Jpress.addTag("jp_modules", new ModulesTag());
		Jpress.addTag("jp_tags", new TagsTag());
		Jpress.addTag("jp_taxonomys", new TaxonomysTag());
		Jpress.addTag("jp_archives", new ArchivesTag());

		Jpress.addFunction("taxonomyBox", new TaxonomyBox());
		Jpress.addFunction("option", new OptionValue());
		Jpress.addFunction("checked", new OptionChecked());

		doSearcherConfig();
		MessageKit.sendMessage(Actions.JPRESS_STARTED);
	}

	private void doSearcherConfig() {
		if (!Jpress.isInstalled()) {
			return;
		}
		String searcher = PropKit.get("jpress_searcher");
		if (StringUtils.isNotBlank(searcher)) {
			SearcherFactory.use(searcher);
		}
	}

	private void dbDialectConfig() {
		String dialect = PropKit.get("jpress_db_dialect");
		if (StringUtils.isNotBlank(dialect)) {
			DbDialectFactory.use(dialect);
		}
	}

}
