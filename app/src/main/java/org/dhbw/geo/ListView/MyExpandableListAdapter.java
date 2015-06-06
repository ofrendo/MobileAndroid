package org.dhbw.geo.ListView;

/**
 * Created by Joern on 05.06.2015.
 */
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.dhbw.geo.R;

import java.util.ArrayList;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private final ArrayList<Group> groups;
    public LayoutInflater inflater;
    public Activity activity;

    public MyExpandableListAdapter(Activity act, ArrayList<Group> groups) {
        activity = act;
        this.groups = groups;
        inflater = act.getLayoutInflater();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return groups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }
    final String tag = "getChild";
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Child childObject = (Child) getChild(groupPosition,childPosition);
        //final String children = (String) getChild(groupPosition, childPosition);

        final String groupName = ((Group)getGroup(groupPosition)).string;

        Log.i(tag, "group"+groupPosition);
        Log.i(tag, "child" + childPosition);



        final String children = childObject.name;
        TextView text = null;
        if (convertView == convertView) {
        Log.i(tag, groupPosition + " "+childPosition+ "created");

            switch (childObject.type){
                case Child.SWITCH:
                    convertView = inflater.inflate(R.layout.rule_switch, null);
                    Switch switchObject = (Switch)convertView.findViewById(R.id.rule_switch);
                    switchObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.i(tag,groupName);
                            Log.i(tag,childObject.name + "changed to "+ isChecked);
                            childObject.checked =  isChecked;
                        }
                    });
                    text = (TextView) convertView.findViewById(R.id.rule_text);
                    text.setText(children);
                    break;
                case Child.CHECKBOX:
                    convertView = inflater.inflate(R.layout.rule_checkbox, null);
                    final CheckBox checkObject = (CheckBox)convertView.findViewById(R.id.rule_checkbox);
                    checkObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            Log.i(tag,groupName);
                            Log.i(tag,childObject.name + "changed to "+ isChecked);
                            childObject.checked =  isChecked;
                        }
                    });
                    text = (TextView) convertView.findViewById(R.id.rule_text);
                    text.setText(children);
                    break;
                case Child.RADIOBUTTONS:
                    convertView = inflater.inflate(R.layout.rule_radio, null);
                    RadioGroup radioGroup = (RadioGroup) convertView.findViewById(R.id.rule_radiogroup);
                    for (int i = 0; i<childObject.options.size(); i++){
                        RadioButton radioButton = new RadioButton(activity);
                        radioGroup.addView(radioButton);
                        radioButton.setText(childObject.options.get(i));
                    }
                    radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            Log.i(tag,groupName);
                            Log.i(tag,childObject.name + "selected  to "+ childObject.options.get(checkedId));
                            childObject.selectedOption = childObject.options.get(checkedId);
                        }
                    });
                    text = (TextView) convertView.findViewById(R.id.rule_text);
                    text.setText(children);
                    break;
                default:
                    //convertView = inflater.inflate(R.layout.listrow_details, null);
                    //text = (TextView) convertView.findViewById(R.id.rule_text);
                    //text.setText(children);
                    break;
            }



            //LinearLayout rowContainer = (LinearLayout) convertView.findViewById(R.id.RowContainer);

            /*switch (childObject.type){
                case Child.CHECKBOX:

                    CheckBox checkbox = new CheckBox(activity);
                    checkbox.setChecked(childObject.checked);
                    rowContainer.addView(checkbox);
                    checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            childObject.checked = isChecked;
                        }
                    });

                    break;
                default: break;
            }*/
        }
        Log.i(tag,"type "+childObject.type);
        Log.i(tag,"id" + convertView.getId());
        Log.i(tag, "################");



        /*convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, children,
                        Toast.LENGTH_SHORT).show();
            }
        });*/
        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return groups.get(groupPosition).children.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        Log.i(tag,"group collapsed" + groupPosition);
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        Log.i(tag,"group expanded" + groupPosition);
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, final ViewGroup parent) {
        //get Group element
        final Group group = (Group) getGroup(groupPosition);

        if (convertView == null) {
            //inflate/create View
            convertView = inflater.inflate(R.layout.listrow_group, null);
            RelativeLayout header = (RelativeLayout)convertView.findViewById(R.id.HeaderRow);
            Switch switchObject = (Switch)convertView.findViewById(R.id.switch1);

            switchObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    group.setActive(isChecked);
                }
            });

        }
       /* Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView; */


        CheckedTextView text = (CheckedTextView)convertView.findViewById(R.id.rule_text);

        text.setText(group.string);
        text.setChecked(isExpanded);
        //because the header contains an additional clickable object we need to reasign an onclicklistener vor expanding/collapsing
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpandableListView list = (ExpandableListView) activity.findViewById(R.id.expandableListView);
                list.destroyDrawingCache();
                if (list.isGroupExpanded(groupPosition)) {
                    list.collapseGroup(groupPosition);
                } else {
                    list.expandGroup(groupPosition,true);
                }
            }
        });
        return convertView;

    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}