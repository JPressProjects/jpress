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


import com.jfinal.kit.LogKit;
import io.jboot.components.event.JbootEvent;
import io.jboot.components.event.JbootEventListener;
import io.jboot.utils.ClassScanner;
import io.jboot.utils.ClassUtil;
import io.jboot.utils.StrUtil;
import io.jpress.core.install.Installer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SitemapManager implements JbootEventListener {

    private static SitemapManager me = new SitemapManager();

    private SitemapManager() {
    }

    public static SitemapManager me() {
        return me;
    }

    private Map<String, SitemapProvider> providers = new ConcurrentHashMap<>();
    private List<SitemapBuilder> builders = Collections.synchronizedList(new ArrayList<>());


    public void build() {
        for (SitemapBuilder b : builders) {
            try {
                b.build();
            } catch (Exception ex) {
                LogKit.error(ex.toString(), ex);
            }
        }
    }

    public List<SitemapBuilder> getBuilders() {
        return builders;
    }

    public void setBuilders(List<SitemapBuilder> builders) {
        this.builders = builders;
    }

    public void addBuilder(SitemapBuilder builder){
        this.builders.add(builder);
    }

    public void init() {
        if (Installer.notInstall()) {
            Installer.addListener(this);
            return;
        }

        List<Class<SitemapProvider>> cls = ClassScanner.scanSubClass(SitemapProvider.class, true);
        if (cls != null && cls.size() > 0) {
            cls.forEach(c -> {
                SitemapProvider provider = ClassUtil.newInstance(c);
                if (provider != null && StrUtil.isNotBlank(provider.getName())) {
                    providers.put(provider.getName(), provider);
                }
            });
        }

        List<Class<SitemapBuilder>> builderCls = ClassScanner.scanSubClass(SitemapBuilder.class, true);
        if (builderCls != null && builderCls.size() > 0) {
            builderCls.forEach(c -> {
                SitemapBuilder builder = ClassUtil.newInstance(c);
                builders.add(builder);
            });
        }
    }

    @Override
    public void onEvent(JbootEvent event) {
        init();
    }

    public void addProvider(String name, SitemapProvider provider) {
        providers.put(name, provider);
    }

    public void addProvider(SitemapProvider provider) {
        providers.put(provider.getName(), provider);
    }

    public SitemapProvider getProvider(String name) {
        return providers.get(name);
    }

    public Map<String, SitemapProvider> getProviders() {
        return providers;
    }

    public List<Sitemap> getIndexSitemapList() {
        List<Sitemap> sitemaps = new ArrayList<>();
        providers.forEach((s, render) -> sitemaps.add(new Sitemap("/sitemap/" + s + ".xml", render.getLastmod())));
        return sitemaps;
    }


}
