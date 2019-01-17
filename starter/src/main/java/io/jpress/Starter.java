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
package io.jpress;

import io.jboot.app.JbootApplication;
import io.jpress.commons.scanner.FileScanner;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress
 */
public class Starter {

    public static void main(String[] args) {
        JbootApplication.run(args);
        startFileScanner();
    }

    private static void startFileScanner() {
        try {
            String classPath = Starter.class.getClassLoader().getResource("").toURI().getPath();
            File srcRootPath = new File(classPath + "../../../");

            List<File> resourcesDirs = new ArrayList<>();
            findResourcesPath(srcRootPath, resourcesDirs);

            for (File resourcesDir : resourcesDirs) {
                startNewScanner(resourcesDir.getCanonicalFile(), classPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void findResourcesPath(File root, List<File> resourcesDirs) {
        File[] dirs = root.listFiles(pathname -> pathname.isDirectory());
        for (File dir : dirs) {
            if (dir.getName().equals("webapp")
                    && dir.getParentFile().getName().equals("main")) {
                resourcesDirs.add(dir);
            } else {
                findResourcesPath(dir, resourcesDirs);
            }
        }
    }


    private static void startNewScanner(File resourcesDir, String classPath) throws IOException, URISyntaxException {
        FileScanner scanner = new FileScanner(resourcesDir.getCanonicalPath(), 5) {
            @Override
            public void onChange(String action, String file) {
                if (FileScanner.ACTION_INIT.equals(action)) {
                    return;
                }

                // main/webapp/
                int indexOf = file.indexOf("main"
                        + File.separator
                        + "webapp"
                        + File.separator);

                File target = new File(classPath, "webapp" + File.separator + file.substring(indexOf + 11));
                System.err.println(action + ":" + target);

                //文件删除
                if (FileScanner.ACTION_DELETE.equals(action)) {
                    target.delete();
                }
                //新增文件 或 修改文件
                else {
                    try {
                        FileUtils.copyFile(new File(file), target);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        scanner.start();
    }
}
