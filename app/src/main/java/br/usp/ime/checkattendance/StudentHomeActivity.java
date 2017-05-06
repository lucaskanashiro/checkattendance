package br.usp.ime.checkattendance;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.ArrayList;

import br.usp.ime.checkattendance.fragments.SeminarsFragment;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.RequestQueueSingleton;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class StudentHomeActivity extends AppCompatActivity {

    private String nusp;
    private String allSeminars;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private NetworkController networkController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Intent intent = getIntent();
        this.nusp = intent.getStringExtra("nusp");

        this.networkController = new NetworkController();

        this.networkController.getAllSeminars(this, new ServerCallback() {
                    @Override
                    public void onSuccess(String response) {
                        allSeminars = response;
                        setupViewPager();
                        setupTabLayout();
                    }

                    @Override
                    public void onError() {
                        String message = "Sorry, we cannot fetch seminars data";
                        Toast.makeText(StudentHomeActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                StudentHomeActivity.this, this.allSeminars);
        this.viewPager.setAdapter(pagerAdapter);
    }

    private void setupTabLayout() {
        this.tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        this.tabLayout.setupWithViewPager(this.viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = this.tabLayout.getTabAt(i);
            tab.setCustomView(this.pagerAdapter.getTabView(i));
        }
    }

    class PagerAdapter extends FragmentPagerAdapter {
        String tabTitles[] = new String[] { "Attended Seminars", "All Seminars"};
        Context context;
        String seminars;

        public PagerAdapter(FragmentManager fm, Context context, String seminars) {
            super(fm);
            this.context = context;
            this.seminars = seminars;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    Fragment seminarFragment = new SeminarsFragment();
                    Bundle args = new Bundle();
                    args.putString("response", seminars);
                    seminarFragment.setArguments(args);
                    return seminarFragment;
                case 1:
                    Fragment seminarFragment2 = new SeminarsFragment();
                    Bundle args2 = new Bundle();
                    args2.putString("response", seminars);
                    seminarFragment2.setArguments(args2);
                    return seminarFragment2;
            }

            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        public View getTabView(int position) {
            View tab = LayoutInflater.from(StudentHomeActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

}