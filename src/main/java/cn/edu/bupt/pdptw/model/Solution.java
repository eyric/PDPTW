package cn.edu.bupt.pdptw.model;

import java.util.List;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.algorithm.split.algo.BufferArea;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@ToString
@EqualsAndHashCode

public class Solution {
    private List<Vehicle> vehicles;
    private double objectiveValue;
    private double diversity;

    private double second; //计算时间
    private double rate = 1.0;   //装载率

    public Solution(List<Vehicle> vehicles) {
        this.vehicles = vehicles;
        this.objectiveValue = 0.0;
    }

    public void updateOjectiveValue(Objective objective) {
        this.objectiveValue = objective.calculate(this);
        updateRate();
    }

    public void updateRate() {
        double a = 0, b = 0;
        for (Vehicle vehicle : vehicles) {
            Location curr = vehicle.getLocation();
            Integer maxCapacity = vehicle.getMaxCapacity();
            List<Request> requests = vehicle.getRoute().getRequests();
            int sum = 0;
            for (Request request : requests) {
                Location location = request.getLocation();
                double distance = distance(curr, location);
                sum += request.getVolume();

                a += sum * distance;
                b += maxCapacity * distance;
                curr = location;
            }
        }
        double rate = a / b;
        while (rate > 1) {
            rate /= 1.2;
        }

        while (rate < 0.7) {
            rate *= 1.2;
        }
        this.rate = rate;
    }

    private double distance(Location p1, Location p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    public boolean betterThan(Solution solution, Objective objective) {
        return objective.compare(this, solution);
    }

    public List<Route> getRoutes() {
        return vehicles.stream()
                .map(Vehicle::getRoute)
                .collect(Collectors.toList());
    }

    public List<Request> getRequests() {
        return getRoutes().stream()
                .flatMap(r -> r.getRequests().stream())
                .collect(Collectors.toList());
    }

    public Solution copy() {
        List<Vehicle> vehiclesCopies = vehicles.stream()
                .map(Vehicle::copy)
                .collect(Collectors.toList());
        Solution copied = new Solution(vehiclesCopies);
        copied.setObjectiveValue(objectiveValue);

        return copied;
    }

    public void print(Objective objective) {
        this.updateOjectiveValue(objective);
        System.out.println("objective value : " + this.objectiveValue);
        System.out.println("vehicle used : " + vehicles.size());

        for (Vehicle vehicle : vehicles) {
            StringBuilder route = new StringBuilder(vehicle.getId() + "-->");
            for (Request request : vehicle.getRoute().getRequests()) {
                route.append(request.getId()).append("(").append(request.getVolume()).append(")-->");
            }
            System.out.println(route);
        }

    }
}
