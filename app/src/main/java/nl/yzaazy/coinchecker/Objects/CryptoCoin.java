package nl.yzaazy.coinchecker.Objects;

public class CryptoCoin {
    private String id;
    private String name;
    private String symbol;
    private Double price_usd;
    private Double price_eur;
    private Double percent_change_24h;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Double getPrice_usd() {
        return price_usd;
    }

    public void setPrice_usd(Double price_usd) {
        this.price_usd = price_usd;
    }

    public Double getPrice_eur() {
        return price_eur;
    }

    public void setPrice_eur(Double price_eur) {
        this.price_eur = price_eur;
    }

    public Double getPercent_change_24h() {
        return percent_change_24h;
    }

    public void setPercent_change_24h(Double percent_change_24h) {
        this.percent_change_24h = percent_change_24h;
    }
}
