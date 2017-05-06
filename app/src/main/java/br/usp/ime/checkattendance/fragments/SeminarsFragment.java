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
import br.usp.ime.checkattendance.utils.RequestQueueSingleton;

/**
 * Created by kanashiro on 5/6/17.
 */

public class SeminarsFragment extends Fragment {

    private ArrayList<Seminar> seminars;
    private String allSeminars;

    public SeminarsFragment() {
        this.seminars = new ArrayList<Seminar>();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        this.allSeminars = args.getString("response");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_seminars, container, false);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.rv_recycler_view);
        rv.setHasFixedSize(true);

        try {
            this.parseSeminars(this.allSeminars);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d("SEMINARS FRAGMENT", this.seminars.toString() + "\n");
        SeminarAdapter adapter = new SeminarAdapter(this.seminars);
        rv.setAdapter(adapter);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);

        return rootView;
    }

    private void updateSeminars(JSONObject seminar) throws JSONException{
        Seminar s = new Seminar(seminar.getString("id"), seminar.getString("name"));
        this.seminars.add(s);
    }

    private void parseSeminars(String response) throws JSONException{
        JSONObject json = new JSONObject(response);
        JSONArray data = json.getJSONArray("data");

        for(int i = 0; i < data.length(); i++) {
            JSONObject seminar = data.getJSONObject(i);
            updateSeminars(seminar);
        }
    }

}
