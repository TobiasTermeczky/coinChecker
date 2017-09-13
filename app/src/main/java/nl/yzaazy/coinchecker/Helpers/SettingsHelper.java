package nl.yzaazy.coinchecker.Helpers;

import android.util.Log;

import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import nl.yzaazy.coinchecker.Objects.Settings;

public class SettingsHelper {
    SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

    public Date getJSONDate(){
        List<Settings> list = Settings.listAll(Settings.class);
        for (int i = 0; i <= list.size(); i++) {
            if(Objects.equals(list.get(i).getSetting(), "JSONDate")){
                try {
                    return dateformat.parse(list.get(i).getValue());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public void setJSONDate(Date date){
        List<Settings> list = Settings.listAll(Settings.class);
        Log.i("settings list: ", list.toString());
        boolean isInList = false;
        Settings setting = new Settings();
        if(!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getSetting(), "JSONDate")) {
                    setting = list.get(i);
                    setting.setValue(dateformat.format(date));
                    setting.save();
                    isInList = true;
                }
            }
        }
        if(!isInList){
            setting.setSetting("JSONDate");
            setting.setValue(dateformat.format(date));
            setting.save();
        }
    }

    public String getCurrencyValue(){
        List<Settings> list = Settings.listAll(Settings.class);
        for (int i = 0; i <= list.size(); i++) {
            if(Objects.equals(list.get(i).getSetting(), "Currency")){
                return list.get(i).getValue();
            }
        }
        return null;
    }

    public void checkSettings() {
        List<Settings> list = Settings.listAll(Settings.class);
        Log.i("settings list: ", list.toString());
        boolean isInList = false;
        if(!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getSetting(), "Currency")) {
                    isInList = true;
                }
            }
        }
        if(!isInList){
            Settings setting = new Settings();
            setting.setSetting("Currency");
            setting.setValue("dollar");
            setting.save();
        }
    }

    public void switchCurrency() {
        Settings currencySetting = new Settings();
        List<Settings> list = Settings.listAll(Settings.class);
        for (int i = 0; i < list.size(); i++) {
            if(Objects.equals(list.get(i).getSetting(), "Currency")){
                currencySetting = list.get(i);
            }
        }
        if(Objects.equals(currencySetting.getValue(), "dollar")) {
            currencySetting.setValue("euro");
        }else {
            currencySetting.setValue("dollar");
        }
        currencySetting.save();
    }
}
