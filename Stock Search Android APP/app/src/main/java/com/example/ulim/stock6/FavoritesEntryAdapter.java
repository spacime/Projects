package com.example.ulim.stock6;

import android.database.ContentObservable;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.content.Context;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by ulim on 5/3/2016.
 */
public class FavoritesEntryAdapter extends ArrayAdapter<FavoriteEntry> {
    public FavoritesEntryAdapter(Context contex, List<FavoriteEntry> items) {
        super(contex, R.layout.custom_favorite_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_favorite_list_item, parent, false);

        FavoriteEntry singleItem = getItem(position);


        TextView favoriteSymbolTextView = (TextView) customView.findViewById(R.id.favoriteSymbolTextView);
        TextView favoriteNameTextView = (TextView) customView.findViewById(R.id.favoriteNameTextView);
        TextView favoriteStockValueTextView = (TextView) customView.findViewById(R.id.favoriteStockValueTextView);
        TextView favoriteChangeTextView = (TextView) customView.findViewById(R.id.favoriteChangeTextView);
        TextView favoriteMarketCapTextView = (TextView) customView.findViewById(R.id.favoriteMarketCapTextView);


        String favoriteChange = singleItem.getFavoriteChange();
        int left  = favoriteChange.indexOf('(');
        int right = favoriteChange.indexOf(')');
        int rightpercentlabel = favoriteChange.indexOf('%');

        favoriteChange = favoriteChange.substring(left + 1, right);
        favoriteSymbolTextView.setText(singleItem.getFavoriteSymbol());
        favoriteNameTextView.setText(singleItem.getFavoriteName());
        favoriteStockValueTextView.setText("$" + singleItem.getFavoriteStockValue());
        favoriteChangeTextView.setText(favoriteChange);
//        favoriteChangeTextView.setBackgroundColor(Color.GREEN);

        System.out.println("view change" + favoriteChange.substring(0, favoriteChange.length() - 1));

        if(Float.parseFloat(favoriteChange.substring(0, favoriteChange.length() - 1)) > 0) {
            favoriteChangeTextView.setBackgroundColor(Color.GREEN);
            favoriteChange = "+" + favoriteChange;
            favoriteChangeTextView.setText(favoriteChange);
        } else {
            favoriteChangeTextView.setBackgroundColor(Color.RED);
            favoriteChangeTextView.setText(favoriteChange);
        }

        favoriteMarketCapTextView.setText("Market Cap : " + singleItem.getFavoriteMarketCap());

        return customView;
    }


}
