package com.example.ulim.stock6;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ResultActivityNew extends AppCompatActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
            */
    private ViewPager mViewPager;

    private boolean isFavorite = false;

    private String stockSymbol;
    private String stockPrice;
    private String stockName;

    private List<NewsEntry> newsListEntries = new ArrayList<NewsEntry>();
    private ArrayAdapter<NewsEntry> adapter2;

    private List<StockDetailsEntry> entries = new ArrayList<StockDetailsEntry>();
    private ArrayAdapter<StockDetailsEntry> adapter;

    private String companySymbol;

    private List<FavoriteEntry> favoriteEntries;

    private ShareDialog shareDialog;
    private CallbackManager callbackManager;

    private Menu mOptionsMenu;

    private int isTheFirst;

    private PhotoViewAttacher mAttacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isTheFirst = 0;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_activity_new);

        /* Facebook init */
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);
        // this part is optional -- register callback to manage result after Share dialog
        // user interaction is done
        shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {

            @Override
            public void onSuccess(Sharer.Result result) {
                if (result.getPostId() != null){
                                    /* Content has been shared and posted */
                    Toast.makeText(ResultActivityNew.this, "You shared this post", Toast.LENGTH_LONG).show();
                } else {
                                                       /* Not posted e.g. User hit cancel Button */
                    Toast.makeText(ResultActivityNew.this, "Post not shared", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancel() {
                                                /* User turned back from FB share offer page */
                Toast.makeText(ResultActivityNew.this, "The post has not been shared", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(ResultActivityNew.this, "Error while trying to share post", Toast.LENGTH_LONG).show();
            }
        });

        setTitle("");

        favoriteEntries = ((MyApplication) this.getApplication()).getFavoriteEntries();
        System.out.println("FavoriteEntries" + favoriteEntries);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        stockSymbol = intent.getStringExtra("symbol");
//        TextView tv = (TextView) findViewById(R.id.textView3);
//        tv.setText(stockSymbol);
        Log.d("Received symbol", "onCreate: " + stockSymbol);

        new NewsFeedListViewFiller().execute(stockSymbol);
        new StockDetailsFragmentFiller().execute(stockSymbol);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);



        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) populateStockDetailsListView(stockSymbol);
//                if (tab.getPosition() == 1) loadHistoricalChartWebView(stockSymbol);
                if (tab.getPosition() == 2) populateNewsListView(stockSymbol);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) populateStockDetailsListView(stockSymbol);
//                if (tab.getPosition() == 1) loadHistoricalChartWebView(stockSymbol);
                if (tab.getPosition() == 2) populateNewsListView(stockSymbol);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                if (tab.getPosition() == 0) populateStockDetailsListView(stockSymbol);
//                if (tab.getPosition() == 1) loadHistoricalChartWebView(stockSymbol);
                if (tab.getPosition() == 2) populateNewsListView(stockSymbol);
            }
        });

//        loadHistoricalChartWebView(stockSymbol);

    }// end of onCreate

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        /* show or hide Add and Remove Favorite buttons according to whether the element if favorite */

        mOptionsMenu = menu;
        menu.findItem(R.id.action_remove_favorite).setVisible(isFavorite);
        menu.findItem(R.id.action_add_favorite).setVisible(!isFavorite);


        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_activity_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml

        switch (item.getItemId()) {
            case R.id.action_add_favorite:
                isFavorite = true;
                /*updateFavorite();*/
                addToFavoriteEntries(entries);
                supportInvalidateOptionsMenu();
                return true;
            case R.id.action_remove_favorite:
                isFavorite = false;
                /*updateFavorite();*/
//                favoriteEntries = ((MyApplication) this.getApplication()).getFavoriteEntries();
//                favoriteEntries.remove()
                removeFromFavoriteEntries(entries);
                supportInvalidateOptionsMenu();
                return true;

            case R.id.share_facebook:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                String yahoo_url = "http://finance.yahoo.com/q?s=" + stockSymbol;

                if (ShareDialog.canShow(ShareLinkContent.class)) {
                    ShareLinkContent linkContent = new ShareLinkContent.Builder()
                            .setContentUrl(Uri.parse(yahoo_url))
                            .setContentTitle("Current Stock Price of " +stockName+" $"+stockPrice+"")
                            .setImageUrl(Uri.parse("http://chart.finance.yahoo.com/t?s="+stockSymbol+"&lang=en-US&width=1200&height=1200"))
                            .setContentDescription(
                                    "Stock Information of "+stockSymbol+".")
                            .build();

                    shareDialog.show(linkContent /*, ShareDialog.Mode*/); /* Show Facebook Share Dialog */
                }
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void removeFromFavoriteEntries(List<StockDetailsEntry> stockDetailsEntries) {
        favoriteEntries = ((MyApplication) this.getApplication()).getFavoriteEntries();
        String symbol = "";
        String name = "";
        String stockValue = "";
        String change = "";
        String marketCap = "";
        for(StockDetailsEntry entry : stockDetailsEntries) {
            switch (entry.getTitle()) {
                case "SYMBOL":
                    symbol = entry.getValue();
                    break;
                case "NAME":
                    name = entry.getValue();
                    break;
                case "LASTPRICE":
                    stockValue = entry.getValue();
                    break;
                case "CHANGE":
                    change = entry.getValue();
                    break;
                case "MARKETCAP":
                    marketCap = entry.getValue();
                    break;
                default:
                    break;
            }
        }

        for(FavoriteEntry entry : favoriteEntries) {
            if(entry.getFavoriteName().equals(name)) {
                favoriteEntries.remove(entry);
            }
        }
    }

    private void addToFavoriteEntries(List<StockDetailsEntry> stockDetailsEntries) {
        String symbol = "";
        String name = "";
        String stockValue = "";
        String change = "";
        String marketCap = "";
        for(StockDetailsEntry entry : stockDetailsEntries) {
            switch (entry.getTitle()) {
                case "SYMBOL":
                    symbol = entry.getValue();
                    break;
                case "NAME":
                    name = entry.getValue();
                    break;
                case "LASTPRICE":
                    stockValue = entry.getValue();
                    break;
                case "CHANGE":
                    change = entry.getValue();
                    break;
                case "MARKETCAP":
                    marketCap = entry.getValue();
                    break;
                default:
                    break;
            }
        }


        favoriteEntries.add(new FavoriteEntry(symbol, name, stockValue, change, marketCap));

        System.out.println("New favorites" + favoriteEntries.get(0));
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
//            return PlaceholderFragment.newInstance(position + 1);
            switch (position) {
                case 0:
                    return new StockDetailsFragment();
                case 1:
                    return new HistoricalChartsFragment();
                case 2:
                    return new NewsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "CURRENT";
                case 1:
                    return "HISTORICAL";
                case 2:
                    return "NEWS";
            }
            return null;
        }

    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap imageBitmap = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                imageBitmap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return imageBitmap;
        }

        protected void onPostExecute(Bitmap result) {

            bmImage.setImageBitmap(result);
//            mAttacher.update();
            bmImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadPhoto(bmImage);
                }
            });
        }
    }

    private void loadPhoto(ImageView imageView) {
        ImageView tempImageView = imageView;
        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.custom,
                (ViewGroup) findViewById(R.id.layout_root));
        ImageView image = (ImageView) layout.findViewById(R.id.image1);
        image.setImageDrawable(tempImageView.getDrawable());
        mAttacher = new PhotoViewAttacher(image);
        imageDialog.setView(layout);
        imageDialog.create();
        imageDialog.show();
        imageDialog.setCancelable(true);
    }

    private void populateStockDetailsListView(String stockSymbol) {
        NonScrollListView list = (NonScrollListView) findViewById(R.id.stockDetailsListView);
        list.setAdapter(adapter);

                    /* show Image in a ImageView */
        ImageView chartImageView = (ImageView) findViewById(R.id.todayStockChartImageView);
        new DownloadImageTask(chartImageView)
                .execute("http://chart.finance.yahoo.com/t?s=" + companySymbol + "&lang=en-US&width=1200&height=1200");

        System.out.println("Derails, finished");


        System.out.println("check" + isFavorite);
        if(isTheFirst == 0) {
            loadHistoricalChartWebView(stockSymbol);
            setTitle(stockName);
            isFavorite = checkWhetherFavorite(entries);
            mOptionsMenu.findItem(R.id.action_remove_favorite).setVisible(isFavorite);
            mOptionsMenu.findItem(R.id.action_add_favorite).setVisible(!isFavorite);

            isTheFirst++;
        }


    }

    private boolean checkWhetherFavorite(List<StockDetailsEntry> stockDetailsEntries) {
        favoriteEntries = ((MyApplication) this.getApplication()).getFavoriteEntries();
        String symbol = "";
        String name = "";
        String stockValue = "";
        String change = "";
        String marketCap = "";
        for(StockDetailsEntry entry : stockDetailsEntries) {
            switch (entry.getTitle()) {
                case "SYMBOL":
                    symbol = entry.getValue();
                    break;
                case "NAME":
                    name = entry.getValue();
                    break;
                case "LASTPRICE":
                    stockValue = entry.getValue();
                    break;
                case "CHANGE":
                    change = entry.getValue();
                    break;
                case "MARKETCAP":
                    marketCap = entry.getValue();
                    break;
                default:
                    break;
            }
        }

        for(FavoriteEntry entry : favoriteEntries) {
            if(entry.getFavoriteName().equals(name)) {
                return true;
            }
        }

        return false;
    }


    private void populateNewsListView(String stockSymbol) {
        /* Create a list of items */
        ListView list = (ListView) findViewById(R.id.newsListView);
        list.setAdapter(adapter2);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter2, View view, int position, long id) {
                NewsEntry singleItem = (NewsEntry) adapter2.getItemAtPosition(position);
                String url = singleItem.getNewsUrl();
                            /* open URL */
                Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void loadHistoricalChartWebView(String stockSymbol) {
        WebView browser = (WebView) findViewById(R.id.webView);
        /* set loading of images */
        browser.getSettings().setLoadsImagesAutomatically(true);
        /* enable JS */
        browser.getSettings().setJavaScriptEnabled(true);
        String url = null;
        try {
            url = "file:///android_asset/historicalChart.html?symbol=" +
                    URLEncoder.encode(stockSymbol, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        browser.loadUrl(url);
    }

    class StockDetailsFragmentFiller extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;


        @Override
        protected String doInBackground(String... key) {
            companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
            int val;

            try {
                /* ------------------ Loading string from server content ------------------------ */
                URL url = new URL("http://level-oxygen-127003.appspot.com/index.php?symbol=" + companySymbol);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                json_string = sb.toString();

                /*Log.d("Info", result);*/
                /* ------------- Finished. String fully loaded from server response ------------- */
                Log.d("Result", sb.toString());

                /* We receive a JSON object (not a JSON array), so we should create a JSONObject */
                JSONObject resultObject = new JSONObject(json_string);
                /*System.out.println("arr: " + Arrays.toString(array));*/

                try {
                    /* Create a list of items */
                    stockPrice= resultObject.get("Last Price").toString();
                    entries.add(new StockDetailsEntry("NAME", resultObject.get("Name").toString(), 0));
                    stockName = resultObject.get("Name").toString();
                    entries.add(new StockDetailsEntry("SYMBOL", resultObject.get("Symbol").toString(), 0));
                    entries.add(new StockDetailsEntry("LASTPRICE", resultObject.get("Last Price").toString(), 0));
                    String change = resultObject.get("Change (Change Percent)").toString();
                    int left = change.indexOf('(');
                    change = change.substring(0, left);
                    change = change.trim();
                    System.out.println("The change is: " + change);
                    int indicate = 0;
                    if(Float.parseFloat(change) > 0) {
                        indicate = 1;
                    } else {
                        indicate = -1;
                    }
                    entries.add(new StockDetailsEntry("CHANGE",
                            resultObject.get("Change (Change Percent)").toString(), indicate));
                    entries.add(new StockDetailsEntry("TIMESTAMP", resultObject.get("Time and Date").toString(), 0));
                    entries.add(new StockDetailsEntry("MARKETCAP", resultObject.get("Market Cap").toString(), 0));
                    entries.add(new StockDetailsEntry("VOLUME", resultObject.get("Volume").toString(), 0));

                    change = resultObject.get("Change YTD (Change Percent YTD)").toString();
                    left = change.indexOf('(');
                    change = change.substring(0, left - 1);
                    change = change.trim();
                    System.out.println("The change is: " + change);
                    indicate = 0;
                    if(Float.parseFloat(change) > 0) {
                        indicate = 1;
                    } else {
                        indicate = -1;
                    }

                    entries.add(new StockDetailsEntry("CHANGEYTD",
                            resultObject.get("Change YTD (Change Percent YTD)").toString(),
                            indicate));
                    entries.add(new StockDetailsEntry("HIGH", resultObject.get("High").toString(), 0));
                    entries.add(new StockDetailsEntry("LOW", resultObject.get("Low").toString(), 0));
                    entries.add(new StockDetailsEntry("OPEN", resultObject.get("Open").toString(), 0));

                } catch (JSONException e) {
                    // Oops
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            } finally {
                urlConnection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
//            adapter = new StockDetailAdapter(ResultActivityNew.this, entries);
            /* populate Stock Details ListView */
                    /* Build Adapter */
            adapter = new StockDetailAdapter(ResultActivityNew.this, entries);
            populateStockDetailsListView(stockSymbol);

        }
    }

    class NewsFeedListViewFiller extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;

        @Override
        protected String doInBackground(String... key) {
            String companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;
//            final List<NewsEntry> newsListEntries = new ArrayList<NewsEntry>();

            try {
                /* ------------------ Loading string from server content ------------------------ */
                URL url = new URL("http://spacime.us-west-2.elasticbeanstalk.com/search.php?bing_input=" + companySymbol);


                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                json_string = sb.toString();
                /*Log.d("Info", result);*/
                /* ------------- Finished. String fully loaded from server response ------------- */
                Log.d("Result", sb.toString());

                /* We receive a JSON array (not a JSON object), so we should create a JSONArray */
                JSONArray array = new JSONArray(json_string);
                System.out.println("arr: " + array.toString());

                try {
                    /* Create a list of items */
                    int len = Math.min(4, array.length());
                    for (int i = 0; i < len; i++) {
                        try {
                            /* Important note: The elements of the array are objects */
                            JSONObject row = array.getJSONObject(i);
                            /*Log.d("News Row::::::::", row.toString());*/
                            /*Log.d("Title", row.getString("Title"));
                            Log.d("Description", row.getString("Description"));
                            Log.d("Source", row.getString("Source"));
                            Log.d("Date", row.getString("Date"));*/
                            newsListEntries.add(new NewsEntry(
                                    row.getString("Title"),
                                    row.getString("Description"),
                                    "Publisher: " + row.getString("Source"),
                                    "Date: " + row.getString("Date"),
                                    row.getString("Url")));

                        } catch (JSONException e) {
                            // Oops
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    // Oops
                    e.printStackTrace();
                }

            } catch (Exception e) {
                Log.w("Error", e.getMessage());
            } finally {
                urlConnection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            adapter2 = new NewsEntryAdapter(ResultActivityNew.this, newsListEntries);
                    /* Configure the list view */
//            ListView list = (ListView) findViewById(R.id.newsListView);
//            list.setAdapter(adapter2);
//
//            list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
//                @Override
//                public void onItemClick(AdapterView<?> adapter2, View view, int position, long id) {
//                    NewsEntry singleItem = (NewsEntry) adapter2.getItemAtPosition(position);
//                    String url = singleItem.getNewsUrl();
//                            /* open URL */
//                    Uri uri = Uri.parse(url); // missing 'http://' will cause crashed
//                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                    startActivity(intent);
//                }
//            });
        }
    }
}
