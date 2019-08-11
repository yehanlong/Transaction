package com.transaction.core.entity;

public class AmountPrice {


    // bty
    private double sy1Price;
    private double sy1Amount;

    // bty-ycc
    private double sy12Price;
    private double sy12Amount;

    // ycc
    private double sy2Price;
    private double sy2Amount;

    // 最小usdt
    private double minUSDT;

    public double getMinUSDT() {
        return minUSDT;
    }

    public void setMinUSDT(double minUSDT) {
        this.minUSDT = minUSDT;
    }




    public double getSy1Price() {
        return sy1Price;
    }

    public void setSy1Price(double sy1Price) {
        this.sy1Price = sy1Price;
    }

    public double getSy1Amount() {
        return sy1Amount;
    }

    public void setSy1Amount(double sy1Amount) {
        this.sy1Amount = sy1Amount;
    }

    public double getSy12Price() {
        return sy12Price;
    }

    public void setSy12Price(double sy12Price) {
        this.sy12Price = sy12Price;
    }

    public double getSy12Amount() {
        return sy12Amount;
    }

    public void setSy12Amount(double sy12Amount) {
        this.sy12Amount = sy12Amount;
    }

    public double getSy2Price() {
        return sy2Price;
    }

    public void setSy2Price(double sy2Price) {
        this.sy2Price = sy2Price;
    }

    public double getSy2Amount() {
        return sy2Amount;
    }

    public void setSy2Amount(double sy2Amount) {
        this.sy2Amount = sy2Amount;
    }

    @Override
    public String toString() {
        return "AmountPrice{" +
                "sy1Price=" + sy1Price +
                ", sy1Amount=" + sy1Amount +
                ", sy12Price=" + sy12Price +
                ", sy12Amount=" + sy12Amount +
                ", sy2Price=" + sy2Price +
                ", sy2Amount=" + sy2Amount +
                ", minUSDT=" + minUSDT +
                '}';
    }
}
