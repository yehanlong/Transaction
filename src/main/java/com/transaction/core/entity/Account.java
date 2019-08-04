package com.transaction.core.entity;

public class Account {
    private String currency; // 币种
    private double active; // 可用余额
    private double frozen; // 冻结余额

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getActive() {
        return active;
    }

    public void setActive(float active) {
        this.active = active;
    }

    public double getFrozen() {
        return frozen;
    }

    public void setFrozen(float frozen) {
        this.frozen = frozen;
    }
}
