package cn.edu.bupt.pdptw.algorithm.agmoipso.model;


import lombok.Data;
import org.uma.jmetal.solution.Solution;

import java.util.List;

@Data
public class Result {
    private double seconds;
    private Solution gbest;
    private List<Solution> pbests;
}
