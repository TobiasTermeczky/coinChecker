package nl.yzaazy.coinchecker;

import com.orm.SugarRecord;

public class CoinToCheck extends SugarRecord<CoinToCheck> {

    String name;

    public CoinToCheck(){

    }

    CoinToCheck(String name){
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
