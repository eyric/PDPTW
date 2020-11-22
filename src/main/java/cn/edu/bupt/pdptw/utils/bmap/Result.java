package cn.edu.bupt.pdptw.utils.bmap;


/**
 * Auto-generated: 2019-03-05 9:0:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class Result {

    private Location location;
    private int precise;
    private int confidence;
    private String level;
    public void setLocation(Location location) {
        this.location = location;
    }
    public Location getLocation() {
        return location;
    }

    public void setPrecise(int precise) {
        this.precise = precise;
    }
    public int getPrecise() {
        return precise;
    }

    public void setConfidence(int confidence) {
        this.confidence = confidence;
    }
    public int getConfidence() {
        return confidence;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public String getLevel() {
        return level;
    }

}
