package cn.edu.bupt.pdptw.algorithm.agmoipso.model;

import cn.edu.bupt.pdptw.algorithm.objective.TotalProfitObjective;
import cn.edu.bupt.pdptw.algorithm.objective.TotalServiceObjective;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Vehicle;
import lombok.Data;
import org.uma.jmetal.solution.Solution;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * solution for pickup and delivery problem
 *
 * @author Levin
 */
@Data
public class PdpSolution implements Solution {
    //目标函数数组
    private double[] fitnessArr;
    //决策变量数组
    private PdpVariable[] variableArr;

    //属性
    protected Map<Object, Object> attributes;

    //兼容以前的代码
    private cn.edu.bupt.pdptw.model.Solution solution;

    //信息熵
    private double entropy;

    private PdpSolution(double[] fitnessArr, PdpVariable[] variableArr) {
        this.fitnessArr = Arrays.copyOf(fitnessArr, fitnessArr.length);
        this.variableArr = Arrays.copyOf(variableArr, variableArr.length);
        attributes = new HashMap<>();
    }

    public PdpSolution(cn.edu.bupt.pdptw.model.Solution solution) {
        this.solution = solution;

        int idx = 0;
        for (Vehicle vehicle : solution.getVehicles()) {
            variableArr[idx++] = new PdpVariable(1, vehicle.getId());
            for (Request request : vehicle.getRoute().getRequests()) {
                int type = request.getType() == RequestType.PICKUP ? 2 : 3;
                variableArr[idx++] = new PdpVariable(type, request.getId() + "");
            }
        }

        fitnessArr[0] = new TotalProfitObjective().calculate(solution);
        fitnessArr[1] = new TotalServiceObjective().calculate(solution);

        this.fitnessArr = Arrays.copyOf(fitnessArr, fitnessArr.length);
        this.variableArr = Arrays.copyOf(variableArr, variableArr.length);
        attributes = new HashMap<>();
    }

    @Override
    public void setObjective(int i, double v) {
        if (i >= fitnessArr.length) {
            throw new RuntimeException("目标函数数目越界");
        }
        fitnessArr[i] = v;
    }

    @Override
    public double getObjective(int i) {
        if (i >= fitnessArr.length) {
            throw new RuntimeException("目标函数数目越界");
        }

        return fitnessArr[i];
    }

    @Override
    public Object getVariableValue(int i) {
        return null;
    }

    @Override
    public void setVariableValue(int i, Object o) {
        if (i >= variableArr.length) {
            throw new RuntimeException("决策变量数目越界");
        }
        variableArr[i] = (PdpVariable) o;
    }

    @Override
    public String getVariableValueString(int i) {
        if (i >= variableArr.length) {
            throw new RuntimeException("决策变量数目越界");
        }

        return variableArr[i].toString();
    }

    @Override
    public int getNumberOfVariables() {
        return variableArr.length;
    }

    @Override
    public int getNumberOfObjectives() {
        return fitnessArr.length;
    }

    @Override
    public Solution copy() {
        return new PdpSolution(this.getFitnessArr(), this.getVariableArr());
    }

    @Override
    public void setAttribute(Object o, Object o1) {
        attributes.put(o, o1);
    }

    @Override
    public Object getAttribute(Object o) {
        return attributes.getOrDefault(o, null);
    }
}
