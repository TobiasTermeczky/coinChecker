package nl.yzaazy.coinchecker.Objects;

import com.orm.SugarRecord;

public class TrackedCoin extends SugarRecord<TrackedCoin> {

    public String name;
    public Coin coin;

    public TrackedCoin() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Coin getCoin() {
        return coin;
    }

    public void setCoin(Coin coin) {
        this.coin = coin;
    }
}
