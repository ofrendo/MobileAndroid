package org.dhbw.geo.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.dhbw.geo.database.DBRule;

import java.util.ArrayList;

/**
 * Created by Joern on 18.06.2015.
 */
public class DBRuleAdapter extends ArrayAdapter<DBRule> {
    ArrayList<DBRule> items;
    public DBRuleAdapter(Context context, int resource, ArrayList<DBRule> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(android.R.layout.simple_list_item_activated_1, null);
        }
        TextView tv = (TextView)v.findViewById(android.R.id.text1);
        tv.setText(items.get(position).getName());

        return v;
    }
}