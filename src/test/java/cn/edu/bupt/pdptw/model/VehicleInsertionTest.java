package cn.edu.bupt.pdptw.model;

import cn.edu.bupt.pdptw.algorithm.insertion.GreedyInsertion;
import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.configuration.Configuration;
import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class VehicleInsertionTest {

	@Test
	public void test() {
		Vehicle v = DataGenerator.generateVehicle(5);
		PickupRequest p = new PickupRequest(1000, new Location(2, 2), 500, 0, 250, 5);
		DeliveryRequest d = new DeliveryRequest(1001, new Location(20, 20), -500, 10, 500, 5);
		InsertionAlgorithm insertion = new GreedyInsertion();
		p.setSibling(d);
		d.setSibling(p);
		Configuration configuration = DataGenerator.generateConfiguration();
		System.out.println(v);
		System.out.println("--------------------");
		System.out.println(p);
		System.out.println(d);
		System.out.println("--------------------");
		insertion.insertRequestForVehicle(p, v, configuration);
		System.out.println(v);
		System.out.println("--------------------");
		System.out.println(p);
		System.out.println(d);
	}

}
