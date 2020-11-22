package cn.edu.bupt.pdptw.algorithm.improvement;

import cn.edu.bupt.pdptw.model.*;

import java.util.List;
import java.util.stream.Collectors;

public class ExchangeInlineOperator extends NeighborOperator{
    @Override
    public Solution generate(Solution solution) {
        Vehicle vehicle = solution.getVehicles().get(random.nextInt(solution.getVehicles().size()));
        List<PickupRequest> pickups = vehicle.getRoute().getRequests().stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        // select the first request
        PickupRequest request1 = pickups.get(random.nextInt(vehicle.getRoute().getRequests().size() / 2));
        RequestPositions bestInsertionPositions = insertion.findBestInsertionPositions(request1, vehicle, configuration);
        vehicle.removeRequest(request1);
        vehicle.insertRequest(request1, bestInsertionPositions.getPickupPosition(), bestInsertionPositions.getDeliveryPosition());

        //select the other request
        PickupRequest request2 = pickups.get(random.nextInt(vehicle.getRoute().getRequests().size() / 2));
        RequestPositions bestInsertionPositions2 = insertion.findBestInsertionPositions(request2, vehicle, configuration);
        vehicle.removeRequest(request2);
        vehicle.insertRequest(request2, bestInsertionPositions2.getPickupPosition(), bestInsertionPositions2.getDeliveryPosition());

        solution.updateOjectiveValue(configuration.getAlgorithms().getObjective());

        return solution.copy();
    }
}
