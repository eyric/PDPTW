package cn.edu.bupt.pdptw.algorithm.removal;

import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;
import cn.edu.bupt.pdptw.test.util.DataGenerator;
import org.junit.Test;

public class RandomRemovalTest {

	@Test
	public void test() {
		Solution s = DataGenerator.generateSolution(6);
		System.out.println("Before: " + s);
		RemovalAlgorithm alg = new RandomRemoval();
		Configuration config = DataGenerator.generateConfiguration();
		
		Vehicle.setScheduler(new DriveFirstScheduler());
		Request pickup = alg.removeRequestFromSolution(s, config);
		System.out.println("Removed: " + pickup);
		System.out.println("\n\nAfter: " + s);
	}

}
