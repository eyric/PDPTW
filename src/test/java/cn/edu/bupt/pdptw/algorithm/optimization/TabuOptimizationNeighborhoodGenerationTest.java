package cn.edu.bupt.pdptw.algorithm.optimization;

import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class TabuOptimizationNeighborhoodGenerationTest {

	@Test
	public void test() {
		Solution solution = DataGenerator.generateSolution(10);
		System.out.println(solution + "\n\n\n");
		Vehicle.setScheduler(new DriveFirstScheduler());
		Configuration configuration = DataGenerator.generateConfiguration();
		TabuOptimization.generateNeighbors(solution, 3, 10, configuration);
	}
}
