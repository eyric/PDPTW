package cn.edu.bupt.pdptw.algorithm.objective;

import static org.junit.Assert.assertEquals;

import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.junit.Test;

import cn.edu.bupt.pdptw.test.util.DataGenerator;

public class TotalDistanceObjectiveTest {
	private Objective objective = new TotalDistanceObjective();
	
	@Test
	public void calculateForRouteTest() {
		Route route = DataGenerator.generateRoute(10);
		Vehicle vehicle = new Vehicle("truck", 200, new Location(0, 0));
		vehicle.setRoute(route);
		double expected= 9.0;
		double actual = objective.calculateForVehicle(vehicle);
		
		assertEquals(expected, actual, 0.001);
	}
}
