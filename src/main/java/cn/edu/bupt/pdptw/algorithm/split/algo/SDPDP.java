package cn.edu.bupt.pdptw.algorithm.split.algo;

import cn.edu.bupt.pdptw.algorithm.split.model.AlgoPara;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.*;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SD-PDP(pickup and delivery problem with split delivery)
 */
public class SDPDP {
    private BufferClustering clustering = new BufferClustering();
    private SplitAlgo splitAlgo = new MutiLabelSplit();

    public Solution solve(List<Vehicle> vehicleList, List<Request> requestList, AlgoPara para) {
        //基于缓冲区聚类
        List<ClusterResult> cluster = clustering.clustering(vehicleList, requestList, para);
        List<Vehicle> vehicles = splitAlgo.split(cluster);
        Solution solution = new Solution(vehicles);
        solution.updateOjectiveValue(Configuration.defaultCfg().getAlgorithms().getObjective());
        return solution;
    }

    public static AlgoPara para(List<Request> requestList, List<Vehicle> vehicleList) {
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

        //客户订单取货点集合
        List<PickupRequest> pickups = requestList.stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        int size = pickups.size();
        double a = 0, b = 0, c = 0, d = 0;
        for (int i = 0; i < size; i++) {
            Request ri = pickups.get(i);
            for (int j = 0; j < size; j++) {
                if (i != j) {
                    Request rj = pickups.get(j);

                    a += BufferArea.distance(ri.getLocation(), rj.getLocation());
                    b += BufferArea.distance(ri.getLocation(), rj.getSibling().getLocation());
                    c += BufferArea.pointToLine(rj.getLocation(), rj.getSibling().getLocation(), ri.getLocation());
                }
            }

            for (Vehicle vehicle : vehicleList) {
                d += BufferArea.distance(ri.getLocation(), vehicle.getLocation());
            }
        }

        double pp = (maxLat - minLat + maxLng - minLng) / 20;
        double sp = size * (size - 1) * pp;

        AlgoPara para = new AlgoPara();
        para.setAlpha(a / sp);
        para.setAlpha(b / sp);
        para.setAlpha(c / sp);
        para.setAlpha(d / sp);

        return para;
    }

    public static void main(String[] args) throws IOException, InvalidFileFormatException, ParseException {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg("pdptw100\\lc101.txt","3\\pdptw100\\lc102.json");
        List<Request> requests = loader.loadRequests(configuration);
        List<Vehicle> vehicles = loader.loadVehicles(configuration);

        AlgoPara para = para(requests, vehicles);
        SDPDP sdpdp = new SDPDP();
        Solution solution = sdpdp.solve(vehicles, requests, para);
        System.out.println(solution.getObjectiveValue());

    }

    public static void run() throws IOException, InvalidFileFormatException, ParseException {
        String path = FileUtil.basePath() + "/resources/test/data/";
        String[] folder = new String[]{"pdptw100", "pdptw200", "pdptw400", "pdptw600", "pdptw800", "pdptw1000"};


        for (String f : folder) {
            List<String> strings = traverseFolder(path + f);
            for (String s : strings) {
                StringBuilder cont = new StringBuilder(s.substring(0, s.length() - 4) + "\t");

                for (int i = 0; i < 5; i++) {
                    DefaultConfigReader loader = new DefaultConfigReader();
                    Configuration configuration = Configuration.defaultCfg(f + "/" + s, "3/" + f + "/" + s.substring(0, s.length() - 3) + "json");
                    List<Request> requests = loader.loadRequests(configuration);
                    List<Vehicle> vehicles = loader.loadVehicles(configuration);

                    AlgoPara para = para(requests, vehicles);

                    SDPDP sdpdp = new SDPDP();
                    Solution solution = sdpdp.solve(vehicles, requests, para);
                    cont.append(solution.getObjectiveValue()).append("\t").append(solution.getVehicles().size()).append("\t").append(solution.getRate()).append("\t");
                }

                cont.append("\n");

                FileUtil.writeFile("D:\\code\\java\\PDPTW\\resources\\test\\li_lim_benchmark\\results\\split\\result.txt", cont.toString());
            }

        }
    }

    public static List<String> traverseFolder(String path) {
        List<String> result = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return Collections.emptyList();
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        List<String> folder = traverseFolder(file2.getAbsolutePath());
                        result.addAll(folder);
                    } else {
                        String name = file2.getName();
                        if (name.endsWith(".txt")) {
                            result.add(name);
                        }

                    }
                }
            }
        }
        return result;
    }
}
