package cn.edu.bupt.pdptw.algorithm.split.model;

import lombok.Data;

@Data
public class AlgoPara {
    /**
     * α型聚类半径
     */
    private double alpha = 5;
    /**
     * β型聚类半径
     */
    private double beta = 8;
    /**
     * αβ型聚类半径
     */
    private double deta = 5;

    /**
     * 车辆分组半径
     */
    private double gama = 5;
}
