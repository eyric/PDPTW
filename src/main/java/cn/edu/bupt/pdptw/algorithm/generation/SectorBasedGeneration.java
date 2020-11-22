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

public class SectorBasedGeneration implements GenerationAlgorithm {

	@Override
	public Solution generateSolution(List<Request> requestPool, List<Vehicle> vehicles,
			Configuration configuration)
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
		boolean insertedSuccessfully;
		
		while (pickupRequests.size() > 0
				&& vehiclesIt.hasNext()) {
			
			PickupRequest curRequest = pickupRequests.remove(0); 
			Vehicle curVehicle = vehiclesIt.next();
			insertedSuccessfully = insertionAlg.insertRequestForVehicle(curRequest, curVehicle, configuration);
			Location pickupLocation = curRequest.getLocation();
			Location deliveryLocation = curRequest.getSibling().getLocation();
			
			double lowerBoundary = (pickupLocation.getPolarAngle() 
					<= deliveryLocation.getPolarAngle())
						? pickupLocation.getPolarAngle()
						: deliveryLocation.getPolarAngle();
			double upperBoundary = (lowerBoundary == pickupLocation.getPolarAngle()) 
					? deliveryLocation.getPolarAngle()
					: pickupLocation.getPolarAngle();

			List<PickupRequest> pickupsInSector = pickupRequests.stream()
					.filter(r -> {
						Location p = r.getLocation();
						Location d = r.getSibling().getLocation();
						
						return p.getPolarAngle() >= lowerBoundary
							&& p.getPolarAngle() <= upperBoundary
							&& d.getPolarAngle() >= lowerBoundary
							&& d.getPolarAngle() <= lowerBoundary;
							
 					}).collect(Collectors.toList());
			Iterator<PickupRequest> sectorIt = pickupsInSector.iterator();

			while (sectorIt.hasNext()) {
				PickupRequest pickupToInsert = sectorIt.next();
				insertedSuccessfully = insertionAlg.insertRequestForVehicle(
						pickupToInsert, curVehicle, configuration);
				
				if (insertedSuccessfully) {
					pickupRequests.remove(pickupToInsert);
					sectorIt.remove();
				}
			}

			if (pickupsInSector.size() == 0
					&& insertedSuccessfully) {
				
				double curAngle = curRequest.getLocation().getPolarAngle();
				Iterator<PickupRequest> nearestIt = pickupRequests.stream()
						.sorted((r1, r2) -> {
							Location p1 = r1.getLocation();
							Location p2 = r2.getLocation();
							double diff1 = Math.abs(p1.getPolarAngle() - curAngle);
							double diff2 = Math.abs(p2.getPolarAngle() - curAngle);
							
							return Double.compare(diff1, diff2);
									
						})
						.collect(Collectors.toList())
						.iterator();
				
				while (insertedSuccessfully && nearestIt.hasNext()) {
					PickupRequest nearestRequest = nearestIt.next();
					insertedSuccessfully = insertionAlg.insertRequestForVehicle(
							nearestRequest, curVehicle, configuration);
					if (insertedSuccessfully) {
						pickupRequests.remove(nearestRequest);
					}
				}
			}
			
			usedVehicles.add(curVehicle);
		}
		
		result.setObjectiveValue(objective.calculate(result));
		return result;
	}
	
}
