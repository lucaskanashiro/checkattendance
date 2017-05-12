package br.usp.ime.checkattendance.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;

import java.util.ArrayList;

import br.usp.ime.checkattendance.R;
import br.usp.ime.checkattendance.adapters.StudentAdapter;
import br.usp.ime.checkattendance.models.Student;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;

/**
 * Created by kanashiro on 5/11/17.
 */

public class StudentsFragment extends android.support.v4.app.Fragment {

    private ArrayList<Student> students;
    private ArrayList<Student> attendeesStudents;
    private View rootView;
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private NetworkController networkController;
    private String studentsId;
    private String[] studentsIdArray;
    private String allStudents;

    public StudentsFragment() {
        this.students = new ArrayList<Student>();
        this.networkController = new NetworkController();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        this.studentsId = args.getString("response");
        this.allStudents = args.getString("allStudents");
        this.attendeesStudents = new ArrayList<Student>();
        this.students = new ArrayList<Student>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        this.inflateLayout(inflater, container);
        this.parseStudents();
        return this.rootView;
    }

    private void setupFragment() {
        for(Student s : this.attendeesStudents)
            Log.d("ATTENDEE", s.toString());
        this.adapter = new StudentAdapter(this.attendeesStudents);
        this.linearLayoutManager = new LinearLayoutManager(getActivity());
        this.setupRecyclerView();
    }

    private Student findSeminar(String nusp) {
        for (Student s : this.students) {
            if (s.getNusp().equals(nusp))
                return s;
        }
        return null;
    }

    private void parseStudents() {
        if (this.studentsId != null) {
            this.attendeesStudents.clear();
            this.studentsIdArray = studentsId.split("\\s+");
            this.students = Parser.parseAllStudents(this.allStudents);

            for(int i=0; i < studentsIdArray.length; i++) {
                Student s = findSeminar(studentsIdArray[i]);
                if (s != null)
                    this.attendeesStudents.add(s);
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
            this.studentsId = Parser.parseAttendedSeminars(seminars);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        this.parseStudents();

        StudentAdapter adapter = new StudentAdapter(this.attendeesStudents);
        this.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}
