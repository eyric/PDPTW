package cn.edu.bupt.pdptw.algorithm.removal;

import java.util.List;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.model.RequestPositions;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class WorstRemoval implements RemovalAlgorithm {

	@Override
	public Request removeRequestForVehicle(Vehicle vehicle, Configuration configuration) {
		RequestPositions worstPositions = findBestRemovalPositions(vehicle, configuration);
		
		return vehicle.removeRequest(
				worstPositions.getPickupPosition(), worstPositions.getDeliveryPosition());
	}

	@Override
	public RequestPositions findBestRemovalPositions(Vehicle vehicle,
			Configuration configuration) {
		
		Objective objective = configuration.getAlgorithms().getObjective();
		double minObjective = Integer.MAX_VALUE;
		double newObjective;
		RequestPositions worstPositions = RequestPositions.createDefault();
		Route route = vehicle.getRoute();
		List<PickupRequest> pickupRequests = route.getRequests()
				.stream()
				.filter(r -> r.getType() == RequestType.PICKUP
						&& !vehicle.getServedRequestsIds().contains(r.getId()))
				.map(r -> (PickupRequest) r)
				.collect(Collectors.toList());

		for (PickupRequest pickup : pickupRequests) {
			RequestPositions positions = vehicle.removeRequest(pickup);
			newObjective = objective.calculateForVehicle(vehicle);
			positions.setObjectiveValue(newObjective);
			
			vehicle.insertRequest(pickup, positions.getPickupPosition(), positions.getDeliveryPosition());
			
			if (newObjective < minObjective) {
				worstPositions = positions;
			}
		}
		
		return worstPositions;
	}

	@Override
	public Request removeRequestFromSolution(Solution solution,
			Configuration configuration) {
		
		double minValue = Integer.MAX_VALUE;
		RequestPositions worstPosition = RequestPositions.createDefault();
		Vehicle worstVehicle = null;
		Request worstRequest = null;
		
		for (Vehicle vehicle : solution.getVehicles()) {
			RequestPositions curPosition = 
					findBestRemovalPositions(vehicle, configuration);	
			
			if (curPosition.getObjectiveValue() < minValue) {
				worstPosition = curPosition;
				worstVehicle = vehicle;
				minValue = curPosition.getObjectiveValue();
			}
		}
		
		if (minValue < Integer.MAX_VALUE) {
			worstRequest = worstVehicle.removeRequest( 
					worstPosition.getPickupPosition(), worstPosition.getDeliveryPosition());
		}
		
		return worstRequest;
	}

}
