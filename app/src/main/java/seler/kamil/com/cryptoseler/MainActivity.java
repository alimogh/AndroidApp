package seler.kamil.com.cryptoseler;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import seler.kamil.com.cryptoseler.bittrex.BittrexRequests;
import seler.kamil.com.cryptoseler.model.DataRow;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private Button removeButton;
    private TabHost tabhost;
    private List<DataRow> row;
    private SingleRow ada;
    private ListView list;
    private int indexToRemove = -1;
    private JSONObject obj;
    private JSONArray tokens;
    private EditText api;
    private EditText secret;
    private Button saveApiData;
    private BittrexRequests bittrexAPI;
    private Button refreshPrices;
    private Button sellButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        setTabHost();
        setListeners();

        this.row = new ArrayList<DataRow>();
        tokens = new JSONArray();

        loadData();
        refreshPrices();
    }

    private void loadData() {
        try {
            obj = new JSONObject(readData());
            tokens = obj.getJSONArray("tokens");

            String key = obj.getString("key");
            String sec = obj.getString("secret");

            api.setText(key);
            secret.setText(sec);


            bittrexAPI = new BittrexRequests(key, sec);

            fillList(tokens);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void savedToast(){
        Toast.makeText(this, "API data saved", Toast.LENGTH_SHORT).show();
    }

    private void pricesToast(){
        Toast.makeText(this, "Prices updated", Toast.LENGTH_SHORT).show();
    }


    private void soldToast(String token, double amount, double price){
        Toast.makeText(this, "Sell order set: "+amount+" "+token+"s for "+price+" btc each." , Toast.LENGTH_SHORT).show();
    }


    private void notSold(String message){
        Toast.makeText(this, "Sell order not set: "+message, Toast.LENGTH_SHORT).show();
    }


    private void setListeners() {

        saveApiData.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                try {
                    obj.remove("key");
                    obj.remove("secret");
                    obj.put("key", api.getText());
                    obj.put("secret", secret.getText());
                    saveSettingsData(obj);
                    savedToast();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });

        addButton.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        inputTokenAlertWindow();
                    }
                }
        );

        removeButton.setOnClickListener(
                new View.OnClickListener(){

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        removeTokenAlertWindow(row.get(indexToRemove).getToken());
                    }
                }

        );//*/

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                indexToRemove = position;
            }
        });

        refreshPrices.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                refreshPrices();
            }
        });

        sellButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
            confirmSell(row.get(indexToRemove).getToken());
            }
        });

    }

    private void confirmSell(String token){
        new AlertDialog.Builder(this)
                .setTitle("Sell?")
                .setMessage("Do you want to remove "+token+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        sellToken();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }
    private void sellToken(){
        new Thread(new Runnable() {
            public void run() {

                try {
                    JSONObject o = (JSONObject) tokens.get(indexToRemove);
                    final DataRow r = row.get(indexToRemove);
                    final JSONObject sellResult = bittrexAPI.sellOrder(r.getToken(), r.getAmount(), r.getActualPrice());
                    final boolean result = sellResult.getBoolean("success");


                    list.post(new Runnable() {
                        @Override
                        public void run() {
                            if(result) {
                                removeToken();
                                soldToast(r.getToken(), r.getAmount(), r.getActualPrice());
                                ada.notifyDataSetChanged();
                            }else {
                                try {
                                    notSold(sellResult.getString("message"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }).start();
    }

    private void removeTokenAlertWindow(String token){
        new AlertDialog.Builder(this)
                .setTitle("Remove?")
                .setMessage("Do you want to remove "+token+"?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        removeToken();
                        ada.notifyDataSetChanged();
                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeToken(){
        row.remove(indexToRemove);
        //tokens.remove(indexToRemove);
        JSONArray newArr = new JSONArray();

        try {
            for(int i = 0; i < tokens.length(); i++)
                if(i != indexToRemove)
                    newArr.put(tokens.get(i));
            tokens=newArr;
            obj.remove("tokens");
            obj.put("tokens", newArr);
            saveSettingsData(obj);

            indexToRemove = -1;


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setTabHost() {
        tabhost.setup();
        TabHost.TabSpec ts = tabhost.newTabSpec("main");
        ts.setContent(R.id.main);
        ts.setIndicator("Main");
        tabhost.addTab(ts);

        ts = tabhost.newTabSpec("settings");
        ts.setContent(R.id.settings);
        ts.setIndicator("Settings");
        tabhost.addTab(ts);
    }

    private void initComponents(){
        addButton = (Button) findViewById(R.id.add_button);
        removeButton = (Button) findViewById(R.id.remove_button);
        tabhost = (TabHost) findViewById(R.id.tabHost);

        api = (EditText)findViewById(R.id.api_key);
        secret = (EditText)findViewById(R.id.api_secret);
        saveApiData = (Button)findViewById(R.id.save_api_data);
        refreshPrices = (Button)findViewById(R.id.refresh_prices_button);
        sellButton = (Button)findViewById(R.id.sell_button);


        list = (ListView) findViewById(R.id.listView);
    }

    private void fillList(JSONArray array){
        for(int i = 0; i < array.length(); i++){
            try {
                JSONObject item = array.getJSONObject(i);
                row.add(new DataRow(item.getString("token"), item.getInt("buyPrice"), item.getInt("amount")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        ada = new SingleRow(this, row);
        ada.setBittrexApi(bittrexAPI);
        list.setAdapter(ada);
    }


    public void inputTokenAlertWindow(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add token");

        LinearLayout lay= new LinearLayout(this);
        lay.setOrientation(LinearLayout.VERTICAL); //1 is for vertical orientation

        final EditText tokenInput = new EditText(this);
        tokenInput.setHint("Token symbol");
        tokenInput.setInputType(InputType.TYPE_CLASS_TEXT);
        //builder.setView(tokenInput);

        final EditText priceInput = new EditText(this);
        priceInput.setHint("Buy price (SAT)");
        priceInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        final EditText amountInput = new EditText(this);
        amountInput.setHint("amount");
        amountInput.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        //builder.setView(priceInput);

        lay.addView(tokenInput);
        lay.addView(priceInput);
        lay.addView(amountInput);
        builder.setView(lay);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(tokenInput.getText().toString().trim() != "" && priceInput.getText().toString().trim() != "" && amountInput.getText().toString().trim() != "")
                    addToken(tokenInput.getText().toString().toUpperCase(), Integer.parseInt(priceInput.getText().toString()), Double.parseDouble(amountInput.getText().toString()));

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


   /* private double getTokenPrice(String token){
        double sellPrice = bittrexAPI.getSellPrice(token);
        return sellPrice;
    }
*/

    private void refreshPrices(){

        new Thread(new Runnable() {
            public void run() {
                Log.d("ACTION", "getting prices");
                for(int i = 0; i < row.size(); i ++){
                    String token = row.get(i).getToken();
                    double price = bittrexAPI.getSellPrice(token);
                    int actualPriceSAT = (int)(price*100000000);
                    DataRow item = (DataRow) ada.getItem(i);
                    item.setActualPriceSAT(actualPriceSAT);
                    item.setActualPrice(price);

                    Log.d("Items******", ""+item);
                }

                list.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d("PRICES LIST", "updated");
                        ada.notifyDataSetChanged();
                        pricesToast();
                    }
                });
            }
        }).start();
    }

    private void addToken(String token, int price, double amount){
        JSONObject newToken = tokenJSON(token, price, amount);
        tokens.put(newToken);
        row.add(new DataRow(token, price, amount));
        saveSettingsData(obj);
        ada.notifyDataSetChanged();
    }

    public JSONObject tokenJSON(String token, int buyPrice, double amount) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("buyPrice", buyPrice);
            object.put("amount", amount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String initJSONFile() {
        String json = null;
        try {
            InputStream is = getAssets().open("seler_settings.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private String readData(){

        String temp="";
        try {
            FileInputStream fis = openFileInput("seler_settings.json");

            int c;

            while( (c = fis.read()) != -1){
                temp = temp + Character.toString((char)c);
            }

            fis.close();
        } catch (FileNotFoundException e) {
            JSONObject empty = null;
            String emptyString= null;
            try {
                emptyString = initJSONFile();
                empty = new JSONObject(emptyString);
                saveSettingsData(empty);
                Log.d("JSON init:", "clean settings init");
            } catch (JSONException e1) {
                return emptyString;
            }

            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return temp;

    }

    public void saveSettingsData(JSONObject objToSave){
        try {
            FileOutputStream fos = openFileOutput("seler_settings.json", Context.MODE_PRIVATE);
            fos.write(objToSave.toString().getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
