package com.example.ulim.stock6;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import android.view.ViewGroup;

import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.itemmanipulation.DynamicListView;
import com.nhaarman.listviewanimations.itemmanipulation.swipedismiss.OnDismissCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.ShareDialog;

public class MainActivity extends AppCompatActivity {
    AutoCompleteTextView autoCompleteTextView;

    private List<FavoriteEntry> myfavoriteEntries;
    private List<FavoriteEntry> tempfavoriteEntries;

    private List<StockDetailsEntry> entries = new ArrayList<StockDetailsEntry>();
    private String stockSymbol;
    private String stockPrice;
    private String stockName;
    private String companySymbol;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FacebookSdk.sdkInitialize(getApplicationContext());


        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        ArrayAdapter adapter = new ArrayAdapter(this, R.layout.autocomplete_item);
        autoCompleteTextView.setAdapter(adapter);

        final TextWatcher textWatcher = new TextWatcher(){
            public void afterTextChanged(Editable editable) {
                // TODO Auto-generated method stub

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newText = s.toString();

                if (autoCompleteTextView.isPerformingCompletion()) {
                    // An item has been selected from the list. Ignore.
                    /*Log.d("onItemClick", "onItemClick: Clicked");*/
                    return;
                }


                // Your code for a general case (suggest valid options to select)
                if (newText.length() >= 3){
                    /*Log.d("InputString", newText);*/
                    new autocompleteGetJson().execute(newText);
                }
            }
        };
        autoCompleteTextView.addTextChangedListener(textWatcher);

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                Stock selection = (Stock)parent.getItemAtPosition(position);
                autoCompleteTextView.removeTextChangedListener(textWatcher);
                autoCompleteTextView.setText(selection.symbol);
                autoCompleteTextView.addTextChangedListener(textWatcher);
            }
        });

        Button clearButton = (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                autoCompleteTextView.setText("");
            }
        });

        Button getQuoteButton = (Button) findViewById(R.id.get_quote_button);
        getQuoteButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d("Get Quote", "onClick: " + autoCompleteTextView.getText().toString());
                getQuote(autoCompleteTextView.getText().toString());
            }
        });


        populateFavoritesListView();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Inflate the menu; this add items to the action bar if is present */
        getMenuInflater().inflate(R.menu.menu_main_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getQuote(String symbol){
        new stockDataGetter().execute(symbol);
    }

    class stockDataGetter extends AsyncTask<String,String,String> {
        HttpURLConnection urlConnection;
        boolean validationError = false;
        String companySymbol;

        @Override
        protected String doInBackground(String... key) {
            companySymbol = key[0];
            StringBuilder sb = new StringBuilder();
            String json_string = null;

            try{
                /* ------------------ Loading string from server content ------------------------ */
                URL url = new URL("http://level-oxygen-127003.appspot.com/index.php?symbol="+companySymbol);
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
                    String errorStr = resultObject.get("Message").toString();
                    validationError = true;

                } catch (NullPointerException e){
                    validationError = false;
                }

            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable(){
                public void run(){
                    if (validationError){
                        android.app.AlertDialog.Builder whetherDelete = new android.app.AlertDialog.Builder(MainActivity.this);
                        whetherDelete.setTitle("Invalid or empty input. Please Check your input\n");
                        whetherDelete.show();
                    } else {
                        /*Toast.makeText(MainActivity.this, "No Error: Data fetched", Toast.LENGTH_LONG).show();*/
                        Intent intent = new Intent(MainActivity.this, ResultActivityNew.class);
                        intent.putExtra("symbol", companySymbol);
                        startActivity(intent);
                    }
                }
            });
            return null;
        }
    }

    class autocompleteGetJson extends AsyncTask<String,String,String> {

        HttpURLConnection urlConnection;
        public List<String> suggest;

        @Override
        protected String doInBackground(String... key) {
            String newText = key[0];
            StringBuilder sb = new StringBuilder();
            String string_of_json = null;
            final ArrayList<Stock> suggest= new ArrayList<MainActivity.Stock>();

            try{
                URL url = new URL("http://level-oxygen-127003.appspot.com/index.php?input="+newText);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                string_of_json = sb.toString();
                /*Log.d("Info", result);*/

                Log.d("Result", sb.toString());

                /* We receive a JSON array (not a JSON object), so we should create a JSONArray */
                JSONArray array = new JSONArray(string_of_json);
                /*System.out.println("arr: " + Arrays.toString(array));*/
                for (int i = 0; i < array.length(); i++) {
                    try{
                        JSONObject row = array.getJSONObject(i);

                        Stock SuggestKey;
                        suggest.add(new Stock(row.getString("Symbol"), row.getString("Name"), row.getString("Exchange")));
                    } catch (JSONException e) {
                        // Oops
                        e.printStackTrace();
                    }
                }

            }catch(Exception e){
                Log.w("Error", e.getMessage());
            }finally {
                urlConnection.disconnect();
            }

            runOnUiThread(new Runnable(){
                public void run(){
                    ArrayAdapter<Stock> aAdapter = new ArrayAdapter<MainActivity.Stock>(getApplicationContext(),
                            R.layout.autocomplete_item, suggest);
//                    aAdapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.item,suggest);
                    /*autoCompleteTextView.setThreshold(3); *//* wait for 3 characters to show suggestions or hints */
                    autoCompleteTextView.setAdapter(aAdapter);
                    aAdapter.notifyDataSetChanged();
                }
            });
            return null;
        }
    }


    public static class Stock {
        private String symbol;
        private String description;

        public Stock(String symbol, String name, String exchange) {
            this.symbol = symbol;
            this.description = name + " (" + exchange + ")";
        }

        @Override
        public String toString() {
            return symbol + "\n" + description;
        }
    }

    private void populateFavoritesListView() {
        /* Create a list of items */
//        final List<FavoriteEntry> favoritesEntries = new ArrayList<FavoriteEntry>();


        myfavoriteEntries = ((MyApplication) this.getApplication()).getFavoriteEntries();
        final ArrayAdapter<FavoriteEntry> adapter = new FavoritesEntryAdapter(this, myfavoriteEntries);
        DynamicListView mDynamicListView = (DynamicListView) findViewById(R.id.favoritesListView);

        mDynamicListView.enableSwipeToDismiss(
                new OnDismissCallback() {
                    @Override
                    public void onDismiss(@NonNull final ViewGroup listView, @NonNull final int[] reverseSortedPositions) {

                        android.app.AlertDialog.Builder whetherDelete = new android.app.AlertDialog.Builder(MainActivity.this);
                        whetherDelete.setTitle("Want to delete");
                        String name = "";
                        for (int position : reverseSortedPositions) {
                            name = adapter.getItem(position).getFavoriteName();
                        }
                        whetherDelete.setTitle("Want to delete " + name + " from favorites?");
                        whetherDelete.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                for (int position : reverseSortedPositions) {
                                    adapter.remove(adapter.getItem(position)) ;
                                }
                            }
                        });
                        whetherDelete.setNegativeButton("CANCLE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        whetherDelete.show();
                    }
                }
        );

        mDynamicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> a, View v, int position, long id) {
                FavoriteEntry f = adapter.getItem(position);
                String name = f.getFavoriteSymbol();
                getQuote(name);
            }
        });

        mDynamicListView.setAdapter(adapter);

    }



    public void refreshFavoriteListView(View view) {
        myfavoriteEntries = ((MyApplication) this.getApplication()).getFavoriteEntries();
        final ArrayAdapter<FavoriteEntry> adapter = new FavoritesEntryAdapter(this, myfavoriteEntries);

        for (int position = 0; position < adapter.getCount(); position++) {
            FavoriteEntry singleItem = adapter.getItem(position);
            new StockDetailsFragmentFiller().execute(singleItem.getFavoriteSymbol());
        }
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
            addToFavoriteEntries(entries);


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
        System.out.println("We have successfully refreshed" + myfavoriteEntries.size());
        tempfavoriteEntries.add(new FavoriteEntry(symbol, name, stockValue, change, marketCap));

        if(tempfavoriteEntries.size() == myfavoriteEntries.size()) {
            myfavoriteEntries = tempfavoriteEntries;
            tempfavoriteEntries = null;
            System.out.print("We have successfully refreshed");
            populateFavoritesListView();
        }
    }



    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        populateFavoritesListView();
    }

}
