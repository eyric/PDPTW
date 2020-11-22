package cn.edu.bupt.pdptw.algorithm.agmoipso.model;

import lombok.Data;


/**
 * 粒子交换对
 */
@Data
public class SO {
    private int x;
    private int y;

    public SO(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
