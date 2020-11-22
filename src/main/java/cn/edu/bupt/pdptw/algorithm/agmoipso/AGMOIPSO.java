package cn.edu.bupt.pdptw.algorithm.agmoipso;

import cn.edu.bupt.pdptw.algorithm.agmoipso.model.*;
import lombok.Data;
import org.uma.jmetal.solution.Solution;
import org.uma.jmetal.util.archive.impl.AdaptiveGridArchive;
import org.uma.jmetal.util.comparator.DominanceComparator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

/**
 * 基于自适应网格的多目标免疫粒子群算法求解 HFPDP-TWCPDP
 */
@Data
public class AGMOIPSO {
    /**
     * 种群
     */
    private List<PdpSolution> population;
    /**
     * 外部存档
     */
    private AdaptiveGridArchive archive;

    /**
     * 外部存档的大小
     */
    private int M;

    /**
     * 自适应网格大小
     */
    private int D;

    /**
     * Pareto比较器
     */
    private DominanceComparator comparator;

    /**
     * 种群大小
     */
    private int size;

    /**
     * 迭代次数
     */
    private int itr;

    /**
     * 最大最小惯性因子
     */
    private double wMax, wMin;

    /**
     * 随机数种子
     */
    protected Random random;

    /**
     * 交换序列
     */
    private ArrayList<ArrayList<SO>> listV = new ArrayList<>();

    /**
     * 一颗粒子历代中出现最好的解
     */
    private HashMap<Integer, PdpSolution> Pd = new HashMap<>();

    /**
     * 整个粒子群经历过的的最好的解
     */
    private PdpSolution Pgd;

    public AGMOIPSO(int m, int d, int size, int itr, PdpProblem problem) {
        this.M = m;
        this.D = d;
        this.size = size;
        this.itr = itr;
        random = new Random(System.currentTimeMillis());

        population = new ArrayList<>(size);
        archive = new AdaptiveGridArchive(M, D, problem.getNumberOfObjectives());
        comparator = new DominanceComparator();

        initPopulation(problem);
    }

    /**
     * 生成初始种群
     */
    private void initPopulation(PdpProblem problem) {
        Solution solution = problem.createSolution();

        int n = size * size;
        List<Solution> initSet = new ArrayList<>(n);


    }


    /**
     * 运行算法
     */
    private Result run(PdpProblem problem) {
        initPopulation(problem);

        return null;
    }

    /**
     * 计算惯性权重
     */
    private double calW(int k) {
        return wMax - (wMax - wMin) * k * k / itr / itr;
    }

    /**
     * 计算粒子运动速度
     *
     * @param k 种群中第k个粒子
     * @param w 惯性因子
     */
    private List<SO> pso(int k, double w) {
        ArrayList<SO> vii = new ArrayList<>();
        //更新公式：Vii=wVi+ra(Pid-Xid)+rb(Pgd-Xid)
        //第一部分，自身交换对
        int len = (int) (w * listV.get(k).size());
        for (int i = 0; i < len; i++) {
            vii.add(listV.get(k).get(i));
        }

        //第二部分，和当前粒子中出现最好的结果比较，得出交换序列
        //ra(Pid-Xid)
        ArrayList<SO> a = minus(population.get(k).getVariableArr(), Pd.get(k).getVariableArr());
        float ra = random.nextFloat();
        len = (int) (ra * a.size());
        for (int i = 0; i < len; i++) {
            vii.add(a.get(i));
        }

        //第三部分，和全局最优的结果比较，得出交换序列
        //rb(Pgd-Xid)
        ArrayList<SO> b = minus(population.get(k).getVariableArr(), Pgd.getVariableArr());
        float rb = random.nextFloat();
        len = (int) (rb * b.size());
        for (int i = 0; i < len; i++) {
            vii.add(b.get(i));
        }
        return vii;
    }


    /**
     * 初始化自身的交换序列即惯性因子
     */
    private void initListV() {
        for (int i = 0; i < size; i++) {
            ArrayList<SO> list = new ArrayList<>();
            int n = random.nextInt(size - 1) % (size);    //随机生成一个数，表示当前粒子需要交换的对数
            for (int j = 0; j < n; j++) {
                //生成两个不相等的编号x,y
                int x = random.nextInt(size - 1) % (size);
                int y = random.nextInt(size - 1) % (size);
                while (x == y) {
                    y = random.nextInt(size - 1) % (size);
                }
                //x不等于y
                SO so = new SO(x, y);
                list.add(so);
            }
            listV.add(list);
        }
    }

    /**
     * 生成交换对，把a变成和b一样，返回需要交换的下标对列表
     */
    public static ArrayList<SO> minus(PdpVariable[] a, PdpVariable[] b) {
        PdpVariable[] tmp = a.clone();
        ArrayList<SO> list = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < b.length; i++) {
            if (tmp[i] != b[i]) {
                //在tmp中找到和b[i]相等的值，将下标存储起来
                for (int j = i + 1; j < tmp.length; j++) {
                    if (tmp[j] == b[i]) {
                        index = j;
                        break;
                    }
                }
                SO so = new SO(i, index);
                list.add(so);
            }
        }
        return list;
    }

    /**
     * 执行交换，生成新解
     */
    public static PdpSolution exchange(PdpVariable[] path, List<SO> vii) {
        return null;
    }
}
