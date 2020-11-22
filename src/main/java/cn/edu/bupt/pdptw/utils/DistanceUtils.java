package cn.edu.bupt.pdptw.utils;

//import cn.edu.bupt.pdptw.model.Location;

import cn.edu.bupt.pdptw.utils.bmap.Location;

/**
 * 关于距离计算的工具类
 */
public class DistanceUtils {
    /**
     * 欧式距离
     */
    public static double euclideanDistance(cn.edu.bupt.pdptw.model.Location p1, cn.edu.bupt.pdptw.model.Location p2) {
        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
    }

    /**
     * 地球半径
     */
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 根据经纬度计算实际距离
     */
    public static double earthDistance(Location x, Location y) {
        double lat1 = x.getLat();
        double lng1 = x.getLng();
        double lat2 = y.getLat();
        double lng2 = y.getLng();
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000d) / 10000d;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 计算编码的相似度
     *
     * @param str1
     * @param str2
     * @return
     */
    public static double ratio(String str1, String str2) {
        int[][] d;
        int n = str1.length();
        int m = str2.length();
        int i;
        int j;
        char ch1;
        char ch2;
        int temp;
        if (n == 0) {
            return 0;
        }
        if (m == 0) {
            return 0;
        }
        d = new int[n + 1][m + 1];
        for (i = 0; i <= n; i++) {
            d[i][0] = i;
        }
        for (j = 0; j <= m; j++) {
            d[0][j] = j;
        }
        for (i = 1; i <= n; i++) {
            ch1 = str1.charAt(i - 1);
            for (j = 1; j <= m; j++) {
                ch2 = str2.charAt(j - 1);
                if (ch1 == ch2) {
                    temp = 0;
                } else {
                    temp = 2;
                }
                d[i][j] =
                        Math.min(Math.min(d[i - 1][j] + 1, d[i][j - 1] + 1), Math.min(d[i][j - 1] + 1, d[i - 1][j - 1] + temp));
            }
        }
        return (double) (m + n - d[n][m]) / (double) (m + n);
    }
}
