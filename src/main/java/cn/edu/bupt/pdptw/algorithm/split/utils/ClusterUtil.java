package cn.edu.bupt.pdptw.algorithm.split.utils;

import cn.edu.bupt.pdptw.algorithm.split.algo.BufferArea;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.algorithm.split.model.TransportOrder;
import cn.edu.bupt.pdptw.model.Location;
import cn.edu.bupt.pdptw.model.Request;

import java.util.List;

public class ClusterUtil {
    /**
     * 计算两个订单之间的聚类距离
     */
    public static double distance(Request r1, Request r2) {
        Request d1 = r1.getSibling();
        Request d2 = r2.getSibling();
        double a = BufferArea.distance(r1.getLocation(), r2.getLocation());
        double b = BufferArea.distance(d1.getLocation(), d2.getLocation());
        double c = BufferArea.distance(r1.getLocation(), d2.getLocation());
        double d = BufferArea.distance(d1.getLocation(), r2.getLocation());

        return Math.sqrt(Math.pow((a + b) / 2, 2) + Math.pow(Math.min(c, d), 2));
    }

    /**
     * 聚类中心
     * 使用几何中心
     */
    public static Location center(ClusterResult result) {
        int x = 0, y = 0, n = result.getRequestList().size();
        n = n > 0 ? n : 1;
        for (Request request : result.getRequestList()) {
            x += (request.getLocation().getX() + request.getSibling().getLocation().getX()) / 2;
            y += (request.getLocation().getY() + request.getSibling().getLocation().getY()) / 2;
        }
        return new Location(x / n, y / n);
    }

    public static TransportOrder center(List<TransportOrder> orders) {
        int x1 = 0, y1 = 0, x2 = 0, y2 = 0, n = orders.size() > 0 ? orders.size() : 1;
        for (TransportOrder order : orders) {
            x1 += order.getPick().getLocation().getX();
            y1 += order.getPick().getLocation().getY();
            x2 += order.getDelivery().getLocation().getX();
            y2 += order.getDelivery().getLocation().getY();
        }

        Location pick = new Location(x1 / n, y1 / n);
        Location delivery = new Location(x2 / n, y2 / n);

        return new TransportOrder(pick, delivery);
    }

    /**
     * 轮廓系数 sc=(b-a)/max(a,b)
     * <p>
     * 其中a表示样本与同一个簇中其他点之间的平均距离,
     * b表示样本与下一个距离最近的簇中其他点的平均距离。
     * 可见轮廓系数的值是介于 [-1,1] ，越趋近于1代表内聚度和分离度都相对较优。
     * 将所有点的轮廓系数求平均，就是该聚类结果总的轮廓系数。
     */
    public static double sc(List<ClusterResult> results) {
        int n = results.size();
        if (n <= 1) return -1;

        double sc = 0;
        for (ClusterResult result : results) {
            List<Request> requestList = result.getRequestList();
            if (requestList.size() <= 1)
                continue;

            double sc0 = 0;
            for (int i = 0; i < requestList.size(); i++) {
                double a = 0, b = 0;
                for (int j = 0; j < requestList.size(); j++) {
                    if (i != j) {
                        a += distance(requestList.get(i), requestList.get(j));
                    }
                }
                a /= (requestList.size() - 1);

                ClusterResult near = nearGroup(requestList.get(i), results, result);
                if (near.getRequestList().size() == 0)
                    continue;
                for (int j = 0; j < near.getRequestList().size(); j++) {
                    b += distance(requestList.get(i), near.getRequestList().get(j));
                }
                b /= near.getRequestList().size();

                sc0 += (b - a) / Math.max(a, b);
            }
            sc += sc0 / requestList.size();

        }
        return sc / results.size();
    }

    /**
     * 离订单最近的聚类簇
     */
    private static ClusterResult nearGroup(Request request, List<ClusterResult> results, ClusterResult result) {
        ClusterResult res = result;
        Location location = new Location((request.getLocation().getX() + request.getSibling().getLocation().getX()) / 2,
                (request.getLocation().getY() + request.getSibling().getLocation().getY()) / 2);
        double distance = Double.MAX_VALUE;
        for (ClusterResult cr : results) {
            double dis = BufferArea.distance(center(cr), location);
            if (!cr.equals(result) && dis < distance) {
                res = cr;
                distance = dis;
            }
        }
        return res;
    }
}
