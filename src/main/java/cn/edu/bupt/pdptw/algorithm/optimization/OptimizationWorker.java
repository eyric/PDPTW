package cn.edu.bupt.pdptw.algorithm.optimization;

import lombok.Data;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;

@Data
public class OptimizationWorker implements Runnable {
	private OptimizationAlgorithm optimization;
	private Solution solution;
	private AdaptiveMemory adaptiveMemory;
	private Configuration configuration;
	
	public OptimizationWorker(Solution solution, Configuration configuration) {
		this.solution = solution;
		this.configuration = configuration;
		this.optimization = configuration.getAlgorithms()
				.getOptimizationAlgorithm()
				.createShallowCopy();
		this.optimization.setConfiguration(configuration);
		this.optimization.setSolution(solution);
	}
	
	@Override
	public void run() {
		optimization.setAdaptiveMemory(new AdaptiveMemory(32, configuration));
		optimization.optimize();
		solution = optimization.getSolution();
		adaptiveMemory = optimization.getAdaptiveMemory();
	}
	
	public synchronized void stopOptimization() {
		optimization.stopOptimization();
	}
}
