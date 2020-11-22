package cn.edu.bupt.pdptw.model;

import cn.edu.bupt.pdptw.algorithm.agmoipso.model.CustomType;
import cn.edu.bupt.pdptw.utils.DistanceUtils;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Objects;

@AllArgsConstructor
@Data
public abstract class Request {
    protected Integer id;
    protected Location location;
    protected Integer volume;
    protected Integer timeWindowStart;
    protected Integer timeWindowEnd;
    protected Integer serviceTime;
    protected Integer realizationTime;
    protected Integer arrivalTime;      //需求到达时间(订单产生时间)
    protected Request sibling;

    protected CustomType customType;  //客户类型
    protected int priority;           //动态优先级

    protected RequestType type;
    protected double profit;

    protected String number; //请求编号

    public Request(Integer id, Location location, Integer volume,
                   Integer timeWindowStart, Integer timeWindowEnd,
                   Integer serviceTime, RequestType type) {

        super();
        this.id = id;
        this.location = location;
        this.volume = volume;
        this.timeWindowStart = timeWindowStart;
        this.timeWindowEnd = timeWindowEnd;
        this.serviceTime = serviceTime;

        this.arrivalTime = 0;
        this.realizationTime = timeWindowStart;
        this.type = type;
        //this.profit = profit;
    }

    public void setSibling(Request request) throws IllegalArgumentException {
        if (request == null) {
            System.out.println("=============================\n==============");
            return;
        }

        if (request.getType() == type
                || !request.getVolume().equals(-volume)) {

            throw new IllegalArgumentException("Assigning sibling of the same type "
                    + " or with different volume is not allowed");
        }

        this.sibling = request;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(id, request.id) &&
                type == request.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

    @Override
    public String toString() {
        return String.format("id: %d, type: %s, loc: %s, earliest: %d, realt: %d, latest: %d, realfinished: %d, servt: %d,"
                        + " sid: %s, v: %d",
                id, type.toString(), location, timeWindowStart, realizationTime, timeWindowEnd,
                realizationTime + serviceTime, serviceTime, (sibling != null) ? "" + sibling.getId() : "NO SIBLING", volume);

    }

    public Request createShallowCopy() {
        Request request;
        if (type.equals(RequestType.PICKUP)) {
            request = new PickupRequest(id, new Location(location.getX(), location.getY()), volume, timeWindowStart, timeWindowEnd, serviceTime);
        } else {
            request = new DeliveryRequest(id, new Location(location.getX(), location.getY()), volume, timeWindowStart, timeWindowEnd, serviceTime);
        }
        return request;
    }

    public Request copy() {
        Request copy = createShallowCopy();
        Request siblingCopy = sibling.createShallowCopy();
        copy.setSibling(siblingCopy);
        siblingCopy.setSibling(copy);

        return copy;
    }

    public String printFile() {
        int pickNum = volume < 0 ? sibling.getId() : 0;
        int deliveryNum = volume > 0 ? sibling.getId() : 0;
        return "" + id + "\t" + location.getX() + "\t" + location.getY() + "\t"
                + volume + "\t" + timeWindowStart + "\t" + timeWindowEnd + "\t"
                + serviceTime + "\t" + pickNum + "\t" + deliveryNum;
    }

    //计算动态优先级
    public void calPriority() {
        int T = Math.min(timeWindowEnd, getSibling().getTimeWindowEnd());
        double D = DistanceUtils.euclideanDistance(getLocation(), getSibling().getLocation());
        double v = VehicleType.minSpeed();
        double t = D / v;

        this.priority = (int) (10 / T * (T - t));
    }

}
