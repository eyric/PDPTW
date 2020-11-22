package cn.edu.bupt.web.controller;

import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.configuration.FileUtil;
import cn.edu.bupt.web.service.PdptwService;
import com.google.gson.Gson;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@CrossOrigin(origins = "*")
public class IndexController {
    @Resource(name = "pdptwService")
    private PdptwService service;

    @RequestMapping("/index")
    public String index() {
        return "visualization";
    }

    @RequestMapping("/solomon")
    @ResponseBody
    public String solomon(String name) {
        Configuration configuration = Configuration.defaultCfg(name);
        if (name.equalsIgnoreCase("task.txt")) {
            configuration.setVehiclesPath("resources/test/data/vehicle.json");
        }
        return service.solve(configuration);
    }

    @RequestMapping("/instance")
    @ResponseBody
    public String instance() {
        String path = FileUtil.basePath() + "/resources/test/data/";
        return new Gson().toJson(traverseFolder(path));
    }

    public static List<String> traverseFolder(String path) {
        List<String> result = new ArrayList<>();
        File file = new File(path);
        if (file.exists()) {
            File[] files = file.listFiles();
            if (null == files || files.length == 0) {
                System.out.println("文件夹是空的!");
                return Collections.emptyList();
            } else {
                for (File file2 : files) {
                    if (file2.isDirectory()) {
                        List<String> folder = traverseFolder(file2.getAbsolutePath());
                        result.addAll(folder);
                    } else {
                        String name = file2.getName();
                        if (name.endsWith(".txt")) {
                            result.add(name);
                        }

                    }
                }
            }
        }
        return result;
    }


}
