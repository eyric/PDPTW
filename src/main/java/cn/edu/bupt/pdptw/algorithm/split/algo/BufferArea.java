package cn.edu.bupt.pdptw.algorithm.split.algo;

import cn.edu.bupt.pdptw.algorithm.split.model.AlgoPara;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterType;
import cn.edu.bupt.pdptw.model.DeliveryRequest;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.PickupRequest;

/**
 * 缓冲区
 */
public class BufferArea {
    public static ClusterType get(PickupRequest pick1, PickupRequest pick2, AlgoPara para) {

        double alpha = para.getAlpha();
        double beta = para.getBeta();
        double deta = para.getDeta();

        if (isTypeOne(pick1, pick2, alpha)) {
            return ClusterType.A;
        } else if (isTypeTwo(pick1, pick2, beta)) {
            return ClusterType.B;
        } else if (isTypeThree(pick1, pick2, alpha, deta)) {
            return ClusterType.AB;
        }

        return ClusterType.N;

    }

    /**
     * I型聚类条件判断
     */
    private static boolean isTypeOne(PickupRequest pick1, PickupRequest pick2, double alpha) {
        DeliveryRequest delivery1 = (DeliveryRequest) pick1.getSibling();
        DeliveryRequest delivery2 = (DeliveryRequest) pick2.getSibling();

        return distance(pick1.getLocation(), pick2.getLocation()) < alpha
                && distance(delivery1.getLocation(), delivery2.getLocation()) < alpha;
    }

    /**
     * II型聚类条件判断
     */
    private static boolean isTypeTwo(PickupRequest pick1, PickupRequest pick2, double beta) {
        DeliveryRequest delivery1 = (DeliveryRequest) pick1.getSibling();
        DeliveryRequest delivery2 = (DeliveryRequest) pick2.getSibling();

        return distance(pick1.getLocation(), delivery2.getLocation()) < beta
                && distance(delivery1.getLocation(), pick2.getLocation()) < beta;
    }

    /**
     * III型聚类条件判断
     */
    private static boolean isTypeThree(PickupRequest pick1, PickupRequest pick2, double alpha, double deta) {
        DeliveryRequest delivery1 = (DeliveryRequest) pick1.getSibling();
        DeliveryRequest delivery2 = (DeliveryRequest) pick2.getSibling();

        return distance(delivery1.getLocation(), delivery2.getLocation()) < alpha
                && pointToLine(pick1.getLocation(), delivery1.getLocation(), pick2.getLocation()) < deta;
    }

    /**
     * 点到直线的垂线距离
     */
    public static double pointToLine(Location L1, Location L2, Location p) {
        int x0 = p.getX();
        int y0 = p.getY();
        int x1 = L1.getX();
        int y1 = L1.getY();
        int x2 = L2.getX();
        int y2 = L2.getY();

        double k = 1.0 * (y2 - y1) / (x2 - x1);
        double yD = (k * (x0 - x1) + k * k * y0 + y1) / (1 + k * k);
        double xD = x0 - k * (yD - y0);

        Location D = new Location((int) xD, (int) yD);

        return distance(p, D);
    }

    public static double distance(Location p1, Location p2) {
        return BufferClustering.getDistance(p1, p2);
    }

    public static void main(String[] args) {

    }

}
