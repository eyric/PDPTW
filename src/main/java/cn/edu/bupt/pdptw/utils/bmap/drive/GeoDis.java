/**
  * Copyright 2019 bejson.com 
  */
package cn.edu.bupt.pdptw.utils.bmap.drive;
import java.util.List;

/**
 * Auto-generated: 2019-03-05 9:43:54
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class GeoDis {

    private int status;
    private List<Result> result;
    private String message;
    public void setStatus(int status) {
         this.status = status;
     }
     public int getStatus() {
         return status;
     }

    public void setResult(List<Result> result) {
         this.result = result;
     }
     public List<Result> getResult() {
         return result;
     }

    public void setMessage(String message) {
         this.message = message;
     }
     public String getMessage() {
         return message;
     }

}