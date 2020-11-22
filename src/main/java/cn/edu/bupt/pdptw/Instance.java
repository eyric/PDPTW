package cn.edu.bupt.pdptw;

import cn.edu.bupt.pdptw.algorithm.decomposition.DecompositionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.generation.GenerationAlgorithm;
import cn.edu.bupt.pdptw.algorithm.optimization.AdaptiveMemory;
import cn.edu.bupt.pdptw.algorithm.optimization.OptimizationWorker;
import cn.edu.bupt.pdptw.configuration.AlgorithmConfiguration;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.logging.LoggingUtils;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class Instance {

    private DefaultConfigReader loader = new DefaultConfigReader();

    public Solution solve(Configuration configuration) throws IOException, InvalidFileFormatException, ParseException {
        long start = System.currentTimeMillis();
        DecompositionAlgorithm decomposition = configuration.getAlgorithms().getDecompositionAlgorithm();
        AdaptiveMemory adaptiveMemory = new AdaptiveMemory(32, configuration);

        AlgorithmConfiguration algs = configuration.getAlgorithms();
        GenerationAlgorithm generation = algs.getGenerationAlgorithm();
        List<Request> requests = loader.loadRequests(configuration);
        List<Vehicle> vehicles = loader.loadVehicles(configuration);
        Solution solution = generation.generateSolution(requests, vehicles, configuration);

        final int CYCLES = (configuration.isDynamic()) ? Integer.MAX_VALUE : configuration.getDecompositionCycles() - 1;
        final int ITERATIONS_PER_DECOMPOSITION = configuration.getIterationsPerDecomposition();
        int cyclesCounter = 0;

        while (cyclesCounter < CYCLES) {
            cyclesCounter++;

            for (int i = 0; i < ITERATIONS_PER_DECOMPOSITION; i++) {
                List<OptimizationWorker> workers = new LinkedList<>();
                List<Solution> partialSolutions = decomposition.decompose(solution, configuration);
                ExecutorService executor = Executors.newFixedThreadPool(partialSolutions.size());

                for (Solution s : partialSolutions) {
                    OptimizationWorker worker = new OptimizationWorker(s, configuration);
                    workers.add(worker);
                    executor.execute(worker);
                }

                executor.shutdown();
                try {
                    executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    LoggingUtils.logStackTrace(e);
                }

                solution = new Solution(
                        workers.stream()
                                .map(OptimizationWorker::getSolution)
                                .flatMap(s -> s.getVehicles().stream())
                                .collect(Collectors.toList()));
                double newObjective = workers.stream()
                        .map(OptimizationWorker::getSolution)
                        .mapToDouble(Solution::getObjectiveValue)
                        .sum();

                solution.setObjectiveValue(newObjective);
                LoggingUtils.info("New objective value: " + newObjective);
            }

            LoggingUtils.info("A decomposition cycle has been finished");

            adaptiveMemory.addSolution(solution);
            adaptiveMemory.update();
            solution = adaptiveMemory.createRandomSolution(0.65, 3);
        }

        try {
            solution.updateOjectiveValue(configuration.getAlgorithms().getObjective());
            LoggingUtils.saveResult(solution, configuration);
        } catch (IOException e) {
            e.printStackTrace();
        }

        LoggingUtils.info("Final solution size: " + solution.getVehicles().size());

        solution.setSecond((System.currentTimeMillis() - start) / 1000.0);
        return solution;
    }
}
