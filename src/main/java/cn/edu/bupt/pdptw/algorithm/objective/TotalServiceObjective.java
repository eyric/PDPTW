package cn.edu.bupt.pdptw.algorithm.objective;


import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class TotalServiceObjective implements Objective {
    @Override
    public double calculate(Solution solution) {
        return 0;
    }

    @Override
    public double calculateForVehicle(Vehicle vehicle) {
        return 0;
    }

    @Override
    public boolean compare(Solution s1, Solution s2) {
        return false;
    }
}
