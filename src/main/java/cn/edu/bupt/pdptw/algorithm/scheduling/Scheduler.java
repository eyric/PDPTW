package cn.edu.bupt.pdptw.algorithm.scheduling;

import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Vehicle;

public interface Scheduler {

    void scheduleRequests(Vehicle vehicle, int firstEarliestRealizationTime);

    void updateSuccessor(Request prev, Request cur);

    void updateRequestRealizationTime(Request req, int time);

    int getSuccessorRealizationTime(Request prev, Request cur);
}