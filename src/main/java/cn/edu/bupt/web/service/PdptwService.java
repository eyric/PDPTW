package cn.edu.bupt.web.service;

import cn.edu.bupt.pdptw.Instance;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.visualization.model.VisualizationData;
import cn.edu.bupt.pdptw.visualization.model.VisualizationRoute;
import com.google.gson.Gson;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

@Service("pdptwService")
public class PdptwService {
    public String solve(Configuration configuration) {
        Instance instance = new Instance();
        try {
            Solution solution = instance.solve(configuration);
            List<VisualizationData> visualizationData = new LinkedList<>();
            solution.getVehicles().forEach(vehicle -> {
                List<VisualizationRoute> routes = new LinkedList<>();
                vehicle.getRoute().getRequests().forEach(request -> routes.add(new VisualizationRoute(request.getId(), request.getLocation(), request.getVolume(),
                        request.getTimeWindowStart(), request.getTimeWindowEnd(), request.getServiceTime(),
                        request.getRealizationTime(), request.getArrivalTime(), request.getType(), request.getSibling().getId())));
                visualizationData.add(new VisualizationData(vehicle.getId(), vehicle.getMaxCapacity(),
                        vehicle.getLocation(), vehicle.getStartLocation(), routes));
            });

            Gson gson = new Gson();

            return gson.toJson(visualizationData);
        } catch (IOException | InvalidFileFormatException | ParseException e) {
            e.printStackTrace();
        }
        return "";
    }
}
