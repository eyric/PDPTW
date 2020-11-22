package cn.edu.bupt.pdptw.algorithm.decomposition;

import java.util.List;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;
import junit.framework.Assert;

import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class PolarAngleDecompositionTest {

	@Test
	public void test() {
		Configuration configuration = DataGenerator.generateConfiguration();
		DecompositionAlgorithm decomposition = new SweepDecomposition();
		Solution solution = DataGenerator.generateSolution(10);
		List<Solution> result = decomposition.decompose(solution, configuration);
		
		result.forEach(s -> s.getVehicles().forEach(v -> System.out.println(v.getId())));
		Assert.assertEquals(2, result.size());
		Assert.assertEquals(result.get(0).getVehicles().size(), 5);
		Assert.assertEquals(result.get(1).getVehicles().size(), 5);
	}

}
