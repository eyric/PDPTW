package cn.edu.bupt.pdptw.algorithm.optimization;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.algorithm.removal.RemovalAlgorithm;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.logging.LoggingUtils;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class TabuOptimization implements OptimizationAlgorithm {
	private Solution solution;
	private AdaptiveMemory adaptiveMemory;
	private Configuration configuration;
	private AtomicBoolean shouldStop = new AtomicBoolean(false);
	
	@Override
	public Solution optimize() {
		LoggingUtils.info("Tabu optimization started (" + solution.getRequests().size() + " requests)");
		
		this.shouldStop.set(false);
		
		final int MAX_ITERATIONS = configuration.getIterations();
		solution.updateOjectiveValue( configuration.getAlgorithms().getObjective());
		Solution curSolution = solution;
		Solution bestSolution = solution;
		adaptiveMemory.addSolution(bestSolution);
		TabuList tabu = new TabuList(1000, configuration);
		final int RANDOM_CREATION_RATE = MAX_ITERATIONS / 10;
		final int TABU_STATUS_DURATION = MAX_ITERATIONS / 10;
		
		for (int i = 0; i < MAX_ITERATIONS && !shouldStop.get() ; i++) {
			if (i % RANDOM_CREATION_RATE == 0 && i != 0) {
				curSolution = adaptiveMemory.createRandomSolution(0.65, 3);
			}
			
			final int iterationNo = i;
			Optional<Solution> bestNeighbor = generateNeighbors(curSolution, 15, 20, configuration)
					.stream()
					.filter(n -> !tabu.isForbiddenByObjective(n, iterationNo)).min(Comparator.comparingDouble(Solution::getObjectiveValue));
			
			
			if (bestNeighbor.isPresent()) {
				curSolution = bestNeighbor.get();
				tabu.setSolutionAsTabu(bestNeighbor.get(), i + TABU_STATUS_DURATION);
				adaptiveMemory.addSolution(bestNeighbor.get());
				
				if (bestNeighbor.get().getObjectiveValue() 
						< bestSolution.getObjectiveValue()) {
					bestSolution = bestNeighbor.get();
					LoggingUtils.info("New best solution found: " + bestSolution.getObjectiveValue());
				}
			}
			
			tabu.update(i);
			adaptiveMemory.update();
		}
		
		LoggingUtils.info("Optimization finished. Best found solution: " 
				+ bestSolution.getObjectiveValue());
		LoggingUtils.info("Number of used vehicles: " + bestSolution.getVehicles().size());
		this.solution = bestSolution;
		
		return bestSolution;
	}
	
	public static List<Solution> generateNeighbors(Solution solution, int n, int maxChainLength, Configuration configuration) {
		List<Solution> neighbors = new LinkedList<>();
		InsertionAlgorithm insertion = configuration.getAlgorithms().getInsertionAlgorithm();
		RemovalAlgorithm removal = configuration.getAlgorithms().getRemovalAlgorithm();
		Objective objective = configuration.getAlgorithms().getObjective();
		
		for (int i = 0; i < n; i++) {
			Solution neighbor = solution.copy(); 
			List<Vehicle> vehicles = neighbor.getVehicles();
			Vehicle prevVehicle = ListUtils.getRandomElement(vehicles);
			Request prevEjected = removal.removeRequestForVehicle(prevVehicle, configuration);
			Request curEjected;
			PickupRequest pickupToInsert;
			boolean insertedSuccessfully;
			
			for (int j = 0; j < maxChainLength; j++) {
				Vehicle curVehicle = ListUtils.getRandomElement(vehicles);
				
				if (curVehicle.getRoute().getRequests().size() > 0) {

					Route routeCopy = curVehicle.getRoute().copy();
					curEjected = removal.removeRequestForVehicle(curVehicle, configuration);
					pickupToInsert = (PickupRequest) 
							((prevEjected.getType() == RequestType.PICKUP) 
							? prevEjected
							: prevEjected.getSibling());
				
					insertedSuccessfully = insertion.insertRequestForVehicle(
							pickupToInsert, curVehicle, configuration);
					
					if (!insertedSuccessfully) {
						curVehicle.setRoute(routeCopy);
						
					} else {
						prevVehicle = curVehicle;
						prevEjected = curEjected;
					}
				}
			}

			pickupToInsert = (PickupRequest) 
					((prevEjected.getType() == RequestType.PICKUP) 
					? prevEjected
					: prevEjected.getSibling());
			
			Iterator<Vehicle> it = vehicles.iterator();
			insertedSuccessfully = false;
			
			while (it.hasNext() && !insertedSuccessfully) {
				insertedSuccessfully = insertion.insertRequestForVehicle(
						pickupToInsert, prevVehicle, configuration);
				prevVehicle = it.next();
			}
			
			if (insertedSuccessfully) {
				it = vehicles.iterator();
				
				while (it.hasNext()) {
					Vehicle v = it.next();
					
					if (v.getRoute().getRequests().size() == 0) {
						it.remove();
					}
				}
				
				neighbor.setObjectiveValue(objective.calculate(neighbor));
				neighbors.add(neighbor);
			}
		}
		
		return neighbors;
	}

	@Override
	public synchronized Solution getSolution() {
		return this.solution;
	}

	@Override
	public synchronized AdaptiveMemory getAdaptiveMemory() {
		return this.adaptiveMemory;
	}

	@Override
	public OptimizationAlgorithm setConfiguration(Configuration configuration) {
		this.configuration = configuration;
		return this;
	}

	@Override
	public OptimizationAlgorithm setSolution(Solution solution) {
		this.solution = solution;
		return this;
	}

	@Override
	public OptimizationAlgorithm setAdaptiveMemory(AdaptiveMemory adaptiveMemory) {
		this.adaptiveMemory = adaptiveMemory;
		return this;
	}

	@Override
	public void stopOptimization() {
		shouldStop.set(true);
	}

	@Override
	public OptimizationAlgorithm createShallowCopy() {
		return new TabuOptimization()
						.setConfiguration(configuration)
						.setSolution(solution)
						.setAdaptiveMemory(adaptiveMemory);
	}



}
		