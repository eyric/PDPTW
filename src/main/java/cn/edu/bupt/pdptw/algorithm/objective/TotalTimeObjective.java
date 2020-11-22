package cn.edu.bupt.pdptw.algorithm.objective;

import cn.edu.bupt.pdptw.model.*;

import java.util.Iterator;

public class TotalTimeObjective implements Objective {
    private TotalDistanceObjective distanceObjective = new TotalDistanceObjective();

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
            double velocity = vehicle.getType().getVelocity();
            double distance = Location.calculateDistance(
                    prevRequest.getLocation(), vehicle.getStartLocation());
            result += Math.max(prevRequest.getTimeWindowStart(), distance / velocity) + prevRequest.getServiceTime();

            while (it.hasNext()) {
                Request curRequest = it.next();
                distance = Location.calculateDistance(
                        prevRequest.getLocation(), curRequest.getLocation());

                result += Math.max(prevRequest.getTimeWindowStart(), result + distance / velocity) + prevRequest.getServiceTime();
                prevRequest = curRequest;
            }

            //return to the distribution center
            result += Math.max(prevRequest.getTimeWindowStart(),
                    result + distance / velocity) + prevRequest.getServiceTime();
        }

        return result;
    }

    @Override
    public boolean compare(Solution s1, Solution s2) {
        return s1.getObjectiveValue() < s2.getObjectiveValue();
    }
}
