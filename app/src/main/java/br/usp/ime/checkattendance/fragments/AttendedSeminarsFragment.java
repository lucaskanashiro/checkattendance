package br.usp.ime.checkattendance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.adapters.SeminarAdapter;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.Parser;

/**
 * Created by kanashiro on 5/6/17.
 */

public class AttendedSeminarsFragment extends Fragment {

    private ArrayList<Seminar> attendedSeminars;
    private String seminars;

    public AttendedSeminarsFragment() {
        this.attendedSeminars = new ArrayList<Seminar>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        this.seminars = args.getString("response");
        this.attendedSeminars = new ArrayList<Seminar>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_attended_seminars, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view_attended);
        rv.setHasFixedSize(true);

        if (this.seminars != null) {
            try {
                this.attendedSeminars = Parser.parseSeminars(this.seminars);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "You did not attended any seminar", Toast.LENGTH_LONG).show();
        }

        SeminarAdapter adapter = new SeminarAdapter(this.attendedSeminars);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

}
