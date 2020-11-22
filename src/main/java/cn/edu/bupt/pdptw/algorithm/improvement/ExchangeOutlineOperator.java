package cn.edu.bupt.pdptw.algorithm.improvement;

import cn.edu.bupt.pdptw.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeOutlineOperator extends NeighborOperator{
    @Override
    public Solution generate(Solution solution) {
        //select the first vehicle
        Vehicle vehicle = solution.getVehicles().get(random.nextInt(solution.getVehicles().size()));
        List<PickupRequest> pickups = vehicle.getRoute().getRequests().stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        PickupRequest request = pickups.get(random.nextInt(vehicle.getRoute().getRequests().size() / 2));
        RequestPositions bestInsertionPositions = insertion.findBestInsertionPositions(request, vehicle, configuration);

        vehicle.removeRequest(request);
        vehicle.insertRequest(request, bestInsertionPositions.getPickupPosition(), bestInsertionPositions.getDeliveryPosition());

        //select the second vehicle
        Vehicle vehicle2 = solution.getVehicles().get(random.nextInt(solution.getVehicles().size()));
        List<PickupRequest> pickups2 = vehicle.getRoute().getRequests().stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        PickupRequest request2 = pickups2.get(random.nextInt(vehicle2.getRoute().getRequests().size() / 2));
        RequestPositions bestInsertionPositions2 = insertion.findBestInsertionPositions(request2, vehicle2, configuration);

        vehicle.removeRequest(request2);
        vehicle.insertRequest(request2, bestInsertionPositions2.getPickupPosition(), bestInsertionPositions2.getDeliveryPosition());


        solution.updateOjectiveValue(configuration.getAlgorithms().getObjective());

        return solution.copy();
    }
}
