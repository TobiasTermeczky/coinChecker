package nl.yzaazy.coinchecker.Objects;

import com.orm.SugarRecord;

public class Coin extends SugarRecord<Coin> {

    String Symbol;
    String Name;
    String NameSymbol;
    String IconUrl;
    String IconLocal;
    Boolean IsChecked = false;
    Double priceUsd;
    Double priceEur;
    Double percentChange24h;

    public String getSymbol() {
        return Symbol;
    }

    public void setSymbol(String symbol) {
        Symbol = symbol;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getNameSymbol() {
        return NameSymbol;
    }

    public void setNameSymbol(String nameSymbol) {
        NameSymbol = nameSymbol;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }

    public String getIconLocal() {
        return IconLocal;
    }

    public void setIconLocal(String iconLocal) {
        IconLocal = iconLocal;
    }

    public Boolean getTracked() {
        return IsChecked;
    }

    public void setTracked() {
        IsChecked = true;
    }

    public void removeTracked() {
        IsChecked = false;
    }

    public Double getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(Double priceUsd) {
        this.priceUsd = priceUsd;
    }

    public Double getPriceEur() {
        return priceEur;
    }

    public void setPriceEur(Double priceEur) {
        this.priceEur = priceEur;
    }

    public Double getPercentChange24h() {
        return percentChange24h;
    }

    public void setPercentChange24h(Double percentChange24h) {
        this.percentChange24h = percentChange24h;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "Symbol='" + Symbol + '\'' +
                ", Name='" + Name + '\'' +
                ", NameSymbol='" + NameSymbol + '\'' +
                ", IconUrl='" + IconUrl + '\'' +
                ", IconLocal='" + IconLocal + '\'' +
                ", IsChecked=" + IsChecked +
                ", priceUsd=" + priceUsd +
                ", priceEur=" + priceEur +
                ", percentChange24h=" + percentChange24h +
                '}';
    }
}
