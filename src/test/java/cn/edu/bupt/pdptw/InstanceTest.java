package cn.edu.bupt.pdptw;

import cn.edu.bupt.pdptw.algorithm.split.algo.SDPDP;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Solution;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;


public class InstanceTest {
    private static final String wpath = "D:\\code\\java\\PDPTW\\resources\\test\\li_lim_benchmark\\usplit.txt";

    public static void main(String[] args) throws ParseException, InvalidFileFormatException, IOException {
        String path = FileUtil.basePath() + "/resources/test/data/";
        String[] folder = new String[]{"pdptw100", "pdptw200", "pdptw400", "pdptw600", "pdptw800", "pdptw1000"};


        for (String f : folder) {
            List<String> strings = SDPDP.traverseFolder(path + f);
            for (String s : strings) {
                String name = s.split("\\.")[0];
                utest(f, name);
            }
        }

    }

    private static void utest(String path, String name) throws ParseException, InvalidFileFormatException, IOException {
        try {
            long start = System.currentTimeMillis();
            Configuration configuration = Configuration.defaultCfg("/" + path + "/" + name + ".txt", "3/" + path + "/" + name + ".json", true);
            Instance instance = new Instance();
            Solution solution = instance.solve(configuration);

            String sb = name + "\t" + solution.getObjectiveValue() + "\t" +
                    solution.getRoutes().size() + "\t" + solution.getRate() + "\t" + (System.currentTimeMillis() - start) / 1000.0 + "\n";
            System.out.println(sb);
            FileUtil.writeFile(wpath, sb);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
