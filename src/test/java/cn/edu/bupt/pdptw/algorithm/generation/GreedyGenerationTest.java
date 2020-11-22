package cn.edu.bupt.pdptw.algorithm.generation;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class GreedyGenerationTest {
    public static void main(String[] args) throws IOException, InvalidFileFormatException, ParseException {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg();
        List<Request> requests = loader.loadRequests(configuration);
        List<Vehicle> vehicles = loader.loadVehicles(configuration);

        GenerationAlgorithm generationAlgorithm = new SweepGeneration();
        Solution solution = generationAlgorithm.generateSolution(requests, vehicles, configuration);
        solution.print(configuration.getAlgorithms().getObjective());
    }
}
