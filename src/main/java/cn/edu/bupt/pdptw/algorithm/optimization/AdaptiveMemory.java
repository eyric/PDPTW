package cn.edu.bupt.pdptw.algorithm.optimization;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import lombok.Getter;
import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

@Getter
public class AdaptiveMemory {
	private static final double MAX_OBJECTIVE_DIFFERENCE = 0.001;
	private List<Solution> solutions;
	private final int SIZE;
	private final Objective objective;
	private final InsertionAlgorithm insertionAlg;
	private final Configuration configuration;
	
	public AdaptiveMemory(int size, Configuration configuration) {
		this.SIZE = size;
		this.solutions = new ArrayList<>(SIZE);
		this.objective = configuration.getAlgorithms().getObjective();
		this.insertionAlg = configuration.getAlgorithms().getInsertionAlgorithm();
		this.configuration = configuration;
	}
	
	public boolean contains(Solution solution) {
		boolean sameSolutionsFound = false;
		Iterator<Solution> solutionsIt = solutions.iterator();
		
		while (solutionsIt.hasNext() && !sameSolutionsFound) {
			
			Solution s = solutionsIt.next();
			
			if (Math.abs(
					solution.getObjectiveValue() - s.getObjectiveValue())
				<= MAX_OBJECTIVE_DIFFERENCE) {
				
				sameSolutionsFound = true;
			}
		}
		
		return sameSolutionsFound;
	}
	
	public synchronized boolean addSolution(Solution solution) {
		boolean success = true;
		
		if (solutions.size() >= SIZE
				|| contains(solution)) {
			success = false;
		} else {
			Solution copy = solution.copy();
			Iterator<Solution> it = solutions.iterator();
			int position = 0;
			while (it.hasNext() 
					&& it.next().getObjectiveValue() < copy.getObjectiveValue()) {
				position++;
			}
			
			if (position < SIZE) {
				solutions.add(position, copy);
			}
		}
		
		return success;
	}
	
	public synchronized boolean removeSolution(Solution solution) {
		boolean present = contains(solution);
		
		if (present) {
			solutions.remove(solution);
		}
		
		return present;
	}
	
	public synchronized Solution removeSolutionOnPosition(int position) {
		return solutions.remove(position);
	}
	
	public synchronized Solution createRandomSolution(double threshold, int iterationsNo) 
			throws IllegalArgumentException {
		
		if (threshold < 0 || threshold >= 1) {
			throw new IllegalArgumentException("Invalid threshold value."
					+ " Should belong to the range [0, 1)");
		}
		if (iterationsNo < 0) {
			throw new IllegalArgumentException("Invalid iterations number."
					+ " Should be non-negative");
		}

		Solution newSolution = null;
		List<Solution> sortedSolutions = new ArrayList<>(solutions);
		sortedSolutions.sort(Comparator.comparingDouble(Solution::getObjectiveValue));

		List<Integer> solutionIndices = new ArrayList<>(sortedSolutions.size());
		Map<Integer, List<Integer>> routeIndicesForSolution = new HashMap<>();
		IntStream.range(0, sortedSolutions.size()).forEach(solutionIndices::add);
		
		for (Integer i : solutionIndices) {
			List<Integer> routeIds = IntStream
					.range(0, sortedSolutions
							.get(i)
							.getRoutes()
							.size())
					.boxed()
					.collect(Collectors.toList());
			
			routeIndicesForSolution.put(i, routeIds);
		}
		
		if (sortedSolutions.size() > 0) {
			Solution firstSolution = sortedSolutions.get(0).copy();
			List<Integer> requestsIds = firstSolution
					.getRequests()
					.stream()
					.map(Request::getId)
					.collect(Collectors.toList());
			List<Vehicle> pickedVehicles = new LinkedList<>();
			Set<Integer> pickedRequestsIds = new HashSet<>();
			
			while (requestsIds.size() > 0 && solutionIndices.size() > 0) {
				int sectorStart = 0;
				int sectorEnd = solutionIndices.size() - 1;
				
				for (int i = 0; i < iterationsNo; i++) {
					if (Math.random() <= threshold) {
						sectorEnd -= (sectorEnd - sectorStart) / 2; 
					} else {
						sectorStart += (sectorEnd - sectorStart) / 2; 
					}
				}

				int diff = sectorEnd - sectorStart;
				int solutionIndex = (int) (sectorStart + Math.random() * diff);
				Solution pickedSolution = sortedSolutions.get(
						solutionIndices.get(solutionIndex));
				List<Integer> routeIndices = 
						routeIndicesForSolution.get(solutionIndices.get(solutionIndex));
				int routeIndex = ListUtils.getRandomElement(routeIndices);
				
				Vehicle pickedVehicle = pickedSolution.getVehicles()
						.get(routeIndex);
				Route pickedRoute = pickedVehicle.getRoute();
				
				boolean uniqueRequests = true;
				Iterator<Request> requestsIt = 
						pickedRoute.getRequests().iterator();
				
				while (requestsIt.hasNext() && uniqueRequests) {
					uniqueRequests = !pickedRequestsIds.contains(requestsIt.next().getId());
				}
				
				if (uniqueRequests) {
					
					Vehicle vehicleCopy = pickedVehicle.copy();
					pickedVehicles.add(vehicleCopy);
					pickedRequestsIds.addAll(pickedVehicle.getRoute().getRequests()
							.stream()
							.map(Request::getId)
							.collect(Collectors.toList()));
							
					requestsIds.removeAll(
							pickedVehicle.getRoute()
								.getRequests()
								.stream()
								.map(Request::getId)
								.collect(Collectors.toList()));
				}

				routeIndices.remove(Integer.valueOf(routeIndex));
				
				if (routeIndices.size() == 0) {
					routeIndicesForSolution.remove(solutionIndices.get(solutionIndex));
					solutionIndices.remove(solutionIndices.get(solutionIndex));
				}
			}
			
			newSolution = new Solution(pickedVehicles);
			
			if (requestsIds.size() > 0) {
				Iterator<PickupRequest> leftRequestsIt = firstSolution.getRequests().stream()
						.filter(r -> r.getType() == RequestType.PICKUP)
						.map(r -> (PickupRequest) r)
						.filter(r -> requestsIds.contains(r.getId()))
						.collect(Collectors.toList())
						.iterator();
				
				boolean insertedSuccessfully = true;
				while (leftRequestsIt.hasNext() && insertedSuccessfully) {
					PickupRequest pickup = leftRequestsIt.next();
					insertedSuccessfully = insertionAlg.insertRequestToSolution(pickup, newSolution, configuration);
				}
				
				if (!insertedSuccessfully) {
					newSolution = sortedSolutions.get(0);
				}
			}
		}
		
		return newSolution;
	}
	
	public void update() {
		while (solutions.size() > SIZE) {
			solutions.remove(solutions.size() - 1);
		}
	}
}
