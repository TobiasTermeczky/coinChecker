package nl.yzaazy.coinchecker.Objects;

import com.orm.SugarRecord;

public class TrackedCoin extends SugarRecord<TrackedCoin> {

    public String name;

    public TrackedCoin(){

    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
