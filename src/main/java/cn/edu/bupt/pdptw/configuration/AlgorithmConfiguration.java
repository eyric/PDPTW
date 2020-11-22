package cn.edu.bupt.pdptw.configuration;

import cn.edu.bupt.pdptw.algorithm.decomposition.DecompositionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.decomposition.SweepDecomposition;
import cn.edu.bupt.pdptw.algorithm.generation.GenerationAlgorithm;
import cn.edu.bupt.pdptw.algorithm.generation.SweepGeneration;
import cn.edu.bupt.pdptw.algorithm.insertion.GreedyInsertion;
import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.algorithm.objective.TotalDistanceObjective;
import cn.edu.bupt.pdptw.algorithm.optimization.OptimizationAlgorithm;
import cn.edu.bupt.pdptw.algorithm.optimization.TabuOptimization;
import cn.edu.bupt.pdptw.algorithm.removal.RemovalAlgorithm;
import cn.edu.bupt.pdptw.algorithm.removal.WorstRemoval;
import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.algorithm.scheduling.Scheduler;
import com.google.inject.Inject;

import lombok.Value;

@Value
public class AlgorithmConfiguration {
	GenerationAlgorithm generationAlgorithm;
	InsertionAlgorithm insertionAlgorithm;
	RemovalAlgorithm removalAlgorithm;
	OptimizationAlgorithm optimizationAlgorithm;
	Objective objective;
	Scheduler scheduler;
	DecompositionAlgorithm decompositionAlgorithm;
	
	public static class AlgorithmConfigurationBuilder {
		private GenerationAlgorithm generationAlgorithm = new SweepGeneration();
		private InsertionAlgorithm insertionAlgorithm = new GreedyInsertion();
		private RemovalAlgorithm removalAlgorithm = new WorstRemoval();
		private OptimizationAlgorithm optimizationAlgorithm = new TabuOptimization();
		private Objective objective = new TotalDistanceObjective();
		private Scheduler scheduler = new DriveFirstScheduler();
		private DecompositionAlgorithm decompositionAlgorithm = new SweepDecomposition();
		
		public AlgorithmConfigurationBuilder() {
			
		}

		public AlgorithmConfigurationBuilder setGenerationAlgorithm(
				GenerationAlgorithm generationAlgorithm) {
			this.generationAlgorithm = generationAlgorithm;
			return this;
		}

		public AlgorithmConfigurationBuilder setInsertionAlgorithm(
				InsertionAlgorithm insertionAlgorithm) {
			this.insertionAlgorithm = insertionAlgorithm;
			return this;
		}

		public AlgorithmConfigurationBuilder setRemovalAlgorithm(
				RemovalAlgorithm removalAlgorithm) {
			this.removalAlgorithm = removalAlgorithm;
			return this;
		}

		public AlgorithmConfigurationBuilder setOptimizationAlgorithm(
				OptimizationAlgorithm optimizationAlgorithm) {
			this.optimizationAlgorithm = optimizationAlgorithm;
			return this;
		}

		public AlgorithmConfigurationBuilder setObjective(Objective objective) {
			this.objective = objective;
			return this;
		}

		public AlgorithmConfigurationBuilder setScheduler(Scheduler scheduler) {
			this.scheduler = scheduler;
			return this;
		}
		
		public AlgorithmConfigurationBuilder setDecompositionAlgorithm(DecompositionAlgorithm decomposition) {
			this.decompositionAlgorithm = decomposition;
			return this;
		}
		
		public AlgorithmConfiguration build() {
			return new AlgorithmConfiguration(
					generationAlgorithm,
					insertionAlgorithm,
					removalAlgorithm,
					optimizationAlgorithm,
					objective,
					scheduler,
					decompositionAlgorithm);
		}
	}
	
	@Inject
	public AlgorithmConfiguration(GenerationAlgorithm generationAlgorithm,
			InsertionAlgorithm insertionAlgorithm,
			RemovalAlgorithm removalAlgorithm,
			OptimizationAlgorithm optimizationAlgorithm,
			Objective objective,
			Scheduler scheduler,
			DecompositionAlgorithm decomposition) {
		
		this.generationAlgorithm = generationAlgorithm;
		this.insertionAlgorithm = insertionAlgorithm;
		this.removalAlgorithm = removalAlgorithm;
		this.optimizationAlgorithm = optimizationAlgorithm;
		this.objective = objective;
		this.scheduler = scheduler;
		this.decompositionAlgorithm = decomposition;
	}
	
	public static AlgorithmConfigurationBuilder createBuilder() {
		return new AlgorithmConfigurationBuilder();
	}
	
	public static AlgorithmConfiguration createDefault() {
		return new AlgorithmConfiguration(
				new SweepGeneration(), 
				new GreedyInsertion(), 
				new WorstRemoval(), 
				new TabuOptimization(), 
				new TotalDistanceObjective(), 
				new DriveFirstScheduler(),
				new SweepDecomposition());
	}
}
