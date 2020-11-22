package cn.edu.bupt.pdptw.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.algorithm.scheduling.DriveFirstScheduler;
import cn.edu.bupt.pdptw.algorithm.scheduling.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import cn.edu.bupt.pdptw.logging.LoggingUtils;

@Data
@EqualsAndHashCode
@AllArgsConstructor
public class Vehicle {
    private final String id;
    private VehicleType type;
    private final Integer maxCapacity;
    private Location location;
    private final Location startLocation;
    private Route route;
    private Set<Integer> servedRequestsIds;

    @Setter
    @Getter
    private static Scheduler scheduler = new DriveFirstScheduler();

    public Vehicle(String id, Integer maxCapacity,
                   Location startLocation) {

        this(id, VehicleType.L42, maxCapacity, startLocation, startLocation,
                new Route(new ArrayList<>()), new HashSet<>());
    }

    public Vehicle(String id, VehicleType type, Integer maxCapacity,
                   Location startLocation) {

        this(id, type, maxCapacity, startLocation, startLocation,
                new Route(new ArrayList<>()), new HashSet<>());
    }

    public List<Request> removeFinishedRequests(int time, boolean shouldLog) {

        /* note that we leave the pickup requests
         * that has already been served but have corresponding
         * delivery requests which are yet to be served! */

        List<Request> removedRequests = route.getRequests()
                .stream()
                .filter(r -> {
                    DeliveryRequest delivery = (DeliveryRequest)
                            ((r.getType() == RequestType.DELIVERY)
                                    ? r : r.getSibling());
                    return delivery.getRealizationTime() + delivery.getServiceTime() <= time;
                })
                .collect(Collectors.toList());

        if (shouldLog) {
            StringBuilder builder = new StringBuilder();
            removedRequests.forEach(r -> builder.append(r.getId()).append(", "));

            if (builder.length() > 0) {
                LoggingUtils.info("Vehicle [" + id
                        + "] has finished realization of the following requests: \n"
                        + "[" + builder.toString() + "]");
            }
        }

        servedRequestsIds.addAll(route.getRequests()
                .stream()
                .filter(r -> r.getRealizationTime() + r.getServiceTime() <= time)
                .map(Request::getId)
                .collect(Collectors.toSet()));
        route = new Route(route.getRequests()
                .stream()
                .filter(r -> {
                    DeliveryRequest delivery = (DeliveryRequest)
                            ((r.getType() == RequestType.DELIVERY)
                                    ? r : r.getSibling());
                    return delivery.getRealizationTime() + delivery.getServiceTime() > time;
                })
                .collect(Collectors.toList()));
        scheduler.scheduleRequests(this, time);
        return removedRequests;
    }

    public void removeRequestsByIds(List<Integer> requestsIds, int time) {

        route = new Route(route.getRequests()
                .stream()
                .filter(r -> !requestsIds.contains(r.getId()))
                .collect(Collectors.toList()));
        scheduler.scheduleRequests(this, time);
    }

    public boolean isInsertionPossible(PickupRequest pickupRequest, int pickupPosition, int deliveryPosition) {
        List<Request> requests = route.getRequests();

        assert pickupPosition <= route.getRequests().size();
        assert deliveryPosition <= route.getRequests().size();
        assert pickupPosition < deliveryPosition;
        assert pickupPosition <= requests.size();
        assert deliveryPosition <= requests.size() + 1;

        boolean insertionPossible = true;
        Request deliveryRequest = pickupRequest.getSibling();
        Request prev = null;
        Request cur;
        Request pickupCopy = pickupRequest.createShallowCopy();
        double totalVolume = 0;
        Iterator<Request> it = requests.iterator();
        int prevOriginalRealizationTime;
        int curOriginalRealizationTime;
        int counter = 0;

        if (pickupPosition < requests.size()) {
            Request nextRequest = requests.get(pickupPosition);
            insertionPossible = !servedRequestsIds.contains(nextRequest.getId());
        }

        while (insertionPossible
                && it.hasNext()
                && counter < pickupPosition) {

            prev = it.next();
            totalVolume += prev.getVolume();
            insertionPossible = (totalVolume <= maxCapacity);
            counter++;
        }

        if (prev != null) {
            scheduler.updateSuccessor(prev, pickupCopy);

        } else {
            scheduler.updateRequestRealizationTime(pickupCopy,
                    (int) Location.calculateDistance(
                            this.startLocation, pickupCopy.getLocation()));
        }

        totalVolume += pickupCopy.getVolume();
        insertionPossible = insertionPossible
                && (pickupCopy.getRealizationTime() <= pickupCopy.getTimeWindowEnd())
                && (totalVolume <= maxCapacity);

        prev = pickupCopy;
        prevOriginalRealizationTime = pickupRequest.getRealizationTime();
        counter++;

        while (insertionPossible
                && it.hasNext()
                && counter < deliveryPosition) {

            cur = it.next();
            totalVolume += cur.getVolume();
            curOriginalRealizationTime = cur.getRealizationTime();
            scheduler.updateSuccessor(prev, cur);
            insertionPossible = (cur.getRealizationTime() <= cur.getTimeWindowEnd())
                    && (totalVolume <= maxCapacity);
            prev.setRealizationTime(prevOriginalRealizationTime);
            prev = cur;
            prevOriginalRealizationTime = curOriginalRealizationTime;
            counter++;
        }

        curOriginalRealizationTime = deliveryRequest.getRealizationTime();
        scheduler.updateSuccessor(prev, deliveryRequest);
        prev.setRealizationTime(prevOriginalRealizationTime);
        prevOriginalRealizationTime = curOriginalRealizationTime;
        totalVolume += deliveryRequest.getVolume();
        prev = deliveryRequest;

        insertionPossible = insertionPossible
                && (deliveryRequest.getRealizationTime() <= deliveryRequest.getTimeWindowEnd())
                && (totalVolume <= maxCapacity);

        while (insertionPossible
                && it.hasNext()) {

            cur = it.next();
            totalVolume += cur.getVolume();
            curOriginalRealizationTime = cur.getRealizationTime();
            scheduler.updateSuccessor(prev, cur);
            insertionPossible = (cur.getRealizationTime() <= cur.getTimeWindowEnd())
                    && (totalVolume <= maxCapacity);
            prev.setRealizationTime(prevOriginalRealizationTime);
            prev = cur;
            prevOriginalRealizationTime = curOriginalRealizationTime;
            counter++;
        }

        prev.setRealizationTime(prevOriginalRealizationTime);

        return insertionPossible;
    }

    public void updateRealizationTimes() {
        scheduler.scheduleRequests(this, 0);
    }

    public void insertRequest(PickupRequest pickupRequest, int pickupPosition, int deliveryPosition) {
        List<Request> requests = route.getRequests();
        requests.add(pickupPosition, pickupRequest);
        requests.add(deliveryPosition, pickupRequest.getSibling());
        updateRealizationTimes();
    }

    public Request removeRequest(int pickupPosition, int deliveryPosition) {
        List<Request> requests = route.getRequests();

        assert pickupPosition >= 0;
        assert deliveryPosition >= 0;
        assert pickupPosition < requests.size();
        assert deliveryPosition < requests.size();

        Request pickup = requests.remove(pickupPosition);
        Request delivery = pickup.getSibling();

        pickup.setRealizationTime(pickup.getTimeWindowStart());
        delivery.setRealizationTime(delivery.getTimeWindowStart());

        requests.remove(deliveryPosition - 1);
        updateRealizationTimes();

        return pickup;
    }

    public Request removeRequest(int pickupPosition) {
        List<Request> requests = route.getRequests();

        assert pickupPosition >= 0;
        assert pickupPosition < requests.size();

        Request pickup = requests.get(pickupPosition);
        Request delivery = pickup.getSibling();

        pickup.setRealizationTime(pickup.getTimeWindowStart());
        delivery.setRealizationTime(delivery.getTimeWindowStart());

        requests.remove(pickupPosition);

        requests.remove(delivery);
        updateRealizationTimes();

        return pickup;
    }

    public RequestPositions removeRequest(PickupRequest pickupRequest) {
        List<Request> requests = route.getRequests();

        assert requests.contains(pickupRequest);
        assert requests.contains(pickupRequest.getSibling());

        int pickupPosition = requests.indexOf(pickupRequest);
        int deliveryPosition = requests.indexOf(pickupRequest.getSibling());
        requests.remove(pickupRequest);
        requests.remove(pickupRequest.getSibling());
        updateRealizationTimes();

        pickupRequest.setRealizationTime(pickupRequest.getTimeWindowStart());
        pickupRequest.getSibling().setRealizationTime(
                pickupRequest.getSibling().getTimeWindowStart());

        return new RequestPositions(pickupPosition, deliveryPosition);
    }

    @Override
    public String toString() {

        return String.format(
                "{\"id\": \"%s\",\"maxCapacity\": %d, \"startLocation\":{\"x\":%d,\"y\":%d}}",
                id, maxCapacity, location.getX(), location.getY());
    }

    public Vehicle createShallowCopy() { //浅复制
        Vehicle copy = new Vehicle(id, maxCapacity, startLocation);
        copy.setLocation(location);
        copy.setRoute(route);

        return copy;
    }

    public Vehicle copy() {
        Vehicle copy = new Vehicle(id, maxCapacity, startLocation);
        copy.setLocation(location);
        copy.setRoute(route.copy());

        return copy;
    }

    public Request getCurrentRequest(int time) {
        if (route.getRequests().size() > 0) {
            Iterator<Request> it = route.getRequests().iterator();
            Request current = it.next();
            Request prev = current;

            while (it.hasNext() && current.getRealizationTime() < time) {
                current = it.next();
                prev = current;
            }

            return prev;
        }

        return null;
    }
}
