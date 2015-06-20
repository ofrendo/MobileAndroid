package org.dhbw.geo.ui.ListView;

/**
 * Created by Joern on 05.06.2015.
 */
import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.dhbw.geo.R;
import org.dhbw.geo.database.DBActionSound;

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
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final Child childObject = (Child) getChild(groupPosition,childPosition);
        //final String children = (String) getChild(groupPosition, childPosition);


        final String groupName = ((Group)getGroup(groupPosition)).name;


        final String childName = childObject.name;
        TextView text = null;

        Log.i(tag, groupPosition + " "+childPosition+ "created");

        switch (childObject.type){
            case Child.SWITCH:
                convertView = inflater.inflate(R.layout.rule_switch, null);
                Switch switchObject = (Switch)convertView.findViewById(R.id.rule_switch);
                switchObject.setChecked(childObject.checked);
                switchObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        childObject.setChecked(isChecked);
                    }
                });
                text = (TextView) convertView.findViewById(R.id.switch_text);
                text.setText(childName);
                break;
            case Child.CHECKBOX:
                convertView = inflater.inflate(R.layout.rule_checkbox, null);
                final CheckBox checkObject = (CheckBox)convertView.findViewById(R.id.rule_checkbox);
                checkObject.setChecked(childObject.checked);
                checkObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                        childObject.setChecked(isChecked);
                    }
                });
                text = (TextView) convertView.findViewById(R.id.checkbox_text);
                text.setText(childName);
                break;
            case Child.RADIOBUTTONS:
                convertView = inflater.inflate(R.layout.rule_radio, null);
                RadioGroup radioGroup = (RadioGroup) convertView.findViewById(R.id.rule_radiogroup);
                //add new buttons
                for (int i = 0; i<childObject.options.size(); i++){
                    RadioButton radioButton = new RadioButton(activity);
                    radioGroup.addView(radioButton);
                    radioButton.setText(childObject.options.get(i));

                    if (childObject.selectedOption == childObject.options.get(i)){
                        radioButton.setChecked(true);
                    }
                }

                radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        int checkedIndex = (checkedId - 1) % childObject.options.size();
                        Log.i(tag, childObject.name + "selected  to " + childObject.options.get(checkedIndex));
                        childObject.selectedOption = childObject.options.get(checkedIndex);
                    }
                });
                text = (TextView) convertView.findViewById(R.id.radio_text);
                text.setText(childName);
                break;
            case Child.SLIDER:
                convertView = inflater.inflate(R.layout.rule_slider, null);
                SeekBar slider = (SeekBar) convertView.findViewById(R.id.rule_slider);
                slider.setMax(childObject.range);
                slider.setProgress(childObject.value - childObject.min);

                final TextView actual = (TextView) convertView.findViewById(R.id.slider_count);
                actual.setText(""+childObject.value);
                slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        actual.setText("" + (progress + childObject.min));
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        childObject.value = seekBar.getProgress() + childObject.min;
                        actual.setText("" + (seekBar.getProgress() + childObject.min));

                    }
                });

                text = (TextView) convertView.findViewById(R.id.slider_text);
                text.setText(childName);

                break;
            case Child.TEXTINPUT:

                convertView = inflater.inflate(R.layout.rule_text_input,null);


                final EditText input = (EditText) convertView.findViewById(R.id.rule_textInput);
                input.setText(childObject.text);



                input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        childObject.setText( "" + input.getText());
                    }
                });



                text = (TextView) convertView.findViewById(R.id.textInput_text);
                text.setText(childName);
                break;
            case Child.NUMBERINPUT:
                convertView = inflater.inflate(R.layout.rule_number_input, null);
                final EditText numInput = (EditText) convertView.findViewById(R.id.rule_numberInput);
                numInput.setText(childObject.numberText);

                numInput.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                        childObject.setNumbertext("" + numInput.getText());
                    }
                });



                text = (TextView) convertView.findViewById(R.id.numberInput_text);
                text.setText(childName);
                break;
            case Child.SOUND:

                convertView = inflater.inflate(R.layout.rule_soundtype, null);
                TextView type = (TextView) convertView.findViewById(R.id.soundtype_type);
                type.setText(childObject.name);


                Switch soundActive = (Switch) convertView.findViewById(R.id.soundType_active);
                soundActive.setChecked(childObject.active);
                soundActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        childObject.setActive(isChecked);
                    }
                });
                final TextView soundActual = (TextView) convertView.findViewById(R.id.soundtype_actual);
                soundActual.setText("" + childObject.soundActual);
                final SeekBar soundSlider = (SeekBar) convertView.findViewById(R.id.soundtype_slider);
                soundSlider.setMax(childObject.soundMax);
                soundSlider.setProgress(childObject.soundActual);
                if (childObject.status.equals(DBActionSound.STATUS_SOUND)){
                    soundSlider.setEnabled(true);
                }else {
                    soundSlider.setEnabled(false);
                }

                soundSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        soundActual.setText(""+progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        childObject.setSoundActual(seekBar.getProgress());
                    }
                });

                RadioGroup soundRadioGroup = (RadioGroup) convertView.findViewById(R.id.soundtype_radio);
                soundRadioGroup.removeAllViews();
                //add new buttons
                for (int i = 0; i<childObject.soundOptions.length; i++){
                    RadioButton radioButton = new RadioButton(activity);
                    soundRadioGroup.addView(radioButton);
                    radioButton.setText(childObject.soundOptions[i]);

                    if (childObject.status.equals(childObject.statusOption[i])){
                        radioButton.setChecked(true);
                    }
                }
                soundRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                        String status = childObject.statusOption[(checkedId - 1) % childObject.statusOption.length];
                        if (status.equals(DBActionSound.STATUS_SOUND)){
                            soundSlider.setEnabled(true);
                        }
                        else {
                            soundSlider.setEnabled(false);
                        }

                        childObject.setStatus(status);
                    }
                });



                break;
            default:
                //convertView = inflater.inflate(R.layout.listrow_details, null);
                //text = (TextView) convertView.findViewById(R.id.rule_text);
                //text.setText(children);
                break;




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
        //Log.i(tag,"type "+childObject.type);
        //Log.i(tag,"id" + convertView.getId());
        //Log.i(tag, "################");



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
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
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


        //inflate/create View
        convertView = inflater.inflate(R.layout.listrow_group, null);
        RelativeLayout header = (RelativeLayout)convertView.findViewById(R.id.HeaderRow);
        Switch switchObject = (Switch)convertView.findViewById(R.id.switch1);
        switchObject.setChecked(group.active);

        if (group instanceof Sound){
            header.removeView(switchObject);
        }

        switchObject.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                group.setActive(isChecked);
            }
        });


       /* Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView; */


        CheckedTextView text = (CheckedTextView)convertView.findViewById(R.id.rule_text);

        text.setText(group.name);
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