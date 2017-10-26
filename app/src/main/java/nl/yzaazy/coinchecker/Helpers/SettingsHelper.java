package nl.yzaazy.coinchecker.Helpers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import nl.yzaazy.coinchecker.Objects.Setting;

public class SettingsHelper {
    private SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

    public void checkSettings() {
        try {
            Setting.find(Setting.class, "setting = ?", "Currency").get(0);
        } catch (IndexOutOfBoundsException e) {
            Setting setting = new Setting();
            setting.setSetting("Currency");
            setting.setValue("dollar");
            setting.save();
        }
        try {
            Setting.find(Setting.class, "setting = ?", "SortOrder").get(0);
        } catch (IndexOutOfBoundsException e) {
            Setting setting = new Setting();
            setting.setSetting("SortOrder");
            setting.setValue("index");
            setting.save();
        }
    }

    public Date getJSONDate() {
        try {
            return dateformat.parse(Setting.find(Setting.class, "setting = ?", "JSONDate").get(0).getValue());
        } catch (IndexOutOfBoundsException | ParseException e) {
            return null;
        }
    }

    public void setJSONDate(Date date) {
        Setting setting = new Setting();
        try {
            setting = Setting.find(Setting.class, "setting = ?", "JSONDate").get(0);
        } catch (IndexOutOfBoundsException e) {
            setting.setSetting("JSONDate");
        }
        setting.setValue(dateformat.format(date));
        setting.save();
    }

    public String getCurrency() {
        Setting setting = Setting.find(Setting.class, "setting = ?", "Currency").get(0);
        return setting.getValue();
    }

    public void switchCurrency() {
        Setting setting = Setting.find(Setting.class, "setting = ?", "Currency").get(0);
        switch (setting.getValue()){
            case "dollar":
                setting.setValue("euro");
                break;
            case "euro":
                setting.setValue("btc");
                break;
            case "btc":
                setting.setValue("dollar");
                break;

        }
        setting.save();
    }

    public String getSortOrder() {
        Setting setting = Setting.find(Setting.class, "setting = ?", "SortOrder").get(0);
        return setting.getValue();
    }

    public void switchOrder() {
        Setting setting = Setting.find(Setting.class, "setting = ?", "SortOrder").get(0);
        switch (setting.getValue()){
            case "index":
                setting.setValue("descending");
                break;
            case "descending":
                setting.setValue("ascending");
                break;
            default:
                setting.setValue("index");
                break;
        }
        setting.save();
    }
}
