package com.rocketmiles.cashier;

import com.rocketmiles.cashier.errors.UnknownDenominationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class DefaultCashier implements ICashier {
    private Map<USD, Integer> till; // the money holder
    private int total = 0;
    Logger logger = LoggerFactory.getLogger(DefaultCashier.class);

    public DefaultCashier(String[] initialDollars) throws Exception {
        till = new HashMap<USD, Integer>();
        addArrayOfCurrencyAmounts(initialDollars);
    }

    public DefaultCashier() {
        till = new HashMap<USD, Integer>();
    }

    public boolean add(String currency) throws UnknownDenominationException {
        USD currencyInUSD = toCurrency(currency);
        int count = getCurrencyAmount(currencyInUSD);
        till.put(currencyInUSD, count + 1);
        total += currencyInUSD.value;
        return true;
    }

    private boolean add(USD currency, int amount) throws Exception {
        if (amount < 0) {
            throw new Exception("incorrect amount " + Integer.toString(amount));
        }
        int count = getCurrencyAmount(currency);
        till.put(currency, count + amount);
        total += currency.value * amount;
        return true;
    }

    /**
     * @param currencies takes the amounts of the following format [$20s, $10s, $5s, $2s, $1s]
     * @return
     * @throws UnknownDenominationException
     */
    public boolean addArrayOfCurrencyAmounts(String[] currencies) throws Exception {
        isCurrencyArrayFormatted(currencies);

        add(USD.$20, Integer.valueOf(currencies[0]));
        add(USD.$10, Integer.valueOf(currencies[1]));
        add(USD.$5, Integer.valueOf(currencies[2]));
        add(USD.$2, Integer.valueOf(currencies[3]));
        add(USD.$1, Integer.valueOf(currencies[4]));

        return true;
    }

    public boolean contains(String currency) throws UnknownDenominationException {
        USD currencyInUSD = toCurrency(currency);
        int count = getCurrencyAmount(currencyInUSD);
        return count > 0;
    }

    private void isCurrencyArrayFormatted(String[] currencies) throws UnknownDenominationException {
        if (currencies.length != 5) {
            logger.error("List of currencies {} not in the correct format", Arrays.toString(currencies));
            logger.error("Expecting the amounts of the following format [$20s, $10s, $5s, $2s, $1s]");
            throw new UnknownDenominationException();
        }
    }

    public boolean remove(String currency) throws Exception {
        USD currencyInUSD = toCurrency(currency);
        int count = getCurrencyAmount(currencyInUSD);

        if (count == 0) return false;

        till.put(currencyInUSD, count - 1);
        total -= currencyInUSD.value;
        return true;
    }

    private boolean remove(USD currency, int amount) throws Exception {
        if (amount < 0) {
            logger.error("Unable to remove negative amounts {}X{}",currency, amount);
            throw new Exception("incorrect amount " + Integer.toString(amount));
        }
        int count = getCurrencyAmount(currency);
        if (count < amount) {
            logger.error("Faliure trying to remove amounts larger than what in the till",currency, amount);
            throw new Exception("Unable to remove " + Integer.toString(amount));
        }
        till.put(currency, count - amount);
        total -= currency.value * amount;
        return true;
    }

    private int getCurrencyAmount(USD currency) {
        return till.containsKey(currency) ? till.get(currency) : 0;
    }

    /**
     *
     * @param currencies counts of denominations [$20,$10,$5,$2,$1]
     * @return
     * @throws Exception
     */
    public boolean removeArrayOfCurrencyAmounts(String[] currencies) throws Exception {
        isCurrencyArrayFormatted(currencies);

        remove(USD.$20, Integer.valueOf(currencies[0]));
        remove(USD.$10, Integer.valueOf(currencies[1]));
        remove(USD.$5, Integer.valueOf(currencies[2]));
        remove(USD.$2, Integer.valueOf(currencies[3]));
        remove(USD.$1, Integer.valueOf(currencies[4]));
        return true;
    }

    /**
     * Looks to see what is in the till for possible change and returns an array of denomination counts [$20,$10,$5,$2,$1]
     * @param amount
     * @return
     * @throws Exception
     */
    public String[] change(String amount) throws Exception {
        Integer changeValue = Integer.valueOf(amount);
        Integer[] changeArray = {0,0,0,0,0} ;
        if (total < changeValue){
            logger.error("Change amount {} greater than total {}", amount , total);
            throw new Exception("change too large");
        }

        changeArray = change(changeValue, changeArray,getIntegerAmounts(),USD.values());
        if (changeArray == null) {
            logger.error("Change not possible for amount {}", amount);
            throw new Exception("unable to make change");
        }
        String[] changeCurrency= Arrays.toString(changeArray).replaceAll("[\\[\\]]", "").split("\\s*,\\s*");
        removeArrayOfCurrencyAmounts(changeCurrency);
        return changeCurrency;

    }


    /**
     * A possible solution to the change algorithim with a greedy approach
     * @param change Amount of change wanted
     * @param changeArray change already pulled out
     * @param till ammounts in the cash register
     * @param denominations available denominations
     * @return
     */
    private Integer[] change(Integer change, Integer[] changeArray, Integer[] till, USD[] denominations){
        for (int i =0 ; i < 5; i++) {
            Integer totalOfType = denominations[i].value * till[i];
            if ( change / denominations[i].value <= till[i]) {
                changeArray[i] += change / denominations[i].value;
                change -= changeArray[i] * denominations[i].value;
                if (change == 0) return changeArray;
            }
        }
        return null;
    }

    /**
     *
     * @return the number of each denomination in descending order
     */
    public String[] getAmounts() {
        String[] amounts = new String[5];

        amounts[0] = String.valueOf(getCurrencyAmount(USD.$20));
        amounts[1] = String.valueOf(getCurrencyAmount(USD.$10));
        amounts[2] = String.valueOf(getCurrencyAmount(USD.$5));
        amounts[3] = String.valueOf(getCurrencyAmount(USD.$2));
        amounts[4] = String.valueOf(getCurrencyAmount(USD.$1));

        return amounts;
    }

    private Integer[] getIntegerAmounts(){
        Integer[] amounts = new Integer[5];

        amounts[0] = getCurrencyAmount(USD.$20);
        amounts[1] = getCurrencyAmount(USD.$10);
        amounts[2] = getCurrencyAmount(USD.$5);
        amounts[3] = getCurrencyAmount(USD.$2);
        amounts[4] = getCurrencyAmount(USD.$1);
        return amounts;
    }

    /**
     * Gets the total amount in this cash register
     * @return sum of all denominations * each amount
     */
    public int getTotal() {
        return total;
    }

    private USD toCurrency(String currency) throws UnknownDenominationException {
        try {
            return USD.valueOf(currency);
        } catch (IllegalArgumentException e) {
            logger.error("Invalid Currency {} not accepted", currency);
            throw new UnknownDenominationException();
        }
    }

}