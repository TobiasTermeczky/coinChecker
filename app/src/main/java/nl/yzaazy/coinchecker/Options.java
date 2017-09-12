package nl.yzaazy.coinchecker;

import com.orm.SugarRecord;

public class Options extends SugarRecord<Options> {

    private String option;
    private String value;

    String getOption() {
        return option;
    }

    void setOption(String option) {
        this.option = option;
    }

    String getValue() {
        return value;
    }

    void setValue(String value) {
        this.value = value;
    }
}
