package cn.edu.bupt.pdptw.algorithm.optimization;

import java.util.ArrayList;
import java.util.List;

import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class ListCopyTest {

	@Test
	public void test() {
		Vehicle v = DataGenerator.generateVehicle(5);
		List<Request> reqs = new ArrayList<>(v.getRoute().getRequests());
		
		System.out.println(reqs.remove(0));
		System.out.println(v);
		reqs.forEach(r -> System.out.println(r));
	}

}
