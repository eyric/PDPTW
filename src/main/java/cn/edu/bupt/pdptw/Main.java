package cn.edu.bupt.pdptw;

import java.io.IOException;
import java.util.List;

import cn.edu.bupt.pdptw.algorithm.dynamic.RequestDispatcher;
import cn.edu.bupt.pdptw.algorithm.generation.GenerationAlgorithm;
import cn.edu.bupt.pdptw.algorithm.optimization.DecompositionOptimizer;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.logging.LoggingUtils;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.json.simple.parser.ParseException;

import cn.edu.bupt.pdptw.configuration.AlgorithmConfiguration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;


public class Main {

    public static void main(String[] args) {
    	try {
    		LoggingUtils.info("Loading configuration data");
    		DefaultConfigReader loader = new DefaultConfigReader();
    		List<Configuration> testConfigurations
    			= loader.loadConfiguration("resources/test/config.json");
    		
    		for (Configuration configuration : testConfigurations) {
    			
    			LoggingUtils.configure(configuration);
    			
    			Vehicle.setScheduler(configuration.getAlgorithms().getScheduler());
    			List<Request> requests = loader.loadRequests(configuration);
    			List<Vehicle> vehicles = loader.loadVehicles(configuration);
    			
    			AlgorithmConfiguration algs = configuration.getAlgorithms();
    			GenerationAlgorithm generation = algs.getGenerationAlgorithm();
    			Solution solution;
    			
    			if (configuration.isDynamic()) {
    				LoggingUtils.info("Dynamic version detected");
    				new RequestDispatcher(requests, vehicles, configuration);
    			} else {
    				LoggingUtils.info("Static version detected");
    				solution = generation.generateSolution(requests, vehicles, configuration);
					System.out.println(solution.getObjectiveValue());
    				LoggingUtils.info("Original objective value: " + solution.getObjectiveValue());
    				DecompositionOptimizer optimizer = new DecompositionOptimizer(solution, configuration);
    				optimizer.startThread();
    			}
    		}

		} catch (InvalidFileFormatException | ParseException | IllegalArgumentException e) {
			LoggingUtils.logStackTrace(e);
		} catch (IOException e) {
			LoggingUtils.logStackTrace(e);
			LoggingUtils.error("An error occurred while reading input file");
		}
	}
}
