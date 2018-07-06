package seler.kamil.com.cryptoseler;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import seler.kamil.com.cryptoseler.bittrex.BittrexRequests;
import seler.kamil.com.cryptoseler.model.DataRow;

/**
 * Created by Kamil on 2018-07-04.
 */

public class SingleRow extends BaseAdapter {

    private Context context;
    private List<DataRow> items;
    private BittrexRequests bittrexApi;

    public SingleRow(Context context, List<DataRow> items) {
        this.context = context;
        this.items = items;
    }

    private class DataHolder {
        TextView token;
        TextView buyPrice;
        TextView actualPrice;
    }

    DataHolder holder = null;
    DataRow rowItem = null;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.single_row, null);
            holder = new DataHolder();
            holder.token = (TextView) convertView.findViewById(R.id.tokenField);
            holder.buyPrice = (TextView) convertView.findViewById(R.id.buyPriceField);
            holder.actualPrice = (TextView) convertView.findViewById(R.id.actualPriceField);
            //holder.sellButton = (Button) convertView.findViewById(R.id.sellButton);
            convertView.setTag(holder);
        } else {
            holder = (DataHolder) convertView.getTag();
        }

        rowItem = (DataRow) getItem(position);

        //setPrice(position);

        if (rowItem.getBuyPrice() >= rowItem.getActualPriceSAT())
            holder.actualPrice.setTextColor(Color.RED);
        else
            holder.actualPrice.setTextColor(Color.GREEN);

        holder.token.setText(rowItem.getToken());
        holder.buyPrice.setText(rowItem.getBuyPrice() + "");
        holder.actualPrice.setText(rowItem.getActualPriceSAT() + "");

        return convertView;
    }


    public void setBittrexApi(BittrexRequests bittrexApi) {
        this.bittrexApi = bittrexApi;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return items.indexOf(getItem(position));
    }

    public DataRow getDataRow(int position) {
        return items.get(position);
    }

}

