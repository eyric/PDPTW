package cn.edu.bupt.pdptw.utils.bmap;


/**
 * Auto-generated: 2019-03-05 9:0:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Location {
    /**
     * 经度
     */
    private double lng;
    /**
     * 纬度
     */
    private double lat;

    public Location() {
    }

    public Location(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLng() {
        return lng;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLat() {
        return lat;
    }

    @Override
    public String toString() {
        return "Location{" +
                "lng=" + lng +
                ", lat=" + lat +
                '}';
    }
}
