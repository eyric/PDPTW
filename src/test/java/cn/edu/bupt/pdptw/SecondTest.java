package cn.edu.bupt.pdptw;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.pdptw.configuration.exception.InvalidFileFormatException;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.web.controller.IndexController;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;

public class SecondTest {
    public static void main(String[] args) {
        String path = "D:\\code\\java\\PDPTW\\resources\\test\\data";
        String output = "D:\\code\\java\\PDPTW\\resources\\test\\li_lim_benchmark\\seconds.txt";
        String[] dataset = new String[]{"pdptw100", "pdptw200", "pdptw400", "pdptw600"};

        for (String d : dataset) {
            List<String> strings = IndexController.traverseFolder(path + "/" + d);
            for (String s : strings) {
                StringBuilder cont = new StringBuilder(s + "\t");
                for (int i = 0; i < 3; i++) {
                    Configuration configuration = Configuration.defaultCfg(d + "/" + s);
                    Instance instance = new Instance();
                    try {
                        Solution solve = instance.solve(configuration);
                        cont.append(solve.getSecond()).append("\t");

                    } catch (IOException | InvalidFileFormatException | ParseException e) {
                        e.printStackTrace();
                    }
                }
                cont.append("\n");
                FileUtil.writeFile(output, cont.toString());


            }
        }

    }
}
