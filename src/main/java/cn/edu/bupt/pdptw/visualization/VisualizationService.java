package cn.edu.bupt.pdptw.visualization;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.logging.LoggingUtils;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.visualization.model.VisualizationData;
import cn.edu.bupt.pdptw.visualization.model.VisualizationRoute;

import com.google.gson.Gson;

public class VisualizationService {

    public void makeVisualizationData(Solution solution, Configuration configuration) throws IOException {
        List<VisualizationData> visualizationData = new LinkedList<>();
        solution.getVehicles().forEach(vehicle -> {
            List<VisualizationRoute> routes = new LinkedList<>();
            vehicle.getRoute().getRequests().forEach(request -> routes.add(new VisualizationRoute(request.getId(), request.getLocation(), request.getVolume(),
                    request.getTimeWindowStart(), request.getTimeWindowEnd(), request.getServiceTime(),
                    request.getRealizationTime(), request.getArrivalTime(), request.getType(),request.getSibling().getId())));
            visualizationData.add(new VisualizationData(vehicle.getId(), vehicle.getMaxCapacity(),
                    vehicle.getLocation(), vehicle.getStartLocation(), routes));
        });

        Gson gson = new Gson();
        String json = gson.toJson(visualizationData);
        String[] requestsPathElements = configuration.getRequestsPath().split("/");
        String fileName = requestsPathElements[requestsPathElements.length - 1]
                + "_" + configuration.getIterations()
                + "_" + configuration.getAlgorithms()
                .getGenerationAlgorithm().getClass().getSimpleName()
                + "_graph.json";

        LoggingUtils.info("Saving solution data under: " + configuration.getOutputPath() + fileName);
        File outputFile = new File(configuration.getOutputPath() + fileName);

        if (!outputFile.exists()) {
            if (!outputFile.createNewFile()) {
                throw new IOException();
            }

        }

        try (
                PrintWriter out = new PrintWriter(outputFile)
        ) {
            out.println(json);
        }
    }

    public void makeVisualizationData(Solution solution, int time, Configuration configuration) throws IOException {
        List<VisualizationData> visualizationData = new LinkedList<>();
        solution.getVehicles().forEach(vehicle -> {
            List<VisualizationRoute> routes = new LinkedList<>();
            vehicle.getRoute().getRequests().forEach(request -> routes.add(new VisualizationRoute(request.getId(), request.getLocation(), request.getVolume(),
                    request.getTimeWindowStart(), request.getTimeWindowEnd(), request.getServiceTime(),
                    request.getRealizationTime(), request.getArrivalTime(), request.getType(),request.getSibling().getId())));
            visualizationData.add(new VisualizationData(vehicle.getId(), vehicle.getMaxCapacity(),
                    vehicle.getLocation(), vehicle.getStartLocation(), routes));
        });

        Gson gson = new Gson();
        String json = gson.toJson(visualizationData);
        String[] requestsPathElements = configuration.getRequestsPath().split("/");
        String fileName = requestsPathElements[requestsPathElements.length - 1]
                + "_" + configuration.getIterations()
                + "_" + configuration.getAlgorithms()
                .getGenerationAlgorithm().getClass().getSimpleName()
                + "_graph_" + time+".json";

        LoggingUtils.info("Saving solution data under: " + configuration.getOutputPath() + fileName);
        File outputFile = new File(configuration.getOutputPath() + fileName);


        if (!outputFile.exists()) {
            if (!outputFile.createNewFile()) {
                throw new IOException();
            }

        }

        try (
                PrintWriter out = new PrintWriter(outputFile)
        ) {
            out.println(json);
        }
    }
}
