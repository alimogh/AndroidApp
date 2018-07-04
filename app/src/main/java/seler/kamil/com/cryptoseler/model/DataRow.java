package seler.kamil.com.cryptoseler.model;

/**
 * Created by Kamil on 2018-07-04.
 */

public class DataRow {

    private String token;
    private int buyPrice;
    private int actualPrice;

    public DataRow(String token, int buyPrice) {
        this.token = token;
        this.buyPrice = buyPrice;
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

    public int getActualPrice() {
        return actualPrice;
    }

    public void setActualPrice(int actualPrice) {
        this.actualPrice = actualPrice;
    }

    @Override
    public String toString() {
        return getToken()+ " "+buyPrice;
    }
}
