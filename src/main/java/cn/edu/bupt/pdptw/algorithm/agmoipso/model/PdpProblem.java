package cn.edu.bupt.pdptw.algorithm.agmoipso.model;

import cn.edu.bupt.pdptw.algorithm.objective.TotalProfitObjective;
import cn.edu.bupt.pdptw.algorithm.objective.TotalServiceObjective;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.Vehicle;
import lombok.Data;
import org.json.simple.parser.ParseException;
import org.uma.jmetal.problem.Problem;
import org.uma.jmetal.solution.Solution;

import java.io.IOException;
import java.util.List;

/**
 * 取送货问题
 */
@Data
public class PdpProblem implements Problem {
    //运输车辆列表
    private List<Vehicle> vehicleList;

    //订单列表
    private List<Request> requestList;

    private Configuration configuration;

    private int numberOfVariables;

    private int numberOfObjectives;

    public PdpProblem(Configuration configuration) {
        DefaultConfigReader loader = new DefaultConfigReader();
        try {
            requestList = loader.loadRequests(configuration);
            vehicleList = loader.loadVehicles(configuration);
            numberOfVariables = vehicleList.size() + 2 * requestList.size();
            numberOfObjectives = 2;
        } catch (IOException | ParseException | InvalidFileFormatException e) {
            throw new RuntimeException("加载数据错误");
        }
        this.configuration = configuration;

    }

    @Override
    public int getNumberOfVariables() {
        return numberOfVariables;
    }

    @Override
    public int getNumberOfObjectives() {
        return numberOfObjectives;
    }

    @Override
    public int getNumberOfConstraints() {
        return 0;
    }

    @Override
    public String getName() {
        return "pdp";
    }

    @Override
    public Solution<?> createSolution() {

        cn.edu.bupt.pdptw.model.Solution solution = configuration.getAlgorithms().getGenerationAlgorithm()
                .generateSolution(requestList, vehicleList, configuration);

        return new PdpSolution(solution);
    }

    @Override
    public void evaluate(Solution solution) {
        solution.setObjective(0, new TotalProfitObjective().calculate(((PdpSolution) solution).getSolution()));
        solution.setObjective(1, new TotalServiceObjective().calculate(((PdpSolution) solution).getSolution()));
    }
}


