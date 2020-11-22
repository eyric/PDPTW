package cn.edu.bupt.pdptw.algorithm.split.algo;

import cn.edu.bupt.pdptw.algorithm.split.model.AlgoPara;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterType;
import cn.edu.bupt.pdptw.algorithm.split.utils.GraphUtil;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 缓冲区聚类
 */
public class BufferClustering {
    public static void main(String[] args) throws IOException, InvalidFileFormatException, ParseException {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg("pdptw100/lc101.txt", "1/pdptw100/lc101.json");
        List<Request> requests = loader.loadRequests(configuration);
        List<Vehicle> vehicles = loader.loadVehicles(configuration);

        BufferClustering clustering = new BufferClustering();
        List<ClusterResult> results = clustering.clustering(vehicles, requests, new AlgoPara());
        System.out.println(results.toString());
    }

    public List<ClusterResult> clustering(List<Vehicle> vehicleList, List<Request> requestList, AlgoPara para) {
        //聚类结果
        List<ClusterResult> clusteringResult = new ArrayList<>();

        //客户订单取货点集合
        List<PickupRequest> pickups = requestList.stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        //计算邻接矩阵
        int size = pickups.size();
        int[][] martrix = new int[size][size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    martrix[i][j] = 0;
                } else {
                    ClusterType clusterType = BufferArea.get(pickups.get(i), pickups.get(j), para);
                    switch (clusterType) {
                        case N:
                            martrix[i][j] = Integer.MAX_VALUE;
                            break;
                        case A:

                        default:
                            martrix[i][j] = 1;
                    }

                }
            }
        }

        //连通图数目为聚类簇数目
        Integer[] vertex = new Integer[size];
        for (int i = 0; i < size; i++) {
            vertex[i] = i;
        }
        GraphUtil<Integer> graphUtil = new GraphUtil<>(vertex, martrix);
        List<List<Integer>> ergodics = graphUtil.ergodic();
        for (List<Integer> ergodic : ergodics) {
            ClusterResult clusterResult = new ClusterResult();
            List<Request> group = new ArrayList<>();
            for (Integer i : ergodic) {
                group.add(pickups.get(i));
            }
            clusterResult.setRequestList(group);
            clusteringResult.add(clusterResult);
        }

        //聚类簇添加车辆
        for (Vehicle vehicle : vehicleList) {
            Location location = vehicle.getLocation();

            double minD = Double.MAX_VALUE;
            int flag = 0;
            int idx = 0;
            for (ClusterResult result : clusteringResult) {
                List<Request> requests = result.getRequestList();
                double min = Double.MAX_VALUE;
                for (Request request : requests) {
                    double distance = BufferArea.distance(location, request.getLocation());
                    if (distance < min) {
                        min = distance;
                    }
                }
                if (min < minD) {
                    flag = idx;
                    minD = min;
                }
                idx++;
            }
            clusteringResult.get(flag).getVehicleList().add(vehicle);
        }

        return clusteringResult;
    }




    /**
     * 地球半径
     */
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 通过经纬度计算直线距离(单位：千米)
     */
    public static double getDistance(Location x, Location y) {
        double lat1 = x.getX();
        double lng1 = x.getY();
        double lat2 = y.getX();
        double lng2 = y.getY();
        /*double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;*/
        return Math.sqrt((lat2 - lat1) * (lat2 - lat1) + (lng2 - lng1) * (lng2 - lng1));
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
