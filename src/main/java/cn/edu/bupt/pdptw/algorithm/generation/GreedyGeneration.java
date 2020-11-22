package cn.edu.bupt.pdptw.algorithm.generation;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class GreedyGeneration implements GenerationAlgorithm {

	@Override
	public Solution generateSolution(List<Request> requestPool,
			List<Vehicle> vehicles, Configuration configuration)
			throws IllegalArgumentException {
		
		InsertionAlgorithm insertion = configuration.getAlgorithms().getInsertionAlgorithm(); 
		Iterator<Vehicle> vehiclesIt = vehicles.iterator();
		Iterator<PickupRequest> pickupsIt = requestPool.stream()
				.filter(r -> r.getType() == RequestType.PICKUP)
				.map(r -> (PickupRequest) r)
				.collect(Collectors.toList())
				.iterator();
		Solution solution = new Solution(new LinkedList<>());
		Vehicle curVehicle = vehiclesIt.next();
		
		while (pickupsIt.hasNext() 
				&& vehiclesIt.hasNext()) {
			
			PickupRequest pickup = pickupsIt.next();
			
			if (!insertion.insertRequestForVehicle(pickup, curVehicle, configuration)) {
				solution.getVehicles().add(curVehicle);
				curVehicle = vehiclesIt.next();
				curVehicle.insertRequest(pickup, 0, 1);
			}
		}
		
		solution.getVehicles().add(curVehicle);
		
		solution.updateOjectiveValue(configuration.getAlgorithms().getObjective());
		
		return solution;
	}


}
