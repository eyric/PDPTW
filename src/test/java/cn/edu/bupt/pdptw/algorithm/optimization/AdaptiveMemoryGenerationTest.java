package cn.edu.bupt.pdptw.algorithm.optimization;

import java.util.List;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;
import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class AdaptiveMemoryGenerationTest {

	@Test
	public void test() {
		Solution s = DataGenerator.generateSolution(5);
		Configuration conf = DataGenerator.generateConfiguration();
		List<Solution> neigh = TabuOptimization.generateNeighbors(s, 10, 5, conf);
		System.out.println("neighbors: " + neigh.size());
		AdaptiveMemory mem = new AdaptiveMemory(1000, conf);
		mem.addSolution(s);
		neigh.forEach(mem::addSolution);
		
		Solution generated = mem.createRandomSolution(0.5, 1);
		System.out.println(s.getRequests().size());
		System.out.println(generated.getRequests().size());
	}

}
