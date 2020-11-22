package cn.edu.bupt.pdptw.configuration;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    public static List<File> getFile(String path) {
        File file = new File(path);
        File[] array = file.listFiles();
        List<File> result = new ArrayList<>();

        if (array == null || array.length == 0) {
            return result;
        }
        for (File value : array) {
            if (value.isFile()) {
                result.add(value);
            } else if (value.isDirectory()) {
                result.addAll(getFile(value.getPath()));
            }
        }
        return result;
    }

    public static File[] getFiles(String path) {
        File file = new File(path);
        return file.listFiles();
    }

    public static String basePath(){
       /* try {
            return new File("").getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        return System.getProperty("user.dir");
    }

    public static void writeFile(String fileName, String cont) {
        File file = new File(fileName);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file,true);
            outputStream.write(cont.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        System.out.println(basePath());
    }
}
