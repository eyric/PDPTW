package cn.edu.bupt.pdptw.algorithm.agmoipso.model;

/**
 * 客户类型
 */
public enum CustomType {
    Strategic(1), //战略客户
    VIP(2),       //普通客户
    Prospective(3); //潜在客户

    private int code;

    CustomType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public static CustomType get(int code) {
        for (CustomType type : CustomType.values()) {
            if (type.getCode() == code)
                return type;
        }
        throw new RuntimeException("客户类型不存在");
    }
}
