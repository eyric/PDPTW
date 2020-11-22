package cn.edu.bupt.pdptw.visualization.model;

import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.RequestType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"})
public class VisualizationRoute implements Serializable {
    Integer id;
    Location location;
    Integer volume;
    Integer timeWindowStart;
    Integer timeWindowEnd;
    Integer serviceTime;
    Integer realizationTime;
    Integer arrivalTime;
    RequestType type;
    Integer sibling;
}
