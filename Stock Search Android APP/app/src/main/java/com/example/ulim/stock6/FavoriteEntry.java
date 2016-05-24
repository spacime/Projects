package com.example.ulim.stock6;

/**
 * Created by Pablo on 4/30/2016.
 */
public class FavoriteEntry {

    private String symbol;
    private String name;
    private String stockValue;
    private String change;
    private String marketCap;

    public FavoriteEntry(final String symbol, final String name,
                         final String stockValue, final String change, final String marketCap) {
        this.symbol = symbol;
        this.name = name;
        this.stockValue = stockValue;
        this.change = change;
        this.marketCap = marketCap;
    }
    /**
     * @return symbol of favorites entry
     */
    public String getFavoriteSymbol() { return symbol; }

    /**
     * @return name of favorites entry
     */
    public String getFavoriteName() { return name; }

    /**
     * @return stockValue of favorites entry
     */
    public String getFavoriteStockValue() { return stockValue; }

    /**
     * @return change of favorites entry
     */
    public String getFavoriteChange() { return change; }

    public void setFavoriteEntry(final String symbol, final String name,final String stockValue,
                                 final String changePercent, final String marketCap){
        this.symbol = symbol;
        this.name = name;
        this.stockValue = stockValue;
        this.change = changePercent;
        this.marketCap = marketCap;
    }

    /**
     * @return change of favorites entry
     */
    public String getFavoriteMarketCap() { return marketCap; }

    @Override public String toString() {
        StringBuilder result = new StringBuilder();
//        String NEW_LINE = System.getProperty("line.separator");
//
//        result.append(this.getClass().getName() + " Object {" + NEW_LINE);
//        result.append(" Name: " + fName + NEW_LINE);
//        result.append(" Number of doors: " + fNumDoors + NEW_LINE);
//        result.append(" Year manufactured: " + fYearManufactured + NEW_LINE );
//        result.append(" Color: " + fColor + NEW_LINE);
//        //Note that Collections and Maps also override toString
//        result.append(" Options: " + fOptions + NEW_LINE);
//        result.append("}");
        result.append(stockValue);


        return result.toString();
    }
}
