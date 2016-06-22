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

import io.jpress.core.Jpress;
import io.jpress.core.JpressConfig;
import io.jpress.plugin.search.SearcherFactory;
import io.jpress.ui.freemarker.function.ContentUrl;
import io.jpress.ui.freemarker.function.OptionChecked;
import io.jpress.ui.freemarker.function.OptionValue;
import io.jpress.ui.freemarker.function.TaxonomyBox;
import io.jpress.ui.freemarker.tag.CommentPageTag;
import io.jpress.ui.freemarker.tag.ContentsTag;
import io.jpress.ui.freemarker.tag.ModuleTag;
import io.jpress.ui.freemarker.tag.TagsTag;

public class Config extends JpressConfig {

	@Override
	public void onJfinalStarted() {
		
		Jpress.addTag("jp_contents", new ContentsTag());
		Jpress.addTag("jp_comment_page", new CommentPageTag());
		Jpress.addTag("jp_module", new ModuleTag());
		Jpress.addTag("jp_tags", new TagsTag());
		
		Jpress.addFunction("taxonomyBox", new TaxonomyBox());
		Jpress.addFunction("option", new OptionValue());
		Jpress.addFunction("checked", new OptionChecked());
		Jpress.addFunction("contentUrl", new ContentUrl());
		
		
		SearcherFactory.use("io.jpress.searcher.DbSearcher");
		
	}

}
