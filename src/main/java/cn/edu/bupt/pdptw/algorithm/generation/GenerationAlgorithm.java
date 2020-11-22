package cn.edu.bupt.pdptw.algorithm.generation;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

import java.util.List;

public interface GenerationAlgorithm {
    Solution generateSolution(List<Request> requestPool, List<Vehicle> vehicles,
                              Configuration configuration) throws IllegalArgumentException;
}
