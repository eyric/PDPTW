package cn.edu.bupt.pdptw.algorithm.split.model;

public enum ClusterType {
    A("α"),
    B("β"),
    AB("αβ"),
    N("no");

    private String name;

    ClusterType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
