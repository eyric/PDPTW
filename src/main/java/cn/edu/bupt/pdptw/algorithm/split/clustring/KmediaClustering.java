package cn.edu.bupt.pdptw.algorithm.split.clustring;


import cn.edu.bupt.pdptw.algorithm.split.model.ClusterResult;
import cn.edu.bupt.pdptw.algorithm.split.model.TransportOrder;
import cn.edu.bupt.pdptw.algorithm.split.utils.ClusterUtil;
import cn.edu.bupt.pdptw.model.Request;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * K-Media聚类
 */
public class KmediaClustering extends KMeansClustering<TransportOrder> {

    public KmediaClustering(List<TransportOrder> mUnits, int k, int i) {
        super(mUnits, k, i);
    }

    @Override
    public double similarScore(TransportOrder o1, TransportOrder o2) {
        return ClusterUtil.distance(o1.getPick(), o2.getPick());
    }

    @Override
    public boolean equals(TransportOrder o1, TransportOrder o2) {
        if (o1 == null || o2 == null) {
            return false;
        }

        if (o1.toString() == null || o2.toString() == null) {
            return false;
        }

        return o1.getPick().equals(o2.getPick()) && o1.getDelivery().equals(o2.getDelivery());
    }

    @Override
    public TransportOrder getCenterT(List<TransportOrder> list) {
        return ClusterUtil.center(list);
    }

    public List<ClusterResult> result() {
        if (getResult() == null)
            return Collections.emptyList();

        List<ClusterResult> results = new ArrayList<>();
        for (List<TransportOrder> group : getResult()) {
            ClusterResult result = new ClusterResult();
            List<Request> requests = new ArrayList<>();
            for (TransportOrder order : group) {
                Request request = order.getPick();
                requests.add(request);
            }
            result.setRequestList(requests);
            results.add(result);

        }
        return results;
    }
}
