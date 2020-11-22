package cn.edu.bupt.pdptw.algorithm.split.model;

import cn.edu.bupt.pdptw.model.DeliveryRequest;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.PickupRequest;
import lombok.Data;

@Data
public class TransportOrder {
    private PickupRequest pick;
    private DeliveryRequest delivery;

    public TransportOrder() {
    }

    public TransportOrder(PickupRequest pick, DeliveryRequest delivery) {
        pick.setSibling(delivery);
        delivery.setSibling(pick);

        this.pick = pick;
        this.delivery = delivery;
    }

    public TransportOrder(Location p1, Location p2) {
        delivery = new DeliveryRequest(0, p2, 0, 0, 100, 10);
        pick = new PickupRequest(0, p1, 0, 0, 100, 10);
        pick.setSibling(delivery);
        delivery.setSibling(pick);
    }

}
