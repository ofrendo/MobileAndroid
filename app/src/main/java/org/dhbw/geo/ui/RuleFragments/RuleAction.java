package org.dhbw.geo.ui.RuleFragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import org.dhbw.geo.ListView.Child;
import org.dhbw.geo.ListView.Group;
import org.dhbw.geo.ListView.MyExpandableListAdapter;
import org.dhbw.geo.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RuleAction.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RuleAction#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RuleAction extends Fragment {

    ArrayList<Group> groups = new ArrayList<Group>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RuleAction.
     */
    // TODO: Rename and change types and number of parameters
    public static RuleAction newInstance(String param1, String param2) {
        RuleAction fragment = new RuleAction();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RuleAction() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        createData();
        ExpandableListView listView = (ExpandableListView) getView().findViewById(R.id.expandableListView);
        MyExpandableListAdapter adapter = new MyExpandableListAdapter(getActivity(),
                groups);
        listView.setAdapter(adapter);
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
        /*for (int j = 0; j < 5; j++) {
            Group group = new Group("Test " + j);
            for (int i = 0; i < 5; i++) {
                group.children.add(new Child("child" + i, false));
            }
            groups.append(j, group);
        }*/

        Group group = new Group ("Wlan");
        group.add(new Child("Active", true,true));
        group.add(new Child("Status",true,false));
        group.add(new Child("Status1",true,false));
        group.add(new Child("Status2",true,false));

        Group group2 = new Group("Sound");
        group2.add(new Child("Active",true,true));
        String [] options = {"hallo","hallo2"};
        //group2.add(new Child("group",options));
        groups.add(group);
        groups.add(group2);

        Group group3 = new Group("whatever");
        group3.add(new Child("active", true,true));
        group3.add(new Child("status", true,false));

        Group group4 = new Group("whatever2");
        group4.add(new Child("active", true,true));
        group4.add(new Child("status", true,false));
        groups.add(group3);
        groups.add(group4);
    }

}
