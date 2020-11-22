package cn.edu.bupt.pdptw.algorithm.insertion;

import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.*;

import java.util.List;

public class GreedyInsertion implements InsertionAlgorithm {

    @Override
    public boolean insertRequestForVehicle(PickupRequest pickup, Vehicle vehicle, Configuration configuration) {

        RequestPositions bestPosition =
                findBestInsertionPositions(pickup, vehicle, configuration);
        boolean inserted = false;

        if (bestPosition.getPickupPosition() != Integer.MAX_VALUE
                && bestPosition.getDeliveryPosition() != Integer.MAX_VALUE) {

            vehicle.insertRequest(pickup, bestPosition.getPickupPosition(),
                    bestPosition.getDeliveryPosition());
            inserted = true;
        }

        return inserted;
    }

    @Override
    public RequestPositions findBestInsertionPositions(
            PickupRequest pickup, Vehicle vehicle, Configuration configuration) {

        Objective objective = configuration.getAlgorithms().getObjective();
        int pickupPosition = Integer.MAX_VALUE;
        int deliveryPosition = Integer.MAX_VALUE;
        double minObjective = Integer.MAX_VALUE;
        double newObjective;
        List<Request> requests = vehicle.getRoute().getRequests();
        RequestPositions bestPositions = RequestPositions.createDefault();

        for (int pPos = 0; pPos <= requests.size(); pPos++) {
            for (int dPos = pPos + 1; dPos <= requests.size() + 1; dPos++) {

                if (vehicle.isInsertionPossible(pickup, pPos, dPos)) {
                    vehicle.insertRequest(pickup, pPos, dPos);
                    newObjective = objective.calculateForVehicle(vehicle);
                    vehicle.removeRequest(pickup);

                    if (newObjective < minObjective) {
                        minObjective = newObjective;
                        pickupPosition = pPos;
                        deliveryPosition = dPos;
                    }

                }
            }
        }

        if (pickupPosition != Integer.MAX_VALUE
                && deliveryPosition != Integer.MAX_VALUE) {
            bestPositions = new RequestPositions(pickupPosition, deliveryPosition, minObjective);
        }

        return bestPositions;
    }

    @Override
    public boolean insertRequestToSolution(PickupRequest pickup,
                                           Solution solution, Configuration configuration) {

        double minValue = Integer.MAX_VALUE;
        RequestPositions bestPosition = RequestPositions.createDefault();
        Vehicle bestVehicle = null;
        boolean inserted = false;

        for (Vehicle vehicle : solution.getVehicles()) {
            RequestPositions curPosition =
                    findBestInsertionPositions(pickup, vehicle, configuration);

            if (curPosition.getObjectiveValue() < minValue) {
                bestPosition = curPosition;
                bestVehicle = vehicle;
                minValue = curPosition.getObjectiveValue();
            }
        }

        if (minValue < Integer.MAX_VALUE) {
            bestVehicle.insertRequest(pickup,
                    bestPosition.getPickupPosition(), bestPosition.getDeliveryPosition());
            inserted = true;
        }

        return inserted;
    }

}
