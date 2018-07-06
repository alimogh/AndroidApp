package seler.kamil.com.cryptoseler.model;

/**
 * Created by Kamil on 2018-07-04.
 */

public class DataRow {

    private String token;
    private int buyPrice;
    private int actualPriceSAT;
    private double actualPrice;
    private double amount=0.0;

    public DataRow(String token, int buyPrice, double amount) {
        this.token = token;
        this.buyPrice = buyPrice;
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(double actualPrice) {
        this.actualPrice = actualPrice;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getBuyPrice() {
        return buyPrice;
    }

    public void setBuyPrice(int buyPrice) {
        this.buyPrice = buyPrice;
    }

    public int getActualPriceSAT() {
        return actualPriceSAT;
    }

    public void setActualPriceSAT(int actualPriceSAT) {
        this.actualPriceSAT = actualPriceSAT;
    }

    @Override
    public String toString() {
        return getToken()+ " "+buyPrice+" actual: "+ actualPriceSAT +" amount: "+amount;
    }
}
