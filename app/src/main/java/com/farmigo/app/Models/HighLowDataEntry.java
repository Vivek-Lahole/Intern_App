package com.farmigo.app.Models;

public class HighLowDataEntry extends DataEntry{


    public HighLowDataEntry(String x, Number high, Number low) {
        setValue("x", x);
        setValue("y", high);
        //setValue("low", low);
    }

    public HighLowDataEntry(Number x, Number high, Number low) {
        setValue("x", x);
        setValue("y", high);
        //setValue("low", low);
    }
}
