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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    private Button confirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = (Button) findViewById(R.id.add_button);
        removeButton = (Button) findViewById(R.id.remove_button);
        tabhost = (TabHost) findViewById(R.id.tabHost);

        api = (EditText)findViewById(R.id.api_key);
        secret = (EditText)findViewById(R.id.api_secret);
        confirm = (Button)findViewById(R.id.save_api_data);

        list = (ListView) findViewById(R.id.listView);

        tabhost.setup();
        TabHost.TabSpec ts = tabhost.newTabSpec("main");
        ts.setContent(R.id.main);
        ts.setIndicator("Main");
        tabhost.addTab(ts);

        ts = tabhost.newTabSpec("settings");
        ts.setContent(R.id.settings);
        ts.setIndicator("Settings");
        tabhost.addTab(ts);

        addButton.setOnClickListener(
                new View.OnClickListener(){

                    @Override
                    public void onClick(View view) {
                        alert();
                    }
                }
        );

        removeButton.setOnClickListener(
                new View.OnClickListener(){

                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View view) {
                        if(indexToRemove > -1) {
                            row.remove(indexToRemove);
                            tokens.remove(indexToRemove);
                            JSONArray newArr = new JSONArray();

                                try {
                                    for(int i = 0; i < tokens.length(); i++)
                                        if(i != indexToRemove)
                                            newArr.put(tokens.get(i));
                                    tokens=newArr;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            ada.notifyDataSetChanged();
                            indexToRemove = -1;
                            saveData(obj);
                        }
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

        Log.d("Loading state: "," reading assets");
        this.row = new ArrayList<DataRow>();
        tokens = new JSONArray();
        try {
            obj = new JSONObject(readData());
            tokens = obj.getJSONArray("tokens");

            Log.d("dupaaaa", obj.toString());
            api.setText(obj.getString("key"));
            secret.setText(obj.getString("secret"));
            fillList(tokens);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        confirm.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                try {
                    obj.remove("key");
                    obj.remove("secret");
                    obj.put("key", api.getText());
                    obj.put("secret", secret.getText());
                    saveData(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        });
    }

    private void fillList(JSONArray array){
        for(int i = 0; i < array.length(); i++){
            try {
                JSONObject item = array.getJSONObject(i);
                row.add(new DataRow(item.getString("token"), item.getInt("buyPrice")));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        ada = new SingleRow(this, row);
        list.setAdapter(ada);
    }

    View.OnClickListener saveApiData = new View.OnClickListener(){

        @Override

        public void onClick(View v) {
            Log.d("dpaaaaaaaa","cefelellellelee");
            try {
                obj.remove("key");
                obj.put("key", api.getText());

                obj.remove("secret");
                obj.put("secret", secret.getText());

                saveData(obj);

                Log.d("dpaaaaaaaa",obj.get("key")+ " "+obj.get("secret"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void alert(){

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
        //builder.setView(priceInput);

        lay.addView(tokenInput);
        lay.addView(priceInput);
        builder.setView(lay);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addToken(tokenInput.getText().toString().toUpperCase(), Integer.parseInt(priceInput.getText().toString()));

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

    private void addToken(String token, int price){
        JSONObject newToken = writeJSON(token, price);
        tokens.put(newToken);
        row.add(new DataRow(token, price));
        saveData(obj);
        ada.notifyDataSetChanged();
    }

    public JSONObject writeJSON(String token, int buyPrice) {
        JSONObject object = new JSONObject();
        try {
            object.put("token", token);
            object.put("buyPrice", buyPrice);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public String readEmptyJSON() {
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
                emptyString = readEmptyJSON();
                empty = new JSONObject(emptyString);
                saveData(empty);
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

    public void saveData(JSONObject objToSave){
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

    @Override
    protected void onDestroy() {
        saveData(obj);
    }
}
