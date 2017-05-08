package br.usp.ime.checkattendance;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
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

    private final static int REFRESH_PAGE = 1;

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
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REFRESH_PAGE) {
            this.networkController.getAllSeminars(this, new ServerCallback() {
                @Override
                public void onSuccess(String response) {
                    allSeminars = response;
                    pagerAdapter.getFragment1().setData(response);
                    pagerAdapter.getFragment2().setData(response);
                }

                @Override
                public void onError() {}
            });
        }
    }

    public void cardDetails(View v) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View promptView = layoutInflater.inflate(R.layout.card_detail_teacher, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final AlertDialog alert = builder.create();

        TextView seminarName = (TextView) promptView.findViewById(R.id.tv_seminar_name_details);
        ImageButton closeButton = (ImageButton) promptView.findViewById(R.id.btn_close);
        ImageButton editSeminar = (ImageButton) promptView.findViewById(R.id.btn_edit_seminar);

        seminarName.setText("blablablablablablabla");
        editSeminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomeActivity.this, UpdateSeminarActivity.class);
                startActivity(intent);
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.dismiss();
            }
        });

        alert.setView(promptView);
        alert.show();
    }

    class PagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Sponsored Seminars", "All Seminars"};
        private Context context;
        private String seminars;
        private Bundle args;
        private SeminarsFragment fragment1, fragment2;

        public PagerAdapter(FragmentManager fm, Context context, String seminars) {
            super(fm);
            this.context = context;
            this.seminars = seminars;

            this.setupFragmentArgs();
            this.setupFragments();
        }

        public SeminarsFragment getFragment1() {
            return this.fragment1;
        }

        public SeminarsFragment getFragment2() {
            return this.fragment2;
        }

        private void setupFragmentArgs() {
            this.args = new Bundle();
            args.putString("response", seminars);
            args.putString("type", "teacher");
        }

        private void setupFragments() {
            this.fragment1 = new SeminarsFragment();
            this.fragment1.setArguments(this.args);

            this.fragment2 = new SeminarsFragment();
            this.fragment2.setArguments(this.args);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return this.fragment1;
                case 1:
                    return this.fragment2;
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
