package nl.yzaazy.coinchecker.Helpers;

import android.util.Log;

import java.util.List;
import java.util.Objects;

import nl.yzaazy.coinchecker.Objects.Options;

public class OptionHelper {

    public String getCurrencyValue(){
        List<Options> list = Options.listAll(Options.class);
        for (int i = 0; i <= list.size(); i++) {
            if(Objects.equals(list.get(i).getOption(), "Currency")){
                return list.get(i).getValue();
            }
        }
        return null;
    }

    public Options getCurrencyOption(){
        List<Options> list = Options.listAll(Options.class);
        for (int i = 0; i <= list.size(); i++) {
            if(Objects.equals(list.get(i).getOption(), "Currency")){
                return list.get(i);
            }
        }
        return null;
    }

    public void checkOptions() {
        List<Options> list = Options.listAll(Options.class);
        Log.i("options list: ", list.toString());
        boolean isInList = false;
        if(!list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.get(i).getOption(), "Currency")) {
                    isInList = true;
                }
            }
        }
        if(!isInList){
            Options option = new Options();
            option.setOption("Currency");
            option.setValue("dollar");
            option.save();
        }
    }

    public void switchCurrency() {
        Options currencyOption = getCurrencyOption();
        if(Objects.equals(currencyOption.getValue(), "dollar")) {
            currencyOption.setValue("euro");
        }else {
            currencyOption.setValue("dollar");
        }
        currencyOption.save();
    }
}
