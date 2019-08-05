package com.transaction.core.entity.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 账户币种信息
 * @author: yhl
 * @create: 2019-08-05
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class PropertyVO {

    private String name;

    private Double total;

    private Double realactive;

    private Double valuation;

    private Double poundage;

    private Double active;

    private Double frozen;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getRealactive() {
        return realactive;
    }

    public void setRealactive(Double realactive) {
        this.realactive = realactive;
    }

    public Double getValuation() {
        return valuation;
    }

    public void setValuation(Double valuation) {
        this.valuation = valuation;
    }

    public Double getPoundage() {
        return poundage;
    }

    public void setPoundage(Double poundage) {
        this.poundage = poundage;
    }

    public Double getActive() {
        return active;
    }

    public void setActive(Double active) {
        this.active = active;
    }

    public Double getFrozen() {
        return frozen;
    }

    public void setFrozen(Double frozen) {
        this.frozen = frozen;
    }
}
