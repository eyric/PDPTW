package cn.edu.bupt.pdptw.algorithm.insertion;

import static org.junit.Assert.assertEquals;

import java.util.List;

import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.test.util.DataGenerator;
import org.junit.Test;

import cn.edu.bupt.pdptw.model.DeliveryRequest;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Vehicle;

public class GreedyInsertionTooHighVolumeTest {
	private final int REQUESTS_NO = 10;
	private Route route = DataGenerator.generateRoute(REQUESTS_NO);
	private InsertionAlgorithm insertionAlg = new GreedyInsertion();
		
	@Test
	public void test() {
		List<Request> pool = route.getRequests();
		Request lastRequest = pool.get(pool.size() - 1);
		
		PickupRequest pickup = new PickupRequest(
				lastRequest.getId() + 1, new Location(lastRequest.getLocation().getX() + 1, 0),
				1000, lastRequest.getTimeWindowEnd() + 20, lastRequest.getTimeWindowEnd() + 100, 50);
		DeliveryRequest delivery = new DeliveryRequest(
				pickup.getId() + 1, new Location(pickup.getLocation().getX() + 1, 0),
				-1000, pickup.getTimeWindowEnd() + 20, pickup.getTimeWindowEnd() + 100, 50);
		
		pickup.setSibling(delivery);
		delivery.setSibling(pickup);
		Vehicle vehicle = new Vehicle("test_truck", 200, new Location(0, 0));
		vehicle.setRoute(route);
		Vehicle.setScheduler(new DriveFirstScheduler());
		Configuration configuration = DataGenerator.generateConfiguration();
		boolean expected = false;
		boolean actual = insertionAlg.insertRequestForVehicle(pickup, vehicle, configuration);
		assertEquals(expected, actual);
	}
}
