package cn.edu.bupt.pdptw.algorithm.optimization;

import cn.edu.bupt.pdptw.model.Solution;
import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class TabuListTest {

	@Test
	public void test() {
		TabuList tabu = new TabuList(100, DataGenerator.generateConfiguration());
		Solution s1 = DataGenerator.generateSolution(10);
		
		for (int i = 0; i < 5000; i++) {
			System.out.println("" + i + ". -> " + tabu.isForbidden(s1, i));
			
			if (!tabu.isForbidden(s1, i)) {
				tabu.setSolutionAsTabu(s1, i + 5);
			}
		}
	}

}
