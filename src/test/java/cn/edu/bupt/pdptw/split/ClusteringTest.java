package cn.edu.bupt.pdptw.split;

import cn.edu.bupt.pdptw.algorithm.split.algo.BufferClustering;
import cn.edu.bupt.pdptw.algorithm.split.algo.SDPDP;
import cn.edu.bupt.pdptw.algorithm.split.clustring.KmediaClustering;
import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.algorithm.split.model.TransportOrder;
import cn.edu.bupt.pdptw.algorithm.split.utils.ClusterUtil;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.*;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ClusteringTest {
    private static final String wpath = "D:\\code\\java\\PDPTW\\resources\\test\\li_lim_benchmark\\cluster.txt";

    public static void main(String[] args) throws IOException, InvalidFileFormatException, ParseException {

        String path = FileUtil.basePath() + "/resources/test/data/";
        String[] folder = new String[]{"pdptw100", "pdptw200", "pdptw400", "pdptw600", "pdptw800", "pdptw1000"};


        for (String f : folder) {
            List<String> strings = SDPDP.traverseFolder(path + f);
            for (String s : strings) {
                String name = s.split("\\.")[0];
                try {
                    test(f + "/" + s, name);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static void test(String name, String s) throws IOException, InvalidFileFormatException, ParseException {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg(name);
        List<Request> requests = loader.loadRequests(configuration);
        List<Vehicle> vehicles = loader.loadVehicles(configuration);

        //基于缓冲区聚类
        BufferClustering bc = new BufferClustering();
        List<ClusterResult> cluster = bc.clustering(vehicles, requests, SDPDP.para(requests, vehicles));

        List<PickupRequest> pickups = requests.stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());
        List<TransportOrder> orderList = new ArrayList<>();
        for (PickupRequest pickupRequest : pickups) {
            orderList.add(new TransportOrder(pickupRequest, (DeliveryRequest) pickupRequest.getSibling()));
        }

        //K-media聚类
        KmediaClustering kc = new KmediaClustering(orderList, cluster.size(), 100);
        kc.clustering();
        List<ClusterResult> result = kc.result();

        String cont = s + "\t";
        cont += cluster.size() + "\t" + ClusterUtil.sc(cluster) + "\t" + ClusterUtil.sc(result) + "\n";

        FileUtil.writeFile(wpath, cont);
    }
}
