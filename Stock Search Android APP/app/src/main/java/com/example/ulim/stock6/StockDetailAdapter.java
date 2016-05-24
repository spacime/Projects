package com.example.ulim.stock6;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ulim on 5/3/2016.
 */
public class StockDetailAdapter extends ArrayAdapter<StockDetailsEntry>{
    public StockDetailAdapter(Context context, List<StockDetailsEntry> items) {
        super(context, R.layout.custom_stockdetail_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_stockdetail_list_item, parent, false);

        StockDetailsEntry singleItem = getItem(position);
        TextView titleText = (TextView) customView.findViewById(R.id.titleText);
        TextView valueText = (TextView) customView.findViewById(R.id.valueText);
        int indicatorValue = singleItem.getIcon();

        titleText.setText(singleItem.getTitle());
        valueText.setText(singleItem.getValue());

        if (indicatorValue != 0){
            ImageView imageView = (ImageView) customView.findViewById(R.id.arrowImageView);
            if (indicatorValue > 0){
                imageView.setImageResource(R.drawable.up);
            } else if (indicatorValue < 0){
                imageView.setImageResource(R.drawable.down);
            }
        }

        return customView;
    }


}
