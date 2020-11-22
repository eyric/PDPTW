package cn.edu.bupt.pdptw.algorithm.removal;

import java.util.List;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.algorithm.optimization.ListUtils;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestPositions;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class RandomRemoval implements RemovalAlgorithm {

	@Override
	public RequestPositions findBestRemovalPositions(Vehicle vehicle,
			Configuration configuration) {
		
		List<Request> requests = vehicle.getRoute().getRequests();
		List<PickupRequest> pickupRequests = requests
				.stream()
				.filter(r -> r.getType() == RequestType.PICKUP
						&& !vehicle.getServedRequestsIds().contains(r.getId()))
				.map(r -> (PickupRequest) r)
				.collect(Collectors.toList());
		PickupRequest pickup = ListUtils.getRandomElement(pickupRequests);
		
		return new RequestPositions(requests.indexOf(pickup),
				requests.indexOf(pickup.getSibling()));
	}

	@Override
	public Request removeRequestForVehicle(Vehicle vehicle,
			Configuration configuration) {
		RequestPositions positions = findBestRemovalPositions(vehicle, configuration);
		return vehicle.removeRequest(
				positions.getPickupPosition(), positions.getDeliveryPosition());
	}

	@Override
	public Request removeRequestFromSolution(Solution solution,
			Configuration configuration) {
		
		List<Vehicle> vehicles = solution.getVehicles();
		Vehicle randomVehicle = ListUtils.getRandomElement(vehicles);
		
		return removeRequestForVehicle(randomVehicle, configuration);
	}


}
