package cn.edu.bupt.pdptw.algorithm.optimization;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;

public interface OptimizationAlgorithm {
	Solution optimize();
	OptimizationAlgorithm setConfiguration(Configuration configuration);
	OptimizationAlgorithm setSolution(Solution solution);
	OptimizationAlgorithm setAdaptiveMemory(AdaptiveMemory adaptiveMemory);
	Solution getSolution();
	AdaptiveMemory getAdaptiveMemory();
	OptimizationAlgorithm createShallowCopy();
	void stopOptimization();
}
