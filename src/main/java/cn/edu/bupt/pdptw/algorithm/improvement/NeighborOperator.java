package cn.edu.bupt.pdptw.algorithm.improvement;

import cn.edu.bupt.pdptw.algorithm.insertion.GreedyInsertion;
import cn.edu.bupt.pdptw.configuration.Configuration;
import cn.edu.bupt.pdptw.model.Solution;

import java.util.*;

/**
 * Neighbor Search Operator
 */
public abstract class NeighborOperator {
    protected GreedyInsertion insertion = new GreedyInsertion();
    protected Configuration configuration = Configuration.defaultCfg();
    protected Random random = new Random(System.currentTimeMillis());

    public abstract Solution generate(Solution solution);

    public List<Solution> generate(Solution solution, int n) {
        Set<Solution> set = new HashSet<>();
        for (int i = 0; i < n; i++) {
            set.add(generate(solution));
        }
        return new ArrayList<>(set);
    }
}
