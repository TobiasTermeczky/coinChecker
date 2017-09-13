package nl.yzaazy.coinchecker.Objects;

import com.orm.SugarRecord;

public class Coin extends SugarRecord<TrackedCoin> {
    String Id;
    String Url;
    String ImageUrl;
    String Name;
    String CoinName;
    String FullName;
    String Algorithm;
    String ProofType;
    String FullyPremined;
    String TotalCoinSupply;
    String PreMinedValue;
    String TotalCoinsFreeFloat;
    String SortOrder;

    public String getCoinName() {
        return CoinName;
    }
}
