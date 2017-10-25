package nl.yzaazy.coinchecker.Objects;

import com.orm.SugarRecord;

public class Setting extends SugarRecord<Setting> {

    private String setting;
    private String value;

    public String getSetting() {
        return setting;
    }

    public void setSetting(String setting) {
        this.setting = setting;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
