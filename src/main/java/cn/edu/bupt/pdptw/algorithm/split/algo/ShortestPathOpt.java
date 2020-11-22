package cn.edu.bupt.pdptw.algorithm.split.algo;

import cn.edu.bupt.pdptw.algorithm.generation.GreedyGeneration;
import cn.edu.bupt.pdptw.algorithm.improvement.OperatorSelector;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ShortestPathOpt {
    public static Route opt(List<Request> requestList, Configuration configuration) {
        Solution solution = generate(requestList);
        for (int i = 0; i < configuration.getIterations(); i++) {
            Solution neighbor = neighbor(solution);
            if (neighbor.betterThan(solution, configuration.getAlgorithms().getObjective())) {
                solution = neighbor;
            }
        }
        return solution.getVehicles().get(0).getRoute();
    }

    private static Solution generate(List<Request> requestList) {
        Vehicle vehicle = new Vehicle("0", 999999, requestList.get(0).getLocation());
        List<Request> requests = new ArrayList<>();
        List<PickupRequest> pickups = requestList.stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        for (PickupRequest pr : pickups) {
            requests.add(pr);
            requests.add(pr.getSibling());
        }

        Route route = new Route(requests);
        vehicle.setRoute(route);
        return new Solution(Collections.singletonList(vehicle));
    }

    private static Solution neighbor(Solution solution) {
        return OperatorSelector.select().generate(solution);
    }

    public static void main(String[] args) throws IOException, InvalidFileFormatException {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg("pdptw100/lc101.txt");
        List<Request> requests = loader.loadRequests(configuration);
        Route opt = ShortestPathOpt.opt(requests, configuration);
        System.out.println(opt.getRequests().size());
    }
}
