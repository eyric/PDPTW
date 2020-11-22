package cn.edu.bupt.pdptw.split;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Vehicle;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DataGen {

    private static final Random random = new Random(System.currentTimeMillis());
    private static final String vpath = "D:\\code\\java\\PDPTW\\resources\\test\\vehicles";

    public static String gen(String name, double rat) throws IOException, InvalidFileFormatException {
        DefaultConfigReader reader = new DefaultConfigReader();
        Configuration cfg = Configuration.defaultCfg(name);
        List<Request> requestList = reader.loadRequests(cfg);

        double maxLat = 0;
        double minLat = Double.MAX_VALUE;
        double maxLng = 0;
        double minLng = Double.MAX_VALUE;

        for (Request request : requestList) {
            Location location = request.getLocation();
            double lat = location.getX();
            double lng = location.getY();

            if (lat > maxLat) {
                maxLat = lat;
            }

            if (lat < minLat) {
                minLat = lat;
            }

            if (lng > maxLng) {
                maxLng = lng;
            }

            if (lng < minLng) {
                minLng = lng;
            }
        }


        int vNum = (int) (requestList.size() * rat);
        double dlat = maxLat - minLat;
        double dlng = maxLng - minLng;

        StringBuilder cont = new StringBuilder("{\n" +
                "\t\"vehicles\": [");
        for (int i = 0; i < vNum; i++) {
            int x = (int) (random.nextFloat() * dlat + minLat);
            int y = (int) (random.nextFloat() * dlng + minLng);
            int capacity = random.nextInt(3) * 5 + 10;
            String s = new Vehicle("" + i, capacity, new Location(x, y)).toString();
            cont.append(s).append(",");
        }


        return cont.substring(0, cont.length() - 1) + "]}";
    }

    public static void main(String[] args) throws IOException, InvalidFileFormatException {
        String path = FileUtil.basePath() + "/resources/test/data/";
        travel(path);

    }

    private static void travel(String path) throws IOException, InvalidFileFormatException {
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");

            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        travel(file2.getAbsolutePath());
                    } else {
                        String name = file2.getName();
                        if (name.endsWith(".txt") && !name.contains("task")) {
                            String[] split = file2.getPath().split("\\\\");
                            String filePath = String.join("/", split[split.length - 2], split[split.length - 1]);
                            String modify = gen(filePath, 2);
                            String output = vpath + "/3/" + filePath;
                            output = output.substring(0, output.length() - 3) + "json";
                            new File(output).delete();
                            writeFile(output, modify);
                            System.out.println(file2.getPath());
                        }

                    }
                }
            }
        }
    }

    private static void writeFile(String fileName, String cont) {
        File file = new File(fileName);
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file, true);
            outputStream.write(cont.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
