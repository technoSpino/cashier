package com.rocketmiles.cashier;

import com.rocketmiles.cashier.errors.UnknownDenominationException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DefaultCashierTest {

    ICashier cashier;

    @Before
    public void setUp() throws Exception {
        cashier = new DefaultCashier();
    }

    @Test
    public void addCurrency() throws Exception {
        cashier.add("$1");
        cashier.add("$5");
        assertEquals(true, cashier.contains("$5"));
        assertEquals(true, cashier.contains("$1"));
    }

    @Test
    public void addInvalidCurrencyThrowsUnknownDenominationException() throws Exception {
        Exception denominationException = null;
        try {
            cashier.add("£1");
        } catch (UnknownDenominationException e) {
            denominationException = e;
        }
        assert (denominationException != null);
    }

    @Test
    public void addMultipleCurrencies() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5", "10"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        assertEquals(true, cashier.contains("$20"));
        assertEquals(true, cashier.contains("$10"));
        assertEquals(false, cashier.contains("$5"));
    }

    @Test
    public void addInvalidMultipleCurrenciesThrowsError() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5"};
        Exception exception = null;
        try {
            cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        } catch (Exception e) {
            exception = e;
        }
        assert (exception != null);

    }

    @Test
    public void removeCurrency() throws Exception {
        cashier.add("$1");
        cashier.add("$5");
        cashier.remove("$5");
        assertEquals(false, cashier.contains("$5"));
        assertEquals(true, cashier.contains("$1"));
    }

    @Test
    public void removeCurrencyReturnsFalseIfNoBills() throws Exception {
        cashier.add("$1");
        cashier.add("$5");
        assertEquals(true, cashier.remove("$5"));
        assertEquals(false, cashier.remove("$5"));
        assertEquals(true, cashier.contains("$1"));
    }

    @Test
    public void removeInvalidCurrency() {
        Exception denominationException = null;
        try {
            cashier.remove("£1");
        } catch (Exception e) {
            denominationException = e;
        }
        assert (denominationException != null);
    }

    @Test
    public void removeMultiple() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5", "10"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        assertEquals(true, cashier.contains("$20"));
        assertEquals(true, cashier.contains("$10"));
        assertEquals(false, cashier.contains("$5"));

        String[] removeAmounts = {"1", "1", "0", "3", "5"};
        cashier.removeArrayOfCurrencyAmounts(removeAmounts);

        String[] amounts = cashier.getAmounts();
        String[] expectedAmounts = {"2", "2", "0", "2", "5"};
        assertArrayEquals(expectedAmounts, amounts);

    }

    @Test
    public void totalIncreasesAfterAdd() throws Exception {
        cashier.add("$1");
        cashier.add("$1");

        assertEquals(2, cashier.getTotal());
    }

    @Test
    public void totalIncreasesAfterMultipleAdd() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5", "10"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);

        assertEquals(110, cashier.getTotal());
    }

    @Test
    public void totalDecreasesAfterRemove() throws Exception {
        cashier.add("$1");
        cashier.add("$1");
        cashier.remove("$1");

        assertEquals(1, cashier.getTotal());
    }

    @Test
    public void totalDecreasesAfterMultipleRemove() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5", "10"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        String[] removeAmounts = {"1", "1", "0", "3", "5"};
        cashier.removeArrayOfCurrencyAmounts(removeAmounts); //{"2","2","0","2","5"}

        assertEquals(69, cashier.getTotal());
    }

    @Test
    public void canGiveChange() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5", "10"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        String[] change = cashier.change("40");
        String[] expectedArray = {"2", "0", "0", "0", "0"};
        assertArrayEquals(expectedArray, change);

        String[] change2 = cashier.change("30");
        String[] expectedArray2 = {"1", "1", "0", "0", "0"};
        assertArrayEquals(expectedArray2, change2);

    }

    @Test
    public void canGiveChangeAndRemoveFromTill() throws Exception {
        String[] currencyAmounts = {"3", "3", "0", "5", "10"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        String[] change = cashier.change("11");
        String[] expectedChangeArray = {"0", "1", "0", "0", "1"};
        assertArrayEquals(expectedChangeArray, change);

        String[] expectedTill = {"3", "2", "0", "5", "9"};
        assertArrayEquals(expectedTill, cashier.getAmounts());
    }

    @Test
    public void canGiveChange35() throws Exception {
        String[] currencyAmounts = {"1", "3", "1", "0", "1"};
        cashier.addArrayOfCurrencyAmounts(currencyAmounts);
        String[] change = cashier.change("35");
        String[] expectedChangeArray = {"1", "1", "1", "0", "0"};
        assertArrayEquals(expectedChangeArray, change);

        String[] expectedTill = {"0", "2", "0", "0", "1"};
        assertArrayEquals(expectedTill, cashier.getAmounts());
    }

    @Test
    public void cannotGiveChange95() throws Exception {
        Exception exception = null;
        try {
            String[] currencyAmounts = {"0", "3", "1", "100", "0"};
            cashier.addArrayOfCurrencyAmounts(currencyAmounts);
            String[] change = cashier.change("95");
            String[] expectedChangeArray = {"0", "1", "1", "0", "0"};
            assertArrayEquals(expectedChangeArray, change);

            String[] expectedTill = {"0", "2", "0", "0", "1"};
            assertArrayEquals(expectedTill, cashier.getAmounts());
        } catch (Exception e) {
            exception = e;
        }
        assert(exception != null);
    }
}