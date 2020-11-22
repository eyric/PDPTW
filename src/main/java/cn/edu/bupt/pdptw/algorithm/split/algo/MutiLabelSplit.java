package cn.edu.bupt.pdptw.algorithm.split.algo;

import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

/**
 * 多标记切分
 */
public class MutiLabelSplit extends SplitAlgo {
    //拆分比例
    private double rate;

    public void setRate(double rate) {
        this.rate = rate;
    }

    public List<Vehicle> split(List<ClusterResult> clusterResultList) {
        List<Vehicle> result = new ArrayList<>();
        for (ClusterResult cluster : clusterResultList) {
            try {
                List<Request> requestList = cluster.getRequestList();
                Route opt = ShortestPathOpt.opt(requestList, Configuration.defaultCfg());
                result.addAll(split(opt, cluster.getVehicleList()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private List<Vehicle> split(Route route, List<Vehicle> vehicleList) {
        List<Vehicle> result = new ArrayList<>();
        List<Request> requests = route.getRequests();
        Request start = requests.get(0);
        Vehicle near = near(start, vehicleList);
        Route r = new Route(new ArrayList<>());
        List<Request> requestList = new ArrayList<>();
        double capacity = 0;
        for (Request request : requests) {
            if (capacity > 0 && ((request.getVolume() + capacity) / near.getMaxCapacity() > rate)) {
                int toAdd = (int) (near.getMaxCapacity() * rate - capacity);
                Request copy = request.copy();
                copy.setVolume(toAdd);
                requestList.add(copy);
                r.setRequests(requestList);
                near.setRoute(r);
                result.add(near);

                requestList = new ArrayList<>();
                Request remain = request.copy();
                remain.setVolume(request.getVolume() - toAdd);
                requestList.add(remain);
                capacity = 0;
                near = near(request, vehicleList);
            } else {
                requestList.add(request);
                capacity += request.getVolume();
            }
        }
        return result;
    }

    private Vehicle near(Request request, List<Vehicle> vehicleList) {
        double min = Double.MAX_VALUE;
        Vehicle result = null;
        for (Vehicle vehicle : vehicleList) {
            double distance = BufferArea.distance(request.getLocation(), vehicle.getLocation());
            if (distance < min) {
                min = distance;
                result = vehicle;
            }
        }
        return result;
    }


}
