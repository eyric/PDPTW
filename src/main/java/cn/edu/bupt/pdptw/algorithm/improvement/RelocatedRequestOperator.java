package cn.edu.bupt.pdptw.algorithm.improvement;

import cn.edu.bupt.pdptw.model.*;

import java.util.List;
import java.util.stream.Collectors;

//线路间
public class RelocatedRequestOperator extends NeighborOperator {
    @Override
    public Solution generate(Solution solution) {
        Vehicle vehicle = solution.getVehicles().get(random.nextInt(solution.getVehicles().size()));
        List<PickupRequest> pickups = vehicle.getRoute().getRequests().stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        PickupRequest request = pickups.get(random.nextInt(vehicle.getRoute().getRequests().size() / 2));

        Vehicle vehicle2 = solution.getVehicles().get(random.nextInt(solution.getVehicles().size()));
        RequestPositions bestInsertionPositions = insertion.findBestInsertionPositions(request, vehicle2, configuration);

        vehicle.removeRequest(request);
        vehicle2.insertRequest(request, bestInsertionPositions.getPickupPosition(), bestInsertionPositions.getDeliveryPosition());
        solution.updateOjectiveValue(configuration.getAlgorithms().getObjective());

        return solution.copy();
    }

}
