package cn.edu.bupt.pdptw.utils;




import cn.edu.bupt.pdptw.utils.bmap.GeoResult;
import cn.edu.bupt.pdptw.utils.bmap.Location;
import cn.edu.bupt.pdptw.utils.bmap.drive.GeoDis;

import java.util.List;
import java.util.Objects;

/**
 * 百度地图工具类
 */
public class BmapUtils {
    /**
     * 百度地图appKey
     */
    private static final String appKey = "xCu3FguwuzydfnufqVd1bQAgRNm827Dh";
    /**
     * 百度地图api
     */
    private static final String geoUrl = "http://api.map.baidu.com/geocoder?address=%s&output=json&key=";
    private static final String driveUrl = "http://api.map.baidu.com/routematrix/v2/driving?output=json&origins=%.6f,%.6f&destinations=%.6f,%.6f&ak=";
    /**
     * 地球半径
     */
    private static final double EARTH_RADIUS = 6378.137;

    /**
     * 获取指定位置经纬度
     *
     * @param addr 地址名称
     * @return {lat,lng}
     */
    public static Location geocoder(String addr) {
        String result = HttpUtils.sendGet(String.format(geoUrl + appKey, addr));
        GeoResult geoResult = JsonUtils.toObject(result, GeoResult.class);
        if (Objects.isNull(geoResult)) {
            return null;
        }
        return geoResult.getResult().getLocation();
    }

    /**
     * 获取两点间驾驶距离,如果获取失败，计算两点间的直线距离
     *
     * @param addr1 起点
     * @param addr2 终点
     * @return km
     */
    public static double distance(String addr1, String addr2) {
        return distance(geocoder(addr1), geocoder(addr2));
    }

    public static double distance(Location location1, Location location2) {
        if (Objects.isNull(location1) || Objects.isNull(location2)) {
            return -1;
        }
        String result = HttpUtils.sendGet(String.format(driveUrl + appKey, location1.getLat(), location1.getLng(), location2.getLat(), location2.getLng()));


        GeoDis geoDis = JsonUtils.toObject(result, GeoDis.class);

        if (geoDis == null){
            return DistanceUtils.earthDistance(location1,location2);
        }

        return geoDis.getResult().get(0).getDistance().getValue() / 1000.0;
    }



}
