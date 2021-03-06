package nl.yzaazy.coinchecker.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;
import com.orm.SugarRecord;

import java.util.Objects;

import nl.yzaazy.coinchecker.Helpers.ImageSaver;
import nl.yzaazy.coinchecker.Helpers.SettingsHelper;
import nl.yzaazy.coinchecker.Interface.RefreshInterface;

public class Coin extends SugarRecord<Coin> implements Comparable<Coin> {

    String Symbol;
    String Name;
    String NameSymbol;
    String IconUrl;
    Boolean IsChecked = false;
    String priceUsd;
    String priceEur;
    String priceBtc;
    String percentChangeUsd24h;
    String percentChangeEur24h;
    String percentChangeBtc24h;
    int sortOrder;
    Boolean locked = false;

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
        save();
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(int sortOrder) {
        this.sortOrder = sortOrder;
    }

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

    public Bitmap getIconLocal(final Context context) {
        return new ImageSaver(context).
                setFileName(getSymbol()).
                setDirectoryName("icons").
                load();
    }

    public Bitmap getSmallIconLocal(final Context context) {
        return new ImageSaver(context).
                setFileName(getSymbol()).
                setDirectoryName("small_icons").
                load();
    }

    public void setSmallIconLocal(Context context, Bitmap response) {
        new ImageSaver(context).setFileName(getSymbol()).setDirectoryName("small_icons").save(response);
//        ImageRequest imageRequest = new ImageRequest(
//                this.getIconUrl(),
//                new Response.Listener<Bitmap>() {
//                    @Override
//                    public void onResponse(Bitmap response) {
//                        new ImageSaver(context).setFileName(getSymbol()).setDirectoryName("small_icons").save(response);
//                        imageListener.newImage(response, uniqueId);
//                    }
//                }, 64, 64,
//                ImageView.ScaleType.CENTER,
//                Bitmap.Config.RGB_565, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.e("Coin", "Could not get image");
//            }
//        });
//        VolleyHelper.getInstance(context).addToRequestQueue(imageRequest);
    }

    public void deleteSmallIconLocal(final Context context) {
        new ImageSaver(context).setFileName(getSymbol()).setDirectoryName("small_icons").deleteFile();
    }

    public Boolean getIsChecked() {
        return IsChecked;
    }

    public void setIsChecked(final Context context, final RefreshInterface refreshInterface) {
        IsChecked = true;
        RequestQueue queue = Volley.newRequestQueue(context);
        ImageRequest imageRequest = new ImageRequest(
                this.getIconUrl(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        new ImageSaver(context).setFileName(getSymbol()).setDirectoryName("icons").save(response);
                        refreshInterface.refresh();
                    }
                }, 0, 0,
                ImageView.ScaleType.CENTER,
                Bitmap.Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Coin", "Could not get image");
            }
        });
        queue.add(imageRequest);
        save();
    }

    public void removeIsChecked(final Context context) {
        IsChecked = false;
        new ImageSaver(context).setFileName(getSymbol()).setDirectoryName("icons").deleteFile();
        save();
    }

    public String getPriceUsd() {
        return priceUsd;
    }

    public void setPriceUsd(String priceUsd) {
        this.priceUsd = priceUsd;
    }

    public String getPriceEur() {
        return priceEur;
    }

    public void setPriceEur(String priceEur) {
        this.priceEur = priceEur;
    }

    public String getPriceBtc() {
        return priceBtc;
    }

    public void setPriceBtc(String priceBtc) {
        this.priceBtc = priceBtc;
    }

    public String getPercentChangeUsd24h() {
        return percentChangeUsd24h;
    }

    public void setPercentChangeUsd24h(String percentChangeUsd24h) {
        this.percentChangeUsd24h = percentChangeUsd24h;
    }

    public String getPercentChangeEur24h() {
        return percentChangeEur24h;
    }

    public void setPercentChangeEur24h(String percentChangeEur24h) {
        this.percentChangeEur24h = percentChangeEur24h;
    }

    public String getPercentChangeBtc24h() {
        return percentChangeBtc24h;
    }

    public void setPercentChangeBtc24h(String percentChangeBtc24h) {
        this.percentChangeBtc24h = percentChangeBtc24h;
    }

    @Override
    public String toString() {
        return "Coin{" +
                "Symbol='" + Symbol + '\'' +
                ", Name='" + Name + '\'' +
                ", NameSymbol='" + NameSymbol + '\'' +
                ", IconUrl='" + IconUrl + '\'' +
                ", IsChecked=" + IsChecked +
                ", priceUsd='" + priceUsd + '\'' +
                ", priceEur='" + priceEur + '\'' +
                ", priceBtc='" + priceBtc + '\'' +
                ", percentChangeUsd24h='" + percentChangeUsd24h + '\'' +
                ", percentChangeEur24h='" + percentChangeEur24h + '\'' +
                ", percentChangeBtc24h='" + percentChangeBtc24h + '\'' +
                ", sortOrder=" + sortOrder +
                ", locked=" + locked +
                '}';
    }

    @Override
    public int compareTo(@NonNull Coin coin) {
        Double localValue = null;
        Double coinValue = null;

        switch (new SettingsHelper().getCurrency()) {
            case "dollar":
                try {
                    localValue = Double.parseDouble(percentChangeUsd24h);
                    coinValue = Double.parseDouble(coin.getPercentChangeUsd24h());
                } catch (Exception ignored) {
                }
                break;
            case "euro":
                try {
                    localValue = Double.parseDouble(percentChangeEur24h);
                    coinValue = Double.parseDouble(coin.getPercentChangeEur24h());
                } catch (Exception ignored) {
                }
                break;
            case "btc":
                try{
                    localValue = Double.parseDouble(percentChangeBtc24h);
                    coinValue = Double.parseDouble(coin.getPercentChangeBtc24h());
                } catch (Exception ignored){
                }
        }

        if (coinValue != null && localValue != null) {
            if (Objects.equals(new SettingsHelper().getSortOrder(), "descending")) {
                return localValue > coinValue ? -1 : localValue < coinValue ? 1 : 0;
            } else if (Objects.equals(new SettingsHelper().getSortOrder(), "ascending")) {
                return localValue < coinValue ? -1 : localValue > coinValue ? 1 : 0;
            } else {
                return sortOrder < coin.getSortOrder() ? -1 : sortOrder > coin.getSortOrder() ? 1 : 0;
            }
        }
        return 0;
    }
}