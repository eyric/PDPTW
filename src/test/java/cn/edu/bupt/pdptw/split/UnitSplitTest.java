package cn.edu.bupt.pdptw.split;

import cn.edu.bupt.pdptw.algorithm.split.algo.SDPDP;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.DefaultConfigReader;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.PickupRequest;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class UnitSplitTest {
    private static final String bpath = "D:\\code\\java\\PDPTW\\resources\\test\\split\\";

    public static void main(String[] args) throws IOException, InvalidFileFormatException {
        String path = FileUtil.basePath() + "/resources/test/data/";
        String[] folder = new String[]{"pdptw100", "pdptw200", "pdptw400", "pdptw600", "pdptw800", "pdptw1000"};


        for (String f : folder) {
            List<String> strings = SDPDP.traverseFolder(path + f);
            for (String s : strings) {
                String name = f + "/" + s;
                System.out.println(name);
                String c = split(name);
                FileUtil.writeFile(bpath + name, c);
            }
        }
    }

    private static String split(String name) throws IOException, InvalidFileFormatException {
        DefaultConfigReader loader = new DefaultConfigReader();
        Configuration configuration = Configuration.defaultCfg(name);
        List<Request> requests = loader.loadRequests(configuration);

        List<PickupRequest> pickups = requests.stream()
                .filter(r -> (r.getType() == RequestType.PICKUP))
                .map(r -> (PickupRequest) r)
                .collect(Collectors.toList());

        int start = 1;
        StringBuilder cont = new StringBuilder();
        for (Request request : pickups) {
            for (int i = 0; i < request.getVolume(); i++) {
                Request pick = request.copy();
                Request delivery = request.getSibling().copy();

                pick.setVolume(1);
                pick.setId(start++);
                delivery.setVolume(-1);
                delivery.setId(start++);

                pick.setSibling(delivery);
                delivery.setSibling(pick);

                cont.append(pick.printFile()).append("\n").
                        append(delivery.printFile()).append("\n");
            }
        }
        return cont.toString();
    }
}
