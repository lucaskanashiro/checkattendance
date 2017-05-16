package br.usp.ime.checkattendance.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.StudentHomeActivity;
import br.usp.ime.checkattendance.adapters.SeminarAdapter;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.ClickListener;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;
import br.usp.ime.checkattendance.utils.RequestQueueSingleton;
import br.usp.ime.checkattendance.utils.ServerCallback;

/**
 * Created by kanashiro on 5/6/17.
 */

public class SeminarsFragment extends Fragment {
    private ArrayList<Seminar> seminars;
    private String allSeminars;
    private String type;
    private Bundle args;
    private View rootView;
    private RecyclerView recyclerView;
    private SeminarAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private ClickListener listener;

    public SeminarsFragment() {
        this.seminars = new ArrayList<Seminar>();
    }

    public void setListener(ClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getBundleArgs();
    }

    private void getBundleArgs() {
        this.args = getArguments();
        this.allSeminars = args.getString(getString(R.string.response));
        this.type = args.getString(getString(R.string.type));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflateLayout(inflater, container);

        this.seminars = Parser.parseStringResponse(this.allSeminars);
        this.adapter = new SeminarAdapter(this.seminars, this.listener);
        this.linearLayoutManager = new LinearLayoutManager(getActivity());
        this.setupRecyclerView();

        return this.rootView;
    }

    private void inflateLayout(LayoutInflater inflater, ViewGroup container) {
        if (this.type.equals(getString(R.string.student)))
            this.rootView = inflater.inflate(R.layout.fragment_seminars, container, false);
        else
            this.rootView = inflater.inflate(R.layout.fragment_seminars_teacher, container, false);
    }

    private void setupRecyclerView() {
        this.recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(this.linearLayoutManager);
    }

    public void setData(String seminars) {
        ArrayList<Seminar> allSeminars= Parser.parseStringResponse(seminars);
        SeminarAdapter adapter = new SeminarAdapter(allSeminars, this.listener);
        this.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
