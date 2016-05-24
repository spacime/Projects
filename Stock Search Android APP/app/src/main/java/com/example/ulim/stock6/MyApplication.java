package com.example.ulim.stock6;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ulim on 5/4/2016.
 */
public class MyApplication extends Application {

    private List<FavoriteEntry> favoriteEntries = new ArrayList<>();


    public List<FavoriteEntry> getFavoriteEntries() {
        return favoriteEntries;
    }

    public void addFavoriteEntry(FavoriteEntry favoriteEntry) {
        favoriteEntries.add(favoriteEntry);
    }
}
