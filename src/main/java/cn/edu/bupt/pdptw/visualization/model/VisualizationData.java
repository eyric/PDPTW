package cn.edu.bupt.pdptw.visualization.model;

import cn.edu.bupt.pdptw.model.Location;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class VisualizationData implements Serializable {
    String truckId;
    Integer maxCapacity;
    Location location;
    Location startLocation;
    List<VisualizationRoute> routes;
}
