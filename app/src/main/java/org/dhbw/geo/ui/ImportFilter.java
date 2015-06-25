package org.dhbw.geo.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBConditionFence;

import java.util.ArrayList;

/**
 * ArrayAdapter for importing DBConditionFences
 * Additionally contains a filterClass
 * @author Joern
 */
public class ImportFilter extends BaseAdapter implements Filterable {

    private ArrayList<DBConditionFence> originalData = null;
    private ArrayList<DBConditionFence> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public ImportFilter(Context context, ArrayList<DBConditionFence> data) {
        this.filteredData = data ;
        this.originalData = data ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public DBConditionFence getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {


        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.rule_condition_row, null);
        }
        TextView tv = (TextView)convertView.findViewById(R.id.ruleCondition_text);
        tv.setText(filteredData.get(position).getName());

        ImageView icon = (ImageView)convertView.findViewById(R.id.ruleCondition_icon);
        icon.setImageResource(R.drawable.map);

        return convertView;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            FilterResults results = new FilterResults();
            String filterString = constraint.toString().toLowerCase();
            if (filterString.equals("")){
                results.count =originalData.size();
                results.values = originalData;
                return results;
            }





            final ArrayList<DBConditionFence> list = originalData;

            int count = list.size();
            final ArrayList<DBConditionFence> nlist = new ArrayList<DBConditionFence>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getName();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<DBConditionFence>) results.values;
            notifyDataSetChanged();
        }

    }
}

