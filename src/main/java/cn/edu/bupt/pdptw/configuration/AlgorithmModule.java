package cn.edu.bupt.pdptw.configuration;

import java.util.HashMap;
import java.util.Map;

import cn.edu.bupt.pdptw.algorithm.decomposition.DecompositionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.decomposition.SweepDecomposition;
import cn.edu.bupt.pdptw.algorithm.generation.GenerationAlgorithm;
import cn.edu.bupt.pdptw.algorithm.generation.GreedyGeneration;
import cn.edu.bupt.pdptw.algorithm.generation.SectorBasedGeneration;
import cn.edu.bupt.pdptw.algorithm.generation.SweepGeneration;
import cn.edu.bupt.pdptw.algorithm.insertion.GreedyInsertion;
import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.insertion.RegretInsertion;
import cn.edu.bupt.pdptw.algorithm.objective.*;
import cn.edu.bupt.pdptw.algorithm.optimization.OptimizationAlgorithm;
import cn.edu.bupt.pdptw.algorithm.optimization.TabuOptimization;
import cn.edu.bupt.pdptw.algorithm.removal.RandomRemoval;
import cn.edu.bupt.pdptw.algorithm.removal.RemovalAlgorithm;
import cn.edu.bupt.pdptw.algorithm.removal.ShawRemoval;
import cn.edu.bupt.pdptw.algorithm.removal.WorstRemoval;
import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.algorithm.scheduling.Scheduler;
import lombok.AllArgsConstructor;

import com.google.inject.AbstractModule;

@AllArgsConstructor
public class AlgorithmModule extends AbstractModule {
    private final AlgorithmDescription description;

    private static final Map<String, Class<? extends GenerationAlgorithm>>
            generationAlgorithms = new HashMap<>();
    private static final Map<String, Class<? extends InsertionAlgorithm>>
            insertionAlgorithms = new HashMap<>();
    private static final Map<String, Class<? extends RemovalAlgorithm>>
            removalAlgorithms = new HashMap<>();
    private static final Map<String, Class<? extends OptimizationAlgorithm>>
            optimizationAlgorithms = new HashMap<>();
    private static final Map<String, Class<? extends Objective>>
            objectives = new HashMap<>();
    private static final Map<String, Class<? extends Scheduler>>
            schedulers = new HashMap<>();
    private static final Map<String, Class<? extends DecompositionAlgorithm>>
            decompositionAlgorithms = new HashMap<>();

    static {

        /* generation algorithms */
        generationAlgorithms.put("greedy", GreedyGeneration.class);
        generationAlgorithms.put("sweep", SweepGeneration.class);
        generationAlgorithms.put("sector", SectorBasedGeneration.class);

        /* insertion algorithms */
        insertionAlgorithms.put("greedy", GreedyInsertion.class);
        insertionAlgorithms.put("regret", RegretInsertion.class);

        /* removal algorithms */
        removalAlgorithms.put("random", RandomRemoval.class);
        removalAlgorithms.put("worst", WorstRemoval.class);
        removalAlgorithms.put("shaw", ShawRemoval.class);

        /* optimization algorithms */
        optimizationAlgorithms.put("tabu", TabuOptimization.class);

        /* objective functions*/
        objectives.put("total_distance", TotalDistanceObjective.class);
        objectives.put("total_vehicles", TotalVehiclesObjective.class);
        objectives.put("total_profit", TotalProfitObjective.class);
        objectives.put("total_time", TotalTimeObjective.class);

        /* scheduling algorithms */
        schedulers.put("drive_first", DriveFirstScheduler.class);

        /* decomposition algorithms */
        decompositionAlgorithms.put("sweep", SweepDecomposition.class);
    }

    @Override
    protected void configure() throws IllegalArgumentException {
        boolean algorithmNamesValid = true;
        final String ERROR_MESSAGE_PATTERN = "\nInvalid %s algorithm name";
        StringBuilder builder = new StringBuilder();

        /* check whether all of the
         * passed algorithms names
         * are present in the corresponding
         * maps */

        algorithmNamesValid = generationAlgorithms.containsKey(description.getGenerationAlgorithmName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "generation"));
        }
        algorithmNamesValid = insertionAlgorithms.containsKey(description.getInsertionAlgorithmName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "insertion"));
        }
        algorithmNamesValid = removalAlgorithms.containsKey(description.getRemovalAlgorithmName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "removal"));
        }
        algorithmNamesValid = optimizationAlgorithms.containsKey(description.getOptimizationAlgorithmName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "optimization"));
        }
        algorithmNamesValid = objectives.containsKey(description.getObjectiveName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "objective"));
        }
        algorithmNamesValid = schedulers.containsKey(description.getSchedulerName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "scheduling"));
        }
        algorithmNamesValid = decompositionAlgorithms.containsKey(description.getDecompositionAlgorithmName());
        if (!algorithmNamesValid) {
            builder.append(String.format(ERROR_MESSAGE_PATTERN, "decomposition"));
        }

        if (!algorithmNamesValid) {
            throw new IllegalArgumentException(builder.toString());
        }

        bind(GenerationAlgorithm.class).to(
                generationAlgorithms.get(description.getGenerationAlgorithmName()));
        bind(InsertionAlgorithm.class).to(
                insertionAlgorithms.get(description.getInsertionAlgorithmName()));
        bind(RemovalAlgorithm.class).to(
                removalAlgorithms.get(description.getRemovalAlgorithmName()));
        bind(OptimizationAlgorithm.class).to(
                optimizationAlgorithms.get(description.getOptimizationAlgorithmName()));
        bind(Objective.class).to(
                objectives.get(description.getObjectiveName()));
        bind(Scheduler.class).to(
                schedulers.get(description.getSchedulerName()));
        bind(DecompositionAlgorithm.class).to(
                decompositionAlgorithms.get(description.getDecompositionAlgorithmName()));
    }

}
