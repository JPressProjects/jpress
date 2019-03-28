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


import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Sitemap implements Serializable {
    private String loc;
    private String lastmod;
    private String changefreq;
    private String priority;

    public Sitemap() {

    }

    public Sitemap(String loc, String lastmod) {
        this.loc = loc;
        this.lastmod = lastmod;
    }

    public Sitemap(String loc, Date lastmod) {
        this.loc = loc;
        this.lastmod = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(lastmod);
    }

    public Sitemap(String loc, String lastmod, String changefreq, String priority) {
        this.loc = loc;
        this.lastmod = lastmod;
        this.changefreq = changefreq;
        this.priority = priority;
    }

    public String getLoc() {
        return loc;
    }

    public void setLoc(String loc) {
        this.loc = loc;
    }

    public String getLastmod() {
        return lastmod;
    }

    public void setLastmod(String lastmod) {
        this.lastmod = lastmod;
    }

    public String getChangefreq() {
        return changefreq;
    }

    public void setChangefreq(String changefreq) {
        this.changefreq = changefreq;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String toXml() {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<sitemap>");
        xmlBuilder.append("<loc>" + loc + "</loc>");
        xmlBuilder.append("<lastmod>" + lastmod + "</lastmod>");
        xmlBuilder.append("</sitemap>");
        return xmlBuilder.toString();
    }

    public String toUrlXml() {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<url>");
        xmlBuilder.append("<loc>" + loc + "</loc>");
        xmlBuilder.append("<lastmod>" + lastmod + "</lastmod>");
        xmlBuilder.append("<changefreq>" + changefreq + "</changefreq>");
        xmlBuilder.append("<priority>" + priority + "</priority>");
        xmlBuilder.append("</url>");
        return xmlBuilder.toString();
    }
}
