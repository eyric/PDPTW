package cn.edu.bupt.pdptw.algorithm.removal;

import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;
import cn.edu.bupt.pdptw.test.util.DataGenerator;
import org.junit.Test;

public class WorstRemovalTest {
	
	@Test
	public void test() {
		Solution s = DataGenerator.generateSolution(1);
		System.out.println("Before: " + s);
		RemovalAlgorithm alg = new WorstRemoval();
		Configuration config = DataGenerator.generateConfiguration();
		
		Vehicle.setScheduler(new DriveFirstScheduler());
		System.out.println("Worst position: " + alg.findBestRemovalPositions(
				s.getVehicles().get(0), config));
		Request pickup = alg.removeRequestFromSolution(s, config);
		System.out.println("Removed: " + pickup);
		System.out.println("\n\nAfter: " + s);
	}

}
