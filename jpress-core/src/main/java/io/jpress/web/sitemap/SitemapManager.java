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
package io.jpress.web.sitemap;


import io.jpress.JPressConsts;
import io.jpress.JPressOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SitemapManager {

    private static SitemapManager me = new SitemapManager();

    private SitemapManager() {
        addRender("new",new NewestSitemapRender());
    }

    public static SitemapManager me() {
        return me;
    }

    private Map<String,SitemapRender> renderMap = new ConcurrentHashMap<>();


    public void addRender(String key,SitemapRender render){
        renderMap.put(key,render);
    }

    public List<Sitemap> getIndexSitemapList(){
        List<Sitemap> sitemaps = new ArrayList<>();
        String domain = JPressOptions.get(JPressConsts.OPTION_WEB_DOMAIN,"");
        String prefix = domain+"/sitemap/";
        renderMap.forEach((s, render) -> sitemaps.add(new Sitemap(prefix+s+".xml",render.getLastmod())));
        return sitemaps;
    }

    public SitemapRender getSitemapRender(String key){
        return renderMap.get(key);
    }

}
