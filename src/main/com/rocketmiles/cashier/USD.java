package com.rocketmiles.cashier;

/**
 * Created by matt on 12/5/16.
 */
public enum USD {
    $20(20),
    $10(10),
    $5(5),
    $2(2),
    $1(1);

    public int value;

    USD(int i) {
       this.value = i;
    }
}
