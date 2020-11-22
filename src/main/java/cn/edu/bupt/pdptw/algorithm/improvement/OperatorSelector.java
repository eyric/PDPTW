package cn.edu.bupt.pdptw.algorithm.improvement;

import java.util.Random;

public class OperatorSelector {
    // random select a NS operator
    private static final int operatorNum = NeighborType.values().length;

    public static NeighborOperator select() {
        return select(NeighborType.get(new Random().nextInt(operatorNum) + 1));
    }

    public static NeighborOperator select(NeighborType type) {
        switch (type) {
            case RELOCATED_DELIVERY:
                return new RelocatedDeliveryOperator();
            case RELOCATED_REQUEST:
                return new RelocatedRequestOperator();
            case EXCHANGE_REQUEST_INLINE:
                return new ExchangeInlineOperator();
            case EXCHANGE_REQUEST_OUTLINE:
                return new ExchangeOutlineOperator();
        }
        return new RelocatedDeliveryOperator();
    }
}
