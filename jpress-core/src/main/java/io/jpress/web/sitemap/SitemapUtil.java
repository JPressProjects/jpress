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
package io.jpress.web.sitemap;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SitemapUtil {

    private static ThreadLocal<SimpleDateFormat> formats = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));

    public static final String date2str(Date date) {
        if (date == null) {
            return null;
        }
        return formats.get().format(date);
    }

    public static final Date str2date(String str) {
        try {
            return formats.get().parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static final String nowStr() {
        return formats.get().format(new Date());
    }


}
