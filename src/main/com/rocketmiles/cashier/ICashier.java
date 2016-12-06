package com.rocketmiles.cashier;

public interface ICashier {
    boolean add(String currency) throws Exception;
    boolean addArrayOfCurrencyAmounts(String[] currencies) throws Exception;
    boolean contains(String currency) throws Exception;
    boolean remove(String currency) throws Exception;
    boolean removeArrayOfCurrencyAmounts(String[] currencies) throws Exception;
    String[] change(String amount) throws Exception;
    String[] getAmounts();
    int getTotal();
}
