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
package io.jpress.service.task;

import com.jfinal.plugin.activerecord.Db;
import io.jboot.components.schedule.annotation.FixedRate;
import io.jpress.model.Utm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//1分钟执行一次
@FixedRate(period = 60, initialDelay = 60)
public class UtmBatchSaveTask implements Runnable {

    private static List<Utm> utmList = Collections.synchronizedList(new ArrayList<>());

    public static void record(Utm utm) {
        utmList.add(utm);
    }


    @Override
    public void run() {
        if (utmList.isEmpty()) {
            return;
        }

        List<Utm> tempUtmList = new ArrayList<>(utmList);
        utmList.clear();

        Db.batchSave(tempUtmList, 1000);
    }



    public static void refreshRecord(){
        new UtmBatchSaveTask().run();
    }

}