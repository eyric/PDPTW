package cn.edu.bupt.pdptw.algorithm.insertion;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.RequestPositions;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public interface InsertionAlgorithm {

    RequestPositions findBestInsertionPositions(
            PickupRequest pickup, Vehicle vehicle, Configuration configuration);

    boolean insertRequestForVehicle(PickupRequest pickup, Vehicle vehicle, Configuration configuration);

    boolean insertRequestToSolution(PickupRequest pickup, Solution solution, Configuration configuration);
}
