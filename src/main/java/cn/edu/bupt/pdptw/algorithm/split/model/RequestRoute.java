package cn.edu.bupt.pdptw.algorithm.split.model;

import cn.edu.bupt.pdptw.algorithm.split.algo.BufferClustering;
import cn.edu.bupt.pdptw.model.Request;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
public class RequestRoute {
    List<Request> requestList;

    private double distance;

    public RequestRoute(List<Request> requestList) {
        this.requestList = requestList;
        updateDistance();
    }

    public boolean betterThan(RequestRoute route) {
        return distance < route.getDistance();
    }

    public void updateDistance() {
        double total = 0;
        Request prev = requestList.get(0);
        for (int i = 1; i < requestList.size(); i++) {
            Request curr = requestList.get(i);
            total += BufferClustering.getDistance(prev.getLocation(),
                    curr.getLocation());
            prev = curr;
        }
        this.distance = total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RequestRoute route = (RequestRoute) o;
        return Objects.equals(requestList, route.requestList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestList);
    }
}
