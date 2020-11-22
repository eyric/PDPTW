package cn.edu.bupt.pdptw.algorithm.split.algo;

import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

import java.util.ArrayList;
import java.util.List;

public class UnitSplit extends SplitAlgo {

    private static final int DIFF = 9999;


    @Override
    public List<Vehicle> split(List<ClusterResult> clusterResultList) {
        List<Vehicle> vehicles = new ArrayList<>();
        for (ClusterResult result : clusterResultList) {
            List<Request> requestList = new ArrayList<>();
            for (Request request : result.getRequestList()) {
                for (int i = 0; i < request.getVolume(); i++) {
                    Request pick = request.copy();
                    Request delivery = request.getSibling().copy();

                    pick.setVolume(1);
                    pick.setId(pick.getId() + DIFF + i);
                    delivery.setVolume(-1);
                    delivery.setId(delivery.getId() + DIFF + i);
                    pick.setSibling(delivery);
                    delivery.setSibling(pick);
                    requestList.add(pick);
                }
            }

            Solution opt = opt(requestList, result.getVehicleList());
            vehicles.addAll(opt.getVehicles());
        }
        return vehicles;
    }
}
