package cn.edu.bupt.pdptw.algorithm.decomposition;

import java.util.List;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;

public interface DecompositionAlgorithm {
	List<Solution> decompose(Solution solution, Configuration configuration);
}
