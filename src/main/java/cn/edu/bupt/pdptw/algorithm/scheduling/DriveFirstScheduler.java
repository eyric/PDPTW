package cn.edu.bupt.pdptw.algorithm.scheduling;

import java.util.Iterator;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.logging.LoggingUtils;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Vehicle;

public class DriveFirstScheduler implements Scheduler {

    @Override
    public void scheduleRequests(Vehicle vehicle, int firstEarliestRealizationTime)
            throws IllegalArgumentException {

        Vehicle copy = vehicle.copy();

        if (vehicle.getRoute().getRequests().size() > 0) {

            Iterator<Request> it = vehicle.getRoute().getRequests()
                    .stream()
                    .filter(r -> r.getRealizationTime() >= firstEarliestRealizationTime)
                    .collect(Collectors.toList())
                    .iterator();
            Request prev = null;

            if (it.hasNext()) {
                prev = it.next();
            }

            while (it.hasNext()) {
                Request cur = it.next();
                double distance = Location.calculateDistance(prev.getLocation(), cur.getLocation());
                int earliestRealizationTime = (int) (prev.getRealizationTime()
                        + prev.getServiceTime()
                        + distance);
                int timeWindowStart = cur.getTimeWindowStart();

                cur.setRealizationTime((earliestRealizationTime >= timeWindowStart)
                        ? earliestRealizationTime
                        : timeWindowStart);

                if (earliestRealizationTime > cur.getTimeWindowEnd()) {
                    LoggingUtils.error(vehicle.getServedRequestsIds());
                    LoggingUtils.error(copy);
                    LoggingUtils.error("-----------------------");
                    LoggingUtils.error(vehicle);

                    throw new IllegalArgumentException("Earliest realization time"
                            + " is greater than the end of the time window" + earliestRealizationTime);
                }

                prev = cur;
            }
        }
    }

    @Override
    public void updateSuccessor(Request prev, Request cur) {
        double distance = Location.calculateDistance(prev.getLocation(), cur.getLocation());
        int earliestRealizationTime = (int) (prev.getRealizationTime()
                + prev.getServiceTime()
                + distance);

        earliestRealizationTime = (earliestRealizationTime > cur.getTimeWindowStart())
                ? earliestRealizationTime
                : cur.getTimeWindowStart();

        cur.setRealizationTime(earliestRealizationTime);
    }

    @Override
    public int getSuccessorRealizationTime(Request prev, Request cur) {
        int result;
        int distance = (int) Location.calculateDistance(prev.getLocation(), cur.getLocation());
        int earliestRealizationTime = prev.getRealizationTime() + prev.getServiceTime() + distance;

        result = (earliestRealizationTime > cur.getTimeWindowStart())
                ? earliestRealizationTime
                : cur.getTimeWindowStart();

        return result;
    }

    @Override
    public void updateRequestRealizationTime(Request req, int time) {
        if (req.getRealizationTime() < time) {
            req.setRealizationTime(time);
        }
    }
}
