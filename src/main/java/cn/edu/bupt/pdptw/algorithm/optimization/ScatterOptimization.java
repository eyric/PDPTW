package cn.edu.bupt.pdptw.algorithm.optimization;

import cn.edu.bupt.pdptw.algorithm.improvement.OperatorSelector;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Request;
import cn.edu.bupt.pdptw.model.RequestType;
import cn.edu.bupt.pdptw.model.Solution;
import cn.edu.bupt.pdptw.model.Vehicle;

import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.util.Comparator.comparingDouble;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

public class ScatterOptimization implements OptimizationAlgorithm {
    private Solution solution;
    private AdaptiveMemory adaptiveMemory;
    private Configuration configuration;
    private AtomicBoolean shouldStop = new AtomicBoolean(false);

    private List<Solution> population = new ArrayList<>();
    private List<Solution> referSet = new ArrayList<>();

    private int size;
    private int b1;
    private int b2;
    private int maxGen;
    private Solution bestS;
    private static DecimalFormat decimalFormat = new DecimalFormat("#.00");
    private Random random = new Random(System.currentTimeMillis());

    @Override
    public Solution optimize() {
        //1.生成初始解集
        for (int i = 0; i < size; i++) {
            population.add(OperatorSelector.select().generate(solution));
        }

        for (int i = 0; i < maxGen; i++) {
            //2.生成参考集
            genRefer();

            //3.解评价
            evaluate();

            //4.子集产生与重组
            segReg();
        }

        return bestS;
    }

    /**
     * 生成参考集
     */
    private void genRefer() {
        referSet = new ArrayList<>();
        //B = B1 + B2
        // 计算每个编码与其他所有编码的多样性距离，选取多样性距离最大的前B1个解作为多样性解
        double[][] varDis = new double[population.size()][population.size()];
        for (int i = 0; i < size; i++) {
            varDis[i][i] = 0;
            for (int j = 0; j < i; j++) {
                double v = CalVarDis(population.get(i), population.get(j));
                varDis[i][j] = varDis[j][i] = v;
            }
        }

        //计算平均多样性距离
        for (int i = 0; i < population.size(); i++) {
            double[] di = varDis[i];
            Solution sc = population.get(i);
            double dd = 0;
            for (double d : di) {
                dd += d;
            }

            sc.setDiversity(Math.abs(dd));
        }

        //按最优质排序
        population = population.stream().collect(
                collectingAndThen(
                        toCollection(() ->
                                new TreeSet<>(
                                        comparingDouble(Solution::getObjectiveValue))), ArrayList::new)
        );

        //如果备选解集的数量少于参考集大小，直接将备选集作为参考集
        if (population.size() <= (b1 + b2)) {
            referSet = population;
            return;
        }

        //优质解
        List<Solution> solutionCodes = population.subList(0, b1);

        referSet.addAll(solutionCodes);

        //按多样性进行排序
        List<Solution> Np_B1 = population.subList(b1, population.size());
        Np_B1.sort((o1, o2) -> Double.compare(o2.getDiversity(), o1.getDiversity()));

        //多样性解
        referSet.addAll(Np_B1.subList(1, b2 + 1));
    }

    /**
     * 解评价
     */
    protected void evaluate() {
        for (Solution sc : referSet) {
            if (sc.betterThan(bestS, configuration.getAlgorithms().getObjective())) {
                bestS = sc;
            }
        }
    }


    /**
     * 子集产生与重组
     */
    private void segReg() {
        //清空备选解集
        population = new ArrayList<>();
        //精英保留
        population.add(bestS);

        for (int i = 0; i < referSet.size(); i++) {
            for (int j = 0; j < i; j++) {
                //选取两个解组成解集对，生成两个新的解
                List<Solution> solutionCodes = reOrg(referSet.get(i), referSet.get(j));

                if (solutionCodes.size() > 0) {

                    population.addAll(solutionCodes);
                }
            }
        }
    }

    /**
     * 路径重连
     */
    private List<Solution> reOrg(Solution sc1, Solution  sc2) {
        int size = sc1.getVehicles().size();
        int theta = random.nextInt(size - 1);

        List<Vehicle> part11 = sc1.getVehicles().subList(0, theta);
        List<Vehicle> part12 = sc1.getVehicles().subList(theta, size);
        List<Vehicle> part21 = sc2.getVehicles().subList(0, theta);
        List<Vehicle> part22 = sc2.getVehicles().subList(theta, size);



        List<Solution> result = new ArrayList<>();


        return result;
    }

    /**
     * 计算多样性距离
     */
    private double CalVarDis(Solution s1, Solution s2) {
        int sco = 0;//分配到相同车辆的订单数
        int c2u = 0; //使用的车辆数
        int cn = s1.getVehicles().size();
        int on = s1.getRequests().size();
        Set<String> sqs1 = new HashSet<>(); //编码1相邻序列对集合
        Set<String> sqs2 = new HashSet<>(); //编码2相邻序列对集合

        for (int i = 0; i < cn; i++) {
            Vehicle vc1 = s1.getVehicles().get(i);
            Vehicle vc2 = s2.getVehicles().get(i);
            c2u++;
            if (vc1.getRoute() != null && vc2.getRoute().getRequests() != null) {
                for (Request oc : vc1.getRoute().getRequests()) {
                    if (oc.getType() == RequestType.PICKUP && vc2.getRoute().getRequests().contains(oc)) {
                        sco++;
                    }
                }
            }
            sqs(sqs1, vc1);
            sqs(sqs2, vc2);
        }

        int sqn = 0;  //相同相邻序列对数目
        for (String sq : sqs1) {
            if (sqs2.contains(sq)) {
                sqn++;
            }
        }

        double x = 1 - (0.5 * sco / on + 0.5 * sqn / (2 * on - c2u));

        if (x <= 0) {
            x = 0.01;
        }
        if (x >= 1) {
            x = 0.99;
        }

        if (Double.isNaN(x)) {
            return 0;
        }

        return Double.parseDouble(decimalFormat.format(-Math.log(x / (2 - x))));
    }

    private void sqs(Set<String> sqs2, Vehicle vc2) {
        if (vc2.getRoute().getRequests() != null) {
            int size = vc2.getRoute().getRequests().size();
            for (int j = 1; j < size; j++) {
                sqs2.add(vc2.getRoute().getRequests().get(j - 1).getId() + "," +
                        vc2.getRoute().getRequests().get(j).getId());
            }
        }
    }

    @Override
    public OptimizationAlgorithm setConfiguration(Configuration configuration) {
        this.configuration = configuration;
        return this;
    }

    @Override
    public OptimizationAlgorithm setSolution(Solution solution) {
        this.solution = solution;
        this.bestS = solution;
        return this;
    }

    @Override
    public OptimizationAlgorithm setAdaptiveMemory(AdaptiveMemory adaptiveMemory) {
        this.adaptiveMemory = adaptiveMemory;
        return this;
    }

    @Override
    public Solution getSolution() {
        return this.solution;
    }

    @Override
    public AdaptiveMemory getAdaptiveMemory() {
        return this.adaptiveMemory;
    }

    @Override
    public OptimizationAlgorithm createShallowCopy() {
        return new ScatterOptimization()
                .setConfiguration(configuration)
                .setSolution(solution)
                .setAdaptiveMemory(adaptiveMemory);
    }

    @Override
    public void stopOptimization() {
        shouldStop.set(true);
    }
}
