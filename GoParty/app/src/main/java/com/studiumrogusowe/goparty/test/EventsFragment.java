package com.studiumrogusowe.goparty.test;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.studiumrogusowe.goparty.R;

import java.util.ArrayList;

public class EventsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public EventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ListView eventsList = (ListView) view.findViewById(R.id.eventsListView);

        ArrayList<String> events = new ArrayList<String>();
        events.add("19.04.2015 Koncert Rockaway");
        events.add("21.04.2015 BeatBattle KRK");
        events.add("22.04.2015 Sztuka samplingu");


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                view.getContext(),
                android.R.layout.simple_list_item_1,
                events);

        eventsList.setAdapter(arrayAdapter);
        // Inflate the layout for this fragment
        return view;
    }

}
