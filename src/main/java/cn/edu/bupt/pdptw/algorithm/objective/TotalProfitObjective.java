package cn.edu.bupt.pdptw.algorithm.objective;

import cn.edu.bupt.pdptw.algorithm.agmoipso.model.CustomType;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class TotalProfitObjective implements Objective {
    private TotalDistanceObjective distanceObjective = new TotalDistanceObjective();

    @Override
    public double calculate(Solution solution) {
        return solution.getVehicles().stream()
                .mapToDouble(this::calculateForVehicle)
                .sum();
    }

    @Override
    public double calculateForVehicle(Vehicle vehicle) {
        double distance = distanceObjective.calculateForVehicle(vehicle);
        double profitSum = vehicle.getRoute().getRequests()
                .stream().filter(r -> r.getType() == RequestType.PICKUP
                        && r.getCustomType() == CustomType.VIP)
                .mapToDouble(Request::getProfit)
                .sum();

        // 利润-变动成本-固定成本
        return profitSum - vehicle.getType().getVarCost() * distance - vehicle.getType().getFixCost();
    }

    @Override
    public boolean compare(Solution s1, Solution s2) {
        return s1.getObjectiveValue() > s2.getObjectiveValue();
    }
}
