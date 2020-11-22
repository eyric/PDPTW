package cn.edu.bupt.pdptw.model;

public enum VehicleType {
    L42("L42", 6, 60, 198, 1.218),
    L62("L62", 12, 60, 223, 1.734),
    L96("L96", 16, 60, 544, 3.037),
    L175("L175", 25, 60, 885, 5.260);

    private String type;
    private double capacity;
    private double velocity;
    private double fixCost;
    private double varCost;

    VehicleType(String type, double capacity, double velocity, double fixCost, double varCost) {
        this.type = type;
        this.capacity = capacity;
        this.velocity = velocity;
        this.fixCost = fixCost;
        this.varCost = varCost;
    }

    public static double minSpeed(){
        double speed = Double.MAX_VALUE;
        for (VehicleType type:VehicleType.values()){
            if (type.getVelocity() < speed){
                speed = type.getVelocity();
            }
        }
        return speed;
    }

    public double getCapacity() {
        return capacity;
    }

    public double getVelocity() {
        return velocity;
    }

    public double getFixCost() {
        return fixCost;
    }

    public double getVarCost() {
        return varCost;
    }

    public String getType() {
        return type;
    }

    public static VehicleType get(String type) {
        for (VehicleType vehicleType : VehicleType.values()) {
            if (vehicleType.getType().equalsIgnoreCase(type)) {
                return vehicleType;
            }
        }
        return L42;
    }
}
