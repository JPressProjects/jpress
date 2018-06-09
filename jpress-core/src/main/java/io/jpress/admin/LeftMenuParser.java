package io.jpress.admin;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Michael Yang 杨福海 （fuhai999@gmail.com）
 * @version V1.0
 * @Package io.jpress.admin
 */
public class LeftMenuParser {

    public static LeftMenu parseMenu(File file) {

        if (file == null || !file.exists()) {
            return null;
        }

        LeftMenu leftMenu = new LeftMenu();

        try {
            String[] lines = readLines(new FileInputStream(file));
            if (lines != null && lines.length > 0) {
                for (String line : lines) {
                    leftMenu.addItem(new LeftMenuItem(line));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return leftMenu;
    }


    public static String[] readLines(InputStream is) throws IOException {
        ArrayList lines = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
            return (String[]) lines.toArray(new String[0]);
        } finally {
            reader.close();
        }

    }


}
