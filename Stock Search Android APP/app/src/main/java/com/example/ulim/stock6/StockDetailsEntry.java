package com.example.ulim.stock6;

/**
 * Created by ulim on 5/3/2016.
 */
public class StockDetailsEntry {
    private final String title;
    private final String value;
    private final int icon;

    public StockDetailsEntry(final String title, final String value,
                             final int icon) {
        this.title = title;
        this.value = value;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public int getIcon() {
        return icon;
    }
}
