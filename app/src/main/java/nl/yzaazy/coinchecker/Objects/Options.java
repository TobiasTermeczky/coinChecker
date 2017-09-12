package nl.yzaazy.coinchecker.Objects;

import com.orm.SugarRecord;

public class Options extends SugarRecord<Options> {

    private String option;
    private String value;

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
