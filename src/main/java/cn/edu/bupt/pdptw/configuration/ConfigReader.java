package cn.edu.bupt.pdptw.configuration;

import java.io.IOException;
import java.util.List;

import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Vehicle;
import org.json.simple.parser.ParseException;

import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;

public interface ConfigReader {
	List<Configuration> loadConfiguration(String configFilePath)
			throws IllegalArgumentException, IOException, ParseException;
	List<Request> loadRequests(Configuration configuration)
			throws IOException, InvalidFileFormatException;
	List<Vehicle> loadVehicles(Configuration configuration)
			throws IOException, ParseException;
}
