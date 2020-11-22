package cn.edu.bupt.pdptw.algorithm.decomposition;

import java.util.*;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class SweepDecomposition implements DecompositionAlgorithm {
	
	@Override
	public List<Solution> decompose(Solution solution,
			Configuration configuration) {
		
		final int MAX_VEHICLES = configuration.getMaxVehiclesInGroup();
		List<Vehicle> vehicles = solution.getVehicles();
		Location warehouseLocation = vehicles.get(0).getStartLocation();
		Map<Vehicle, Double> anglesForVehicles = new HashMap<>();
		vehicles.forEach(v -> anglesForVehicles.put(v,
				Location.calculatePolarAngle(warehouseLocation,
				Location.findCentroid(v.getRoute()
						.getRequests()
						.stream()
						.map(Request::getLocation)
						.collect(Collectors.toList())
				))));

		vehicles = vehicles.stream()
				.sorted(Comparator.comparing(anglesForVehicles::get))
				.collect(Collectors.toList());
		
		List<Solution> solutions = new ArrayList<>(solution.getRequests().size() / MAX_VEHICLES);
		double startAngle = (Math.random() * (2 * Math.PI));
		double curAngle = -1.0;
		Iterator<Vehicle> it = vehicles.iterator();
		Vehicle startVehicle = null;
		int skipped = 0;

		while (it.hasNext() && curAngle < startAngle) {
			startVehicle = it.next();
			curAngle = anglesForVehicles.get(startVehicle);
			skipped++;
		}
		
		int vehiclesAddedToCurrentSolution = 1;
		Solution curSolution = new Solution(new LinkedList<>());
		curSolution.getVehicles().add(startVehicle);
		solutions.add(curSolution);
		
		while (it.hasNext()) {
			if (vehiclesAddedToCurrentSolution > MAX_VEHICLES) {
				curSolution = new Solution(new LinkedList<>());
				solutions.add(curSolution);
				vehiclesAddedToCurrentSolution = 0;
			}
			
			curSolution.getVehicles().add(it.next());
			vehiclesAddedToCurrentSolution++;
			
		}

		if (skipped > 0) {
			it = vehicles.iterator();
			int vehiclesIndex = 0;
			
			while (vehiclesIndex < (skipped - 1)) {
				if (vehiclesAddedToCurrentSolution > MAX_VEHICLES) {
					curSolution = new Solution(new LinkedList<>());
					solutions.add(curSolution);
					vehiclesAddedToCurrentSolution = 0;
				}
				
				curSolution.getVehicles().add(it.next());
				vehiclesAddedToCurrentSolution++;
				vehiclesIndex++;
				
			}
		}
		
		return solutions;
	}
}
