package cn.edu.bupt.pdptw.algorithm.removal;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.RequestPositions;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public interface RemovalAlgorithm {

    /* Note that we shouldn't remove requests whose ids
     * are present the the vehicle.servedRequestsIds set */
    RequestPositions findBestRemovalPositions(Vehicle vehicle, Configuration configuration);

    Request removeRequestForVehicle(Vehicle vehicle, Configuration configuration);

    Request removeRequestFromSolution(Solution solution, Configuration configuration);
}
