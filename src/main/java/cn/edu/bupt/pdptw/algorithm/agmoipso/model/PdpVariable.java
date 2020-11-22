package cn.edu.bupt.pdptw.algorithm.agmoipso.model;

import lombok.Data;

@Data
public class PdpVariable {
    private int type;   //基因类型 1：车辆 2：取货点 3：送货点
    private String value;  //基因取值 对应的车辆或订单编号

    public PdpVariable(int type, String value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public String toString() {
        return type + "_" + value;
    }
}
