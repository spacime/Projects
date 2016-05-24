package com.example.ulim.stock6;

import android.content.Context;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import java.util.Date;

/**
 * Created by ulim on 5/3/2016.
 */
public class NewsEntryAdapter extends ArrayAdapter<NewsEntry>{
    public NewsEntryAdapter(Context context, List<NewsEntry> items) {
        super(context, R.layout.custom_news_list_item, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View customView = inflater.inflate(R.layout.custom_news_list_item, parent, false);

        NewsEntry singleItem = getItem(position);
        TextView newsTitleText = (TextView) customView.findViewById(R.id.newsTitleText);
        TextView newsContentText = (TextView) customView.findViewById(R.id.newsContentText);
        TextView newsPublisherText = (TextView) customView.findViewById(R.id.newsPublisherText);
        TextView newsDateText = (TextView) customView.findViewById(R.id.newsDateText);

        newsTitleText.setText(Html.fromHtml("<a href=\" " + singleItem.getNewsUrl() + " \">" +
                singleItem.getNewsTitle() + "</a>"));
        newsTitleText.setAutoLinkMask(Linkify.WEB_URLS); /* linkify web urls inside TextView */
        newsTitleText.setLinkTextColor(Color.parseColor("#000000")); /*set link color to black*/

        newsContentText.setText(singleItem.getNewsContent());
        newsPublisherText.setText(singleItem.getNewsPublisher());
//        Timestamp stamp = new Timestamp();
//        android.text.format.DateFormat df =        new android.text.format.DateFormat();
////        df.format("yyyy-MM-dd hh:mm:ss", new java.util.Date());
//        newsDateText.setText(df.format("yyyy-MM-dd hh:mm:ss", new java.util.Date(singleItem.getNewsDate())));
        String dataString = singleItem.getNewsDate();
        dataString = dataString.substring(5, dataString.length());
        dataString = dataString.trim();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");

        try {
            Date date = format.parse(dataString);
            newsDateText.setText(dataString);
            DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
            String anotherdataString = df.format(date);
            newsDateText.setText("Date:" + anotherdataString);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
//        newsDateText.setText(dataString);

        return customView;
    }
}
