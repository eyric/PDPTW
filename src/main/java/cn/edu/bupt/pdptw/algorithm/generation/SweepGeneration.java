package cn.edu.bupt.pdptw.algorithm.generation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class SweepGeneration implements GenerationAlgorithm {

	@Override
	public Solution generateSolution(List<Request> requestPool,
			List<Vehicle> vehicles, Configuration configuration)
			throws IllegalArgumentException {

		if (requestPool == null) {
			throw new IllegalArgumentException("Pool of requests list is set to NULL");
		}
		if (vehicles == null) {
			throw new IllegalArgumentException("Vehicles list is set to NULL");
		}
		
		InsertionAlgorithm insertionAlg = configuration.getAlgorithms().getInsertionAlgorithm();
		Objective objective = configuration.getAlgorithms().getObjective();
		List<Vehicle> usedVehicles = new LinkedList<>();
		Solution result = new Solution(usedVehicles);
		Location warehouseLocation = vehicles.get(0).getLocation();

		requestPool.forEach(r -> r.getLocation().updatePolarAngle(warehouseLocation));

		List<PickupRequest> pickupRequests = requestPool.stream()
				.filter(r -> r.getType() == RequestType.PICKUP)
				.map(r -> (PickupRequest) r)
				.sorted((r1, r2) -> {
					double r1Angle = r1.getLocation().getPolarAngle();
					double r2Angle = r2.getLocation().getPolarAngle();
					
					return Double.compare(r1Angle, r2Angle);
				}).collect(Collectors.toCollection(LinkedList::new));
		
		Iterator<Vehicle> vehiclesIt = vehicles.iterator();
		Iterator<PickupRequest> pickupIt = pickupRequests.iterator();
		PickupRequest pickupWithoutVehicle = null;
		boolean requestsLeft = true;
		
		while (requestsLeft
				&& vehiclesIt.hasNext()) {
			
			boolean insertedSuccessfully = true;
			Vehicle curVehicle = vehiclesIt.next();
			requestsLeft = false;
			
			if (pickupWithoutVehicle != null) {
				insertionAlg.insertRequestForVehicle(
						pickupWithoutVehicle, curVehicle, configuration);
				pickupWithoutVehicle = null;
			}
			
			while (insertedSuccessfully
					&& pickupIt.hasNext()) {
				
				PickupRequest curRequest = pickupIt.next();
				insertedSuccessfully = insertionAlg.insertRequestForVehicle(curRequest, curVehicle, configuration);
				
				if (!insertedSuccessfully) {
					pickupWithoutVehicle = curRequest;
					requestsLeft = true;
				}
			}
			
			usedVehicles.add(curVehicle);
		}
		
		result.setObjectiveValue(objective.calculate(result));
		return result;
	}
}
