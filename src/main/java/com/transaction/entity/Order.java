package com.transaction.entity;

import java.util.LinkedList;
import java.util.List;

public class Order {
    private double price;
    private double count;

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }
}
