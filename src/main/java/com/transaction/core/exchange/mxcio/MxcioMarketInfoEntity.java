package com.transaction.core.exchange.mxcio;


public class MxcioMarketInfoEntity {

    /**
     * 价格
     */
    private double p;

    /**
     * 数量
     */
    private double q;

    /**
     * 类型
     */
    private Integer T;

    /**
     * 时间戳
     */
    private Long t;



    public MxcioMarketInfoEntity(double p, double q, Integer T, Long t) {
        this.p = p;
        this.q = q;
        this.T = T;
        this.t = t;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getQ() {
        return q;
    }

    public void setQ(double q) {
        this.q = q;
    }

    public Integer getT() {
        return T;
    }

    public void setT(Integer t) {
        T = t;
    }

    public Long gett() {
        return t;
    }

    public void sett(Long t1) {
        t = t1;
    }
}
