package nl.yzaazy.coinchecker.Helpers;

import android.util.Log;

import java.util.List;
import java.util.Objects;

import nl.yzaazy.coinchecker.Objects.Settings;

public class SettingsHelper {

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
