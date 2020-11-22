package cn.edu.bupt.pdptw.test.util;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
//import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ModifySolomonData {
    private static Random random = new Random(System.currentTimeMillis());

    public static String modify(String name) throws IOException, InvalidFileFormatException {
        DefaultConfigReader reader = new DefaultConfigReader();
        Configuration cfg = Configuration.defaultCfg(name);
        List<Request> requests = reader.loadRequests(cfg);
        List<PickupRequest> pickups = requests.stream()
                .filter(r -> r.getType() == RequestType.PICKUP)
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        StringBuilder result = new StringBuilder();
        for (PickupRequest request : pickups) {
            double distance = Location.calculateDistance(request.getLocation(), request.getSibling().getLocation());
            int amount = (int) (distance * 2.618 + request.getVolume() * 0.618);

            int arriveTime;
            float flag = random.nextFloat();
            if (flag < 0.2) {
                arriveTime = 0;
            } else if (flag >= 0.2 && flag < 0.4) {
                arriveTime = (int) (flag * 100);
            } else if (flag >= 0.4 && flag < 0.6) {
                arriveTime = 100 + (int) (flag * 100);
            } else if (flag >= 0.6 && flag < 0.8) {
                arriveTime = 200 + (int) (flag * 100);
            } else if (flag >= 0.8 && flag < 0.9) {
                arriveTime = 300 + (int) (flag * 100);
            } else if (flag >= 0.9 && flag < 0.95) {
                arriveTime = 400 + (int) (flag * 100);
            } else {
                arriveTime = 0;
            }


            result.append(arriveTime).append("\n");
            System.out.println(amount + "\t" + arriveTime);
        }
        return result.toString();
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
                            String modify = modify(filePath);
                            String output = file2.getPath() + ".arrival_time";
                            new File(output+"s").delete();
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
            outputStream = new FileOutputStream(file,true);
            outputStream.write(cont.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
