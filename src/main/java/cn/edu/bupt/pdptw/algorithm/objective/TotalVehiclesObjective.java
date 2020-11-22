package cn.edu.bupt.pdptw.algorithm.objective;

import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class TotalVehiclesObjective implements Objective {

	@Override
	public double calculate(Solution solution) {

		return (double) (int) solution.getVehicles().stream()
				.filter(v -> {
					Route route = v.getRoute();
					return (route.getRequests().size()
							+ route.getRequests().size()) > 0;
				}).count();
	}

	@Override
	public double calculateForVehicle(Vehicle vehicle) {
		return 1;
	}

	@Override
	public boolean compare(Solution s1, Solution s2) {
		return s1.getObjectiveValue() < s2.getObjectiveValue();
	}
}
