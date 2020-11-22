package cn.edu.bupt.pdptw.algorithm.dynamic;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import cn.edu.bupt.pdptw.algorithm.optimization.AdaptiveMemory;
import cn.edu.bupt.pdptw.algorithm.optimization.DecompositionOptimizer;
import cn.edu.bupt.pdptw.configuration.Configuration;
import lombok.Getter;
import cn.edu.bupt.pdptw.algorithm.insertion.InsertionAlgorithm;
import cn.edu.bupt.pdptw.algorithm.objective.Objective;
import cn.edu.bupt.pdptw.logging.LoggingUtils;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Route;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

public class RequestDispatcher {
    private static final int INSERTION_CHECK_RATE = 10;
    private static final int TIME_DELTA = 50;

    @Getter
    private List<Request> requests;
    @Getter
    private List<Vehicle> vehicles;
    @Getter
    private int vehiclesUsed;
    @Getter
    private Solution solution;
    @Getter
    private AtomicInteger time;
    @Getter
    private Configuration configuration;
    private InsertionWorker insertionWorker;
    private DecompositionOptimizer optimizer;
    private Thread optimizerThread;
    private ScheduledExecutorService executionService;

    private class InsertionWorker implements Runnable {
        private InsertionAlgorithm insertion;
        private Objective objective;

        public InsertionWorker() {
            this.insertion = configuration.getAlgorithms().getInsertionAlgorithm();
            this.objective = configuration.getAlgorithms().getObjective();
        }

        @Override
        public void run() {
            int curTime = time.addAndGet(TIME_DELTA);
            LoggingUtils.info("Current time: " + curTime);

            List<PickupRequest> pickups = requests.stream()
                    .filter(r -> (r.getType() == RequestType.PICKUP)
                            && (r.getArrivalTime() <= curTime))
                    .map(r -> (PickupRequest) r)
                    .collect(Collectors.toList());

            requests = requests.stream()
                    .filter(r -> r.getArrivalTime() > curTime)
                    .collect(Collectors.toList());

            try {
                LoggingUtils.saveResult(solution, curTime, configuration);
            } catch (IOException e) {
                LoggingUtils.logStackTrace(e);
            }

            try {
                LoggingUtils.info("Attempting to stop the optimization thread");
                optimizer.stopOptimization();
                optimizerThread.join();
            } catch (InterruptedException e) {
                LoggingUtils.logStackTrace(e);
            }

            solution = optimizer.getSolution();

            /* remove the finished requests from the current solution */
            List<Integer> removedRequestsIds = solution.getVehicles()
                    .stream()
                    .flatMap(v -> v.removeFinishedRequests(curTime, true).stream())
                    .map(Request::getId)
                    .collect(Collectors.toList());

            AdaptiveMemory adaptiveMemory = optimizer.getAdaptiveMemory();
            adaptiveMemory.getSolutions()
                    .forEach(s -> s.getVehicles()
                            .forEach(v -> {
                                v.removeRequestsByIds(removedRequestsIds, curTime);
                            }));

            if (pickups.size() > 0) {
                LoggingUtils.info("Inserting new requests");
                List<Vehicle> spareCopies = new LinkedList<>();
                boolean insertedSuccessfully;


                for (PickupRequest pickup : pickups) {
                    LoggingUtils.info("Inserting: " + pickup.getId()
                            + " (arrival time: " + pickup.getArrivalTime() + ")");

                    PickupRequest pickupCopy = (PickupRequest) pickup.copy();
                    insertedSuccessfully = insertion.insertRequestToSolution(
                            pickupCopy, solution, configuration);

                    if (!insertedSuccessfully) {
                        Vehicle spareVehicle = vehicles.get(vehiclesUsed);
                        spareCopies.add(spareVehicle);

                        LoggingUtils.info("Vehicle [" + spareVehicle.getId() + "] has been used");
                        insertion.insertRequestForVehicle(pickupCopy, spareVehicle, configuration);
                        solution.getVehicles().add(spareVehicle);
                        vehiclesUsed++;
                    }

                    solution.updateOjectiveValue(objective);
                }

                if (adaptiveMemory.getSolutions().size() > 0) {
                    LoggingUtils.info("Updating adaptive memory ("
                            + adaptiveMemory.getSolutions().size() + " solutions)");

                    for (Solution s : adaptiveMemory.getSolutions()) {
                        int spareCopiesUsed = 0;
                        for (PickupRequest pickup : pickups) {

                            PickupRequest pickupCopy = (PickupRequest) pickup.copy();
                            insertedSuccessfully = insertion.insertRequestToSolution(pickupCopy, s, configuration);

                            if (!insertedSuccessfully) {
                                Vehicle spareCopy;
                                if (spareCopiesUsed >= spareCopies.size()) {
                                    spareCopy = vehicles.get(vehiclesUsed);
                                    spareCopies.add(spareCopy);
                                    vehiclesUsed++;
                                } else {
                                    Vehicle spareVehicle = spareCopies.get(spareCopiesUsed);
                                    spareCopy = spareVehicle.copy();
                                    spareCopy.setRoute(new Route(new LinkedList<>()));
                                }

                                insertion.insertRequestForVehicle(pickupCopy, spareCopy, configuration);
                                s.getVehicles().add(spareCopy);
                                spareCopiesUsed++;
                            }
                        }

                        s.updateOjectiveValue(objective);
                    }
                }
            }

            solution.setVehicles(solution.getVehicles()
                    .stream()
                    .filter(v -> v.getRoute().getRequests().size() > 0)
                    .collect(Collectors.toList()));

            adaptiveMemory.getSolutions()
                    .forEach(s -> s.setVehicles(
                            s.getVehicles().stream()
                                    .filter(v -> v.getRoute().getRequests().size() > 0)
                                    .collect(Collectors.toList())
                    ));

            solution.updateOjectiveValue(objective);
            adaptiveMemory.getSolutions()
                    .forEach(s -> s.updateOjectiveValue(objective));

            for (Vehicle v : solution.getVehicles()) {
                if (v.getRoute().getRequests().size() > 0) {
                    LoggingUtils.info("Vehicle ["
                            + v.getId() + "] is currently serving request ["
                            + v.getCurrentRequest(curTime).getId() + "]");
                }
            }

            if (solution.getVehicles().size() > 1) {
                optimizer.setSolution(solution);
                optimizerThread = optimizer.startThread();
            }

            if (requests.size() > 0
                    || solution.getRequests().size() > 0) {
                LoggingUtils.info("Starting optimizer thread");

                LoggingUtils.info("Rescheduling the insertion task");
                executionService.schedule(this, INSERTION_CHECK_RATE, TimeUnit.SECONDS);
            }
        }
    }

    public RequestDispatcher(List<Request> requests, List<Vehicle> vehicles, Configuration configuration) {
        this.time = new AtomicInteger(0);
        this.configuration = configuration;

        this.solution = configuration.getAlgorithms()
                .getGenerationAlgorithm()
                .generateSolution(
                        requests.stream()
                                .filter(r -> r.getArrivalTime() == 0)
                                .collect(Collectors.toList()), vehicles, configuration);
        this.requests = requests.stream()
                .filter(r -> r.getArrivalTime() != 0)
                .collect(Collectors.toList());
        this.vehiclesUsed = solution.getVehicles().size();
        this.vehicles = vehicles.stream()
                .filter(v -> v.getRoute().getRequests().size() == 0)
                .collect(Collectors.toList());

        this.insertionWorker = new InsertionWorker();
        this.optimizer = new DecompositionOptimizer(solution, configuration);
        this.optimizerThread = this.optimizer.startThread();
        this.executionService = Executors.newScheduledThreadPool(1);
        this.executionService.schedule(this.insertionWorker, INSERTION_CHECK_RATE, TimeUnit.SECONDS);
    }


}
