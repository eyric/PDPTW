package cn.edu.bupt.pdptw.split;


import cn.edu.bupt.pdptw.model.Location;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;


public class MinimumBoundingPolygon {

    private static List<Point> convert(List<Location> points) {
        List<Point> pointList = new ArrayList<>();
        for (Location point : points) {
            pointList.add(new Point(point.getX(), point.getY()));
        }
        return pointList;
    }

    private static LinkedList<Point> convert2(List<Point> points) {
        LinkedList<Point> pointList = new LinkedList<>();
        for (Point point : points) {
            pointList.add(new Point(point.getX(), point.getY()));
        }
        return pointList;
    }

    public static LinkedList<Point> findSmallestPolygon(List<Location> ps1) {
        if (null == ps1 || ps1.isEmpty()) {
            return null;
        }

        List<Point> ps = convert(ps1);
        Point corner = findStartPoint(ps);
        if (null == corner) {
            return null;
        }

        double minAngleDif, oldAngle = 2 * Math.PI;
        LinkedList<Point> bound = new LinkedList<>();
        do {
            minAngleDif = 2 * Math.PI;

            bound.add(corner);

            Point nextPoint = corner;
            double nextAngle = oldAngle;
            for (Point p : ps) {
                if (p.founded) { // 已被加入边界链表的点
                    continue;
                }

                if (p.equals(corner)) { // 重合点
                    /*if (!p.equals(bound.getFirst())) {
                        p.founded = true;
                    }*/
                    continue;
                }

                double currAngle = DiscretePointUtil.angleOf(corner, p); /* 当前向量与x轴正方向的夹角 */
                double angleDif = DiscretePointUtil.reviseAngle(oldAngle - currAngle); /* 两条向量之间的夹角（顺时针旋转的夹角） */

                if (angleDif < minAngleDif) {
                    minAngleDif = angleDif;
                    nextPoint = p;
                    nextAngle = currAngle;
                }
            }

            oldAngle = nextAngle;
            corner = nextPoint;
            corner.founded = true;
        } while (!corner.equals(bound.getFirst())); /* 判断边界是否闭合 */

        return convert2(bound);
    }

    /**
     * 查找起始点（保证y最大的情况下、尽量使x最小的点）
     */
    private static Point findStartPoint(List<Point> ps) {
        if (null == ps || ps.isEmpty()) {
            return null;
        }

        Point p = ps.get(0);
        ListIterator<Point> iter = ps.listIterator();

        while (iter.hasNext()) {
            Point point = iter.next();
            if (point.getY() > p.getY() || (point.getY() == p.getY() && point.getX() < p.getX())) { /* 找到最靠上靠左的点 */
                p = point;
            }
        }

        return p;
    }
}

class DiscretePointUtil {

    /**
     * <p>
     * <b>查找离散点集中的(min_x, min_Y) (max_x, max_Y)</b>
     * <p>
     * <pre>
     * 查找离散点集中的(min_x, min_Y) (max_x, max_Y)
     * </pre>
     *
     * @param points 离散点集
     * @return [(min_x, min_Y), (max_x, max_Y)]
     * @author ManerFan 2015年4月9日
     */
    public static Point[] calMinMaxDots(final List<Point> points) {
        if (null == points || points.isEmpty()) {
            return null;
        }

        double min_x = points.get(0).getX(), max_x = points.get(0).getX();
        double min_y = points.get(0).getY(), max_y = points.get(0).getY();

        /* 这里存在优化空间，可以使用并行计算 */
        for (Point point : points) {
            if (min_x > point.getX()) {
                min_x = point.getX();
            }

            if (max_x < point.getX()) {
                max_x = point.getX();
            }

            if (min_y > point.getY()) {
                min_y = point.getY();
            }

            if (max_y < point.getY()) {
                max_y = point.getY();
            }
        }

        Point ws = new Point(min_x, min_y);
        Point en = new Point(max_x, max_y);

        return new Point[]{ws, en};
    }

    /**
     * <p>
     * <b>求矩形面积平方根</b>
     * <p>
     * <pre>
     * 以两个点作为矩形的对角线上的两点，计算其面积的平方根
     * </pre>
     *
     * @param ws 西南点
     * @param en 东北点
     * @return 矩形面积平方根
     * @author ManerFan 2015年4月9日
     */
    public static double calRectAreaSquare(Point ws, Point en) {
        if (null == ws || null == en) {
            return .0;
        }

        /* 为防止计算面积时float溢出，先计算各边平方根，再相乘 */
        return Math.sqrt(Math.abs(ws.getX() - en.getX()))
                * Math.sqrt(Math.abs(ws.getY() - en.getY()));
    }

    /**
     * <p>
     * <b>求两点之间的长度</b>
     * <p>
     * <pre>
     * 求两点之间的长度
     * </pre>
     *
     * @param ws 西南点
     * @param en 东北点
     * @return 两点之间的长度
     * @author ManerFan 2015年4月10日
     */
    public static double calLineLen(Point ws, Point en) {
        if (null == ws || null == en) {
            return .0;
        }

        if (ws.equals(en)) {
            return .0;
        }

        double a = Math.abs(ws.getX() - en.getX()); // 直角三角形的直边a
        double b = Math.abs(ws.getY() - en.getY()); // 直角三角形的直边b

        double min = Math.min(a, b); // 短直边
        double max = Math.max(a, b); // 长直边

        /**
         * 为防止计算平方时float溢出，做如下转换
         * √(min²+max²) = √((min/max)²+1) * abs(max)
         */
        double inner = min / max;
        return Math.sqrt(inner * inner + 1.0) * max;
    }

    /**
     * <p>
     * <b>求两点间的中心点</b>
     * <p>
     * <pre>
     * 求两点间的中心点
     * </pre>
     *
     * @param ws 西南点
     * @param en 东北点
     * @return 两点间的中心点
     * @author ManerFan 2015年4月10日
     */
    public static Point calCerter(Point ws, Point en) {
        if (null == ws || null == en) {
            return null;
        }

        return new Point(ws.getX() + (en.getX() - ws.getX()) / 2.0, ws.getY()
                + (en.getY() - ws.getY()) / 2.0);
    }

    /**
     * <p>
     * <b>计算向量角</b>
     * <p>
     * <pre>
     * 计算两点组成的向量与x轴正方向的向量角
     * </pre>
     *
     * @param s 向量起点
     * @param d 向量终点
     * @return 向量角
     * @author ManerFan 2015年4月17日
     */
    public static double angleOf(Point s, Point d) {
        double dist = calLineLen(s, d);

        if (dist <= 0) {
            return .0;
        }

        double x = d.getX() - s.getX(); // 直角三角形的直边a
        double y = d.getY() - s.getY(); // 直角三角形的直边b

        if (y >= 0.) { /* 1 2 象限 */
            return Math.acos(x / dist);
        } else { /* 3 4 象限 */
            return Math.acos(-x / dist) + Math.PI;
        }
    }

    /**
     * <p>
     * <b>修正角度</b>
     * <p>
     * <pre>
     * 修正角度到 [0, 2PI]
     * </pre>
     *
     * @param angle 原始角度
     * @return 修正后的角度
     * @author ManerFan 2015年4月17日
     */
    public static double reviseAngle(double angle) {
        while (angle < 0.) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }

        return angle;
    }

}

/**
 * <p>
 * <b>离散点</b>
 * <p>
 * <pre>
 * 离散点
 * </pre>
 *
 * @author ManerFan 2015年4月10日
 */
class Point {

    /**
     * x坐标
     */
    private double x;

    /**
     * y坐标
     */
    private double y;

    /**
     * 边界查找算法中 是否被找到
     */
    boolean founded = false;

    public Point() {
    }

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Point other = (Point) obj;
        if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
            return false;
        if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
            return false;
        return true;
    }

}
