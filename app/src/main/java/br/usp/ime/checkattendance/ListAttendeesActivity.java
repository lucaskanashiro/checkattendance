package br.usp.ime.checkattendance;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import br.usp.ime.checkattendance.fragments.StudentsFragment;
import br.usp.ime.checkattendance.models.Student;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class ListAttendeesActivity extends AppCompatActivity {
    private String seminarId;
    private NetworkController networkController;
    private String attendeesId;
    private String allStudents;

    private ViewPager viewPager;
    private ListAttendeesActivity.PagerAdapter pagerAdapter;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_attendees);

        this.setupActionBar();
        this.getSentData();
        this.networkController = new NetworkController();
        this.getAttendeesData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.list_attendees_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.seminarId = intent.getStringExtra(getString(R.string.seminar_id));
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager_list_attendees);
        this.pagerAdapter = new ListAttendeesActivity.PagerAdapter(getSupportFragmentManager(),
                this.allStudents, this.attendeesId);
        this.viewPager.setAdapter(this.pagerAdapter);
    }

    private void setupTabLayout() {
        this.tabLayout = (TabLayout) findViewById(R.id.tab_layout_list_attendees);
        this.tabLayout.setupWithViewPager(this.viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = this.tabLayout.getTabAt(i);
            tab.setCustomView(this.pagerAdapter.getTabView(i));
        }
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(ListAttendeesActivity.this, message, duration).show();
    }

    private void getAllStudentsData() {
        this.networkController.getAllStudents(this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    allStudents = response;
                    setupViewPager();
                    setupTabLayout();
                }
            }

            @Override
            public void onError() {
                showMessage(getString(R.string.cannot_fetch_students), Toast.LENGTH_LONG);
            }
        });
    }

    private void getAttendeesData() {
        this.networkController.getAttendees(this.seminarId, this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    try {
                        attendeesId = Parser.parseAttendees(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    getAllStudentsData();
                }
            }

            @Override
            public void onError() {}
        });
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] {"Attendees"};
        private String students;
        private String attendees;
        private Bundle args;
        private StudentsFragment fragment;

        public PagerAdapter(FragmentManager fm, String students, String attendees) {
            super(fm);
            this.students = students;
            this.attendees = attendees;

            this.setupFragmentArgs();
            this.setupFragments();
        }

        private void setupFragmentArgs() {
            this.args = new Bundle();
            args.putString(getString(R.string.response), attendees);
            args.putString(getString(R.string.allStudents), students);
        }

        private void setupFragments() {
            this.fragment = new StudentsFragment();
            this.fragment.setArguments(this.args);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return this.fragment;
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(ListAttendeesActivity.this)
                    .inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

}
