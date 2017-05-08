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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import br.usp.ime.checkattendance.fragments.SeminarsFragment;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class TeacherHomeActivity extends AppCompatActivity {

    private String nusp;
    private String allSeminars;
    private ViewPager viewPager;
    private TeacherHomeActivity.PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private NetworkController networkController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        this.getSentData();

        this.networkController = new NetworkController();
        this.setupSeminars();
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.nusp = intent.getStringExtra("nusp");
    }

    private void setupSeminars() {
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
                        Toast.makeText(TeacherHomeActivity.this, message, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.teacher_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.item_register_teacher) {
            Intent intent = new Intent(TeacherHomeActivity.this, RegisterActivity.class);
            intent.putExtra("type", "teacher");
            startActivity(intent);
        } else if (id == R.id.item_teacher_logout) {
            finish();
        } else if (id == R.id.item_edit_teacher) {
            Intent intent = new Intent(TeacherHomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra("type", "teacher");
            intent.putExtra("nusp", this.nusp);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.pagerAdapter = new TeacherHomeActivity.PagerAdapter(getSupportFragmentManager(),
                TeacherHomeActivity.this, this.allSeminars);
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

    public void createSeminar(View v) {
        Intent intent = new Intent(TeacherHomeActivity.this, RegisterSeminarActivity.class);
        startActivity(intent);
    }

    class PagerAdapter extends FragmentPagerAdapter {
        String tabTitles[] = new String[] { "Sponsored Seminars", "All Seminars"};
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
                    args.putString("type", "teacher");
                    seminarFragment.setArguments(args);
                    return seminarFragment;
                case 1:
                    Fragment seminarFragment2 = new SeminarsFragment();
                    Bundle args2 = new Bundle();
                    args2.putString("response", seminars);
                    args2.putString("type", "teacher");
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
            View tab = LayoutInflater.from(TeacherHomeActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

}
