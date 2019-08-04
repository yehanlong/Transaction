package com.transaction.entity;

public class account {
    public String currency; // 币种
    public float active; // 可用余额
    public float frozen; // 冻结余额

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public float getActive() {
        return active;
    }

    public void setActive(float active) {
        this.active = active;
    }

    public float getFrozen() {
        return frozen;
    }

    public void setFrozen(float frozen) {
        this.frozen = frozen;
    }
}
