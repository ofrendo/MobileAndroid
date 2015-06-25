package org.dhbw.geo.ui.RuleFragments;

import android.app.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.dhbw.geo.database.DBAction;
import org.dhbw.geo.database.DBActionMessage;
import org.dhbw.geo.database.DBActionNotification;
import org.dhbw.geo.database.DBActionSimple;
import org.dhbw.geo.database.DBActionSound;
import org.dhbw.geo.ui.ListView.Bluetooth;
import org.dhbw.geo.ui.ListView.Group;
import org.dhbw.geo.ui.ListView.Message;
import org.dhbw.geo.ui.ListView.MyExpandableListAdapter;
import org.dhbw.geo.R;
import org.dhbw.geo.ui.ListView.Notification;
import org.dhbw.geo.ui.ListView.Sound;
import org.dhbw.geo.ui.ListView.WLAN;

import java.util.ArrayList;

/**
 * Fragmnent for the ActionsTab for Rules
 * @author Joern
 */
public class RuleAction extends Fragment {

    ArrayList<Group> groups = new ArrayList<Group>();
    RuleContainer activity;
    ArrayList<DBAction> actionList;
    WLAN wlan;
    Message message;
    Notification notification;
    Sound sound;
    Bluetooth bluetooth;


    private OnFragmentInteractionListener mListener;


    public RuleAction() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        createData();
        ExpandableListView listView = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(getActivity(),
                groups);
        listView.setAdapter(adapter);
        /*
        Button btn = (Button)getActivity().findViewById(R.id.testbutton);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listActions();
            }
        });*/
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rule_action, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (RuleContainer)activity;

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void createData() {

        if (groups.size()!=0){
            return;
        }
        activity.rule.loadAllActions();

        //create soundcontainer
        sound = new Sound();

        ArrayList<DBAction> actions = DBAction.selectAllFromDB(activity.rule.getId());
        for (int i = 0; i<actions.size(); i++){
            DBAction action = actions.get(i);
            if (action instanceof DBActionMessage){
                message = new Message((DBActionMessage)action);
            }
            else if (action instanceof DBActionNotification){
                notification = new Notification((DBActionNotification)action);
            }
            else if (action instanceof DBActionSound){
                sound.addChild((DBActionSound)action);
            }
            else if (action instanceof DBActionSimple){
                if (((DBActionSimple) action).getType().equals(DBActionSimple.TYPE_WIFI)){

                    wlan = new WLAN((DBActionSimple)action);
                }
                else if (((DBActionSimple) action).getType().equals(DBActionSimple.TYPE_BLUETOOTH)){
                    bluetooth = new Bluetooth((DBActionSimple)action);
                }
            }
        }




        if (message == null){
            message = new Message(activity.rule);
        }
        if (notification == null){
            notification = new Notification(activity.rule);
        }
        if (wlan == null){
            wlan = new WLAN(activity.rule);
        }
        if (bluetooth == null){
            bluetooth = new Bluetooth(activity.rule);
        }

        sound.addChildren(activity.rule);

        groups.add(message);
        groups.add(notification);
        groups.add(wlan);
        groups.add(bluetooth);
        groups.add(sound);

    }

}
