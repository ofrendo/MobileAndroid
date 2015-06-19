package org.dhbw.geo.ui.RuleFragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import org.dhbw.geo.database.DBAction;
import org.dhbw.geo.database.DBActionMessage;
import org.dhbw.geo.database.DBActionNotification;
import org.dhbw.geo.ui.ListView.Bluetooth;
import org.dhbw.geo.ui.ListView.Child;
import org.dhbw.geo.ui.ListView.Group;
import org.dhbw.geo.ui.ListView.Message;
import org.dhbw.geo.ui.ListView.MyExpandableListAdapter;
import org.dhbw.geo.R;
import org.dhbw.geo.ui.ListView.Notification;
import org.dhbw.geo.ui.ListView.Sound;
import org.dhbw.geo.ui.ListView.WLAN;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RuleAction.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RuleAction} factory method to
 * create an instance of this fragment.
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

    private void listActions() {
        ArrayList<String> list = new ArrayList<String>();
        for (int i=0;i<groups.size();i++){
            Group gp = groups.get(i);
            if (gp.getActions()!=null)
            list.addAll(gp.getActions());
        }
        for (int i=0;i<list.size();i++){
            Log.i("Action",list.get(i));
        }


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

        ArrayList<DBAction> actions = DBAction.selectAllFromDB(activity.rule.getId());
        for (int i = 0; i<actions.size(); i++){
            DBAction action = actions.get(i);
            if (action instanceof DBActionMessage){
                Log.e("MESSAGE","Message vorhanden");
                message = new Message((DBActionMessage)action);
            }
            else if (action instanceof DBActionNotification){
                notification = new Notification(((DBActionNotification)action).getMessage());
            }
        }




        if (message == null){
            message = new Message(activity.rule);
        }
        groups.add(message);
        groups.add(new Notification());
        groups.add(new WLAN());
        groups.add(new Bluetooth());
        groups.add(new Sound());

    }

}
