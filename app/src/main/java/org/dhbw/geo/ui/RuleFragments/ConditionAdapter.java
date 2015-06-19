package org.dhbw.geo.ui.RuleFragments;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBCondition;
import org.dhbw.geo.database.DBConditionFence;
import org.dhbw.geo.database.DBRule;

import java.util.ArrayList;

/**
 * Created by Joern on 19.06.2015.
 */
public class ConditionAdapter extends ArrayAdapter<DBCondition> {
    ArrayList<DBCondition> items;
    public ConditionAdapter(Context context, int resource, ArrayList<DBCondition> items) {
        super(context, resource, items);
        this.items = items;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.rule_condition_row, null);
        }

        TextView tv = (TextView)v.findViewById(R.id.ruleCondition_text);
        Log.e("Conditions","Condition: "+items.get(position).getName());
        tv.setText(items.get(position).getName());

        ImageView icon = (ImageView)v.findViewById(R.id.ruleCondition_icon);

        if (items.get(position) instanceof DBConditionFence){
            Log.i("Condition", "Fence");
        }
        else{
            Log.i("Condition", "Time");
        }

        return v;
    }

}
