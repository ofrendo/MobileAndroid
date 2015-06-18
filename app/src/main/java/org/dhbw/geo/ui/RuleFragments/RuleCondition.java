package org.dhbw.geo.ui.RuleFragments;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.dhbw.geo.R;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RuleCondition.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RuleCondition#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RuleCondition extends ListFragment {
    ArrayList<String> listItems=new ArrayList<String>();


    ArrayAdapter<String> adapter;




    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RuleCondition.
     */
    // TODO: Rename and change types and number of parameters
    public static RuleCondition newInstance(String param1, String param2) {
        RuleCondition fragment = new RuleCondition();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RuleCondition() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listItems.add("hallo");
        //set adapter for dynamical listview
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,listItems);
        setListAdapter(adapter);


        //on click listener for listviewelements
        ListView listView = getListView();
        listView.setTextFilterEnabled(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // When clicked, show a toast with the TextView text
                Intent nextScreen = new Intent(getActivity().getApplicationContext(), RuleContainer.class);
                startActivity(nextScreen);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_rule_condition, container, false);
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

    @Override
    public void onResume(){
        super.onResume();
        //add items to listItems and notifyChange
        listItems.add("hallo2");
        adapter.notifyDataSetChanged();
    }

}
