package com.transaction.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Order implements Comparable<Order>{
    private double price;
    private double am;

//    public Order(){}
//
//    public Order(double price, double am) {
//        this.price = price;
//        this.am = am;
//    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getAm() {
        return am;
    }

    public void setAm(double am) {
        this.am = am;
    }

    @Override
    public int compareTo(Order o) {
        if(this.getPrice() > o.getPrice()){
            return 1;
        }
        if(this.getPrice() < o.getPrice()){
            return -1;
        }
        return 0;
    }
}
