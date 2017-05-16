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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.adapters.SeminarAdapter;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.ClickListener;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;
import br.usp.ime.checkattendance.utils.ServerCallback;

/**
 * Created by kanashiro on 5/6/17.
 */

public class AttendedSeminarsFragment extends Fragment {
    private ArrayList<Seminar> attendedSeminars;
    private String seminarsId;
    private String allSeminars;
    private View rootView;
    private RecyclerView recyclerView;
    private SeminarAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private String[] seminarIdsArray;
    private ArrayList<Seminar> seminars;

    public AttendedSeminarsFragment() {
        this.attendedSeminars = new ArrayList<Seminar>();
        this.seminars = new ArrayList<Seminar>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        this.seminarsId = args.getString(getString(R.string.response));
        this.allSeminars = args.getString(getString(R.string.allSeminars));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflateLayout(inflater, container);
        this.parseAttendedSeminars();
        return this.rootView;
    }

    private void setupFragment() {
        this.adapter = new SeminarAdapter(this.attendedSeminars,
                getString(R.string.student), null);
        this.linearLayoutManager = new LinearLayoutManager(getActivity());
        this.setupRecyclerView();
    }

    private Seminar findSeminar(String id) {
        for (Seminar s : this.seminars) {
            if (s.getId().equals(id))
                return s;
        }
        return null;
    }

    private void parseAttendedSeminars() {
        if (this.seminarsId != null) {
            this.attendedSeminars.clear();
            this.seminarIdsArray = seminarsId.split("\\s+");
            this.seminars = Parser.parseStringResponse(this.allSeminars);

            for(int i=0; i < seminarIdsArray.length; i++) {
                Seminar s = findSeminar(seminarIdsArray[i]);
                if (s != null)
                    this.attendedSeminars.add(s);
            }

            this.setupFragment();
        }
    }

    private void inflateLayout(LayoutInflater inflater, ViewGroup container) {
        this.rootView = inflater.inflate(R.layout.fragment_attended_seminars, container, false);
    }

    private void setupRecyclerView() {
        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view_attended);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
    }

    public void setData(String seminars) {
        try {
            this.seminarsId = Parser.parseAttendedSeminars(seminars);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.parseAttendedSeminars();
        this.setAdapterOnRecyclerView();
        adapter.notifyDataSetChanged();
    }

    private void setAdapterOnRecyclerView() {
        SeminarAdapter adapter = new SeminarAdapter(this.attendedSeminars,
                getString(R.string.student), null);
        this.recyclerView.setAdapter(adapter);
    }
}
