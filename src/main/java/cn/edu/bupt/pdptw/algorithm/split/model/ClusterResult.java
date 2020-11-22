package cn.edu.bupt.pdptw.algorithm.split.model;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 聚类结果
 */
@Data
@AllArgsConstructor
public class ClusterResult {
    private List<Vehicle> vehicleList;
    private List<Request> requestList;

    public ClusterResult() {
        vehicleList = new ArrayList<>();
        requestList = new ArrayList<>();
    }

    /**
     * 簇内最短路径
     */
    public RequestRoute route() {
        Configuration configuration = Configuration.defaultCfg();
        int itr = configuration.getIterations();

        RequestRoute route = generate();
        Set<RequestRoute> tabuList = new HashSet<>();
        tabuList.add(route);
        for (int i = 0; i < itr; i++) {
            RequestRoute neighbor = neighbor(route);
            while (tabuList.contains(neighbor)) {
                neighbor = neighbor(route);
            }

            if (neighbor.betterThan(route)) {
                route = neighbor;
            }
        }
        return route;
    }

    /**
     * 生成一个可行解
     */
    private RequestRoute generate() {
        List<PickupRequest> pickups = requestList.stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        List<DeliveryRequest> deliverys = requestList.stream()
                .filter(r -> (r.getType() == RequestType.DELIVERY))
                .map(r -> (DeliveryRequest) r)
                .collect(Collectors.toList());

        List<Request> requestList = new ArrayList<>();
        Collections.shuffle(pickups);
        Collections.shuffle(deliverys);
        return new RequestRoute(null);
    }

    /**
     * 生成邻域
     */
    private RequestRoute neighbor(RequestRoute route) {
        return new RequestRoute(null, 0);
    }
}
