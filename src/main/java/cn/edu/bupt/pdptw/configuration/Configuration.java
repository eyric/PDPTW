package cn.edu.bupt.pdptw.configuration;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.simple.parser.ParseException;
import cn.edu.bupt.pdptw.model.Location;

import java.io.IOException;

@Data
@AllArgsConstructor

public class Configuration {
    private String requestsPath;
    private String vehiclesPath;
    private String outputPath;
    private boolean dynamic;
    private boolean actual;
    private int iterations;
    private int decompositionCycles;
    private int iterationsPerDecomposition;
    private int maxVehiclesInGroup;
    private Location warehouseLocation;
    private AlgorithmConfiguration algorithms;

    public static Configuration defaultCfg() {
        Configuration testConfigurations
                = null;
        try {
            testConfigurations = new DefaultConfigReader().loadConfiguration("resources/test/config.json").get(0);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return testConfigurations;
    }

    public static Configuration defaultCfg(String fileName) {
        Configuration testConfigurations
                = null;
        try {
            testConfigurations = new DefaultConfigReader().loadConfiguration("resources/test/config.json").get(0);
            testConfigurations.setRequestsPath("resources/test/data/" + fileName);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return testConfigurations;
    }

    public static Configuration defaultCfg(String rp, String vp, boolean... split) {
        Configuration testConfigurations
                = null;
        try {
            testConfigurations = new DefaultConfigReader().loadConfiguration("resources/test/config.json").get(0);
            if (split.length > 0 && split[0]) {
                testConfigurations.setRequestsPath("resources/test/split/" + rp);
                testConfigurations.setVehiclesPath("resources/test/vehicles/" + vp);
            } else {
                testConfigurations.setRequestsPath("resources/test/data/" + rp);
                testConfigurations.setVehiclesPath("resources/test/vehicles/" + vp);
            }

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return testConfigurations;
    }

    @Override
    public String toString() {
        return "requestsPath: " + requestsPath
                + "\r\nvehiclesPath: " + vehiclesPath
                + "\r\noutputPath: " + outputPath
                + "\r\ndynamic: " + isDynamic()
                + "\r\niterations: " + iterations
                + "\r\ndecompositionCycles: " + decompositionCycles
                + "\r\niterationsPerDecomposition: " + iterationsPerDecomposition
                + "\r\nmaxVehiclesInGroup: " + maxVehiclesInGroup
                + "\r\nalgorithms:"
                + "\r\n\tgeneration: " + algorithms.getGenerationAlgorithm().getClass().getSimpleName()
                + "\r\n\tinsertion: " + algorithms.getInsertionAlgorithm().getClass().getSimpleName()
                + "\r\n\tremoval: " + algorithms.getRemovalAlgorithm().getClass().getSimpleName()
                + "\r\n\toptimization: " + algorithms.getOptimizationAlgorithm().getClass().getSimpleName()
                + "\r\n\tdecomposition: " + algorithms.getDecompositionAlgorithm().getClass().getSimpleName()
                + "\r\n\tobjective: " + algorithms.getObjective().getClass().getSimpleName()
                + "\r\n\tscheduler: " + algorithms.getScheduler().getClass().getSimpleName();
    }
}
