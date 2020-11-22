package cn.edu.bupt.pdptw.algorithm.objective;

import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

import java.util.Iterator;

public class TotalDistanceObjective implements Objective {

    @Override
    public double calculate(Solution solution) {
        return solution.getVehicles().stream()
                .mapToDouble(this::calculateForVehicle)
                .sum();
    }

    @Override
    public double calculateForVehicle(Vehicle vehicle) {
        double result = 0;
        Iterator<Request> it = vehicle.getRoute().getRequests().iterator();

        if (it.hasNext()) {
            Request prevRequest = it.next();

            result += Location.calculateDistance(
                    prevRequest.getLocation(), vehicle.getStartLocation());

            while (it.hasNext()) {
                Request curRequest = it.next();
                result += Location.calculateDistance(
                        prevRequest.getLocation(), curRequest.getLocation());
                prevRequest = curRequest;
            }

            result += Location.calculateDistance(
                    prevRequest.getLocation(), vehicle.getStartLocation());

        }

        return result;
    }

    @Override
    public boolean compare(Solution s1, Solution s2) {
        return s1.getObjectiveValue() < s2.getObjectiveValue();
    }
}
