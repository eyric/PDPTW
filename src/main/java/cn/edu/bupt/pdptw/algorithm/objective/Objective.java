package cn.edu.bupt.pdptw.algorithm.objective;

import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public interface Objective {
	double calculate(Solution solution);
	double calculateForVehicle(Vehicle vehicle);
	boolean compare(Solution s1,Solution s2);
}
