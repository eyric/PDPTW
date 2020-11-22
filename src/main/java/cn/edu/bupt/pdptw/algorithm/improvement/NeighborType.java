package cn.edu.bupt.pdptw.algorithm.improvement;

public enum NeighborType {
    RELOCATED_DELIVERY("relocated_delivery",1),
    RELOCATED_REQUEST("relocated_request",2),
    EXCHANGE_REQUEST_INLINE("exchange_request_inline",3),
    EXCHANGE_REQUEST_OUTLINE("exchange_request_outline",4),
    INSERT_REQUEST("insert_request",5),
    REMOVE_REQUEST("remove_request",6),
    PD_SHIFT("pd-shift",7),
    PD_EXCHANGE("pd-exchange",8),
    PD_REARRANGE("pd-rearrange",9);

    private String name;
    private int code;

    NeighborType(String name, int code) {
        this.name = name;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public int getCode() {
        return code;
    }

    public static NeighborType get(int code){
        for (NeighborType type:NeighborType.values()){
            if (type.getCode() == code)
                return type;
        }
        return RELOCATED_DELIVERY;
    }
}
