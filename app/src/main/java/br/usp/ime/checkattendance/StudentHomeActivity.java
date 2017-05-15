package br.usp.ime.checkattendance;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;

import br.usp.ime.checkattendance.fragments.AttendedSeminarsFragment;
import br.usp.ime.checkattendance.fragments.SeminarsFragment;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.ClickListener;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;
import br.usp.ime.checkattendance.utils.RequestQueueSingleton;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class StudentHomeActivity extends AppCompatActivity implements ClickListener {

    private String nusp;
    private String allSeminars;
    private String attendedSeminarsId;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private NetworkController networkController;

    private LayoutInflater layoutInflater;
    private View dialogView;
    private AlertDialog alertDialog;
    private Button qrCodeButton;
    private Button bluetoothButton;

    private final int REFRESH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_home);

        Intent intent = getIntent();
        this.nusp = intent.getStringExtra("nusp");

        this.networkController = new NetworkController();

        this.networkController.getAttendedSeminars(this.nusp, this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    attendedSeminarsId = Parser.parseAttendedSeminars(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                networkController.getAllSeminars(StudentHomeActivity.this, new ServerCallback() {
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

            @Override
            public void onError() {
                String message = "Sorry, we cannot fetch seminars data";
                Toast.makeText(StudentHomeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });

        this.setupDialogView();
        this.createDialog();
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.pagerAdapter = new PagerAdapter(getSupportFragmentManager(),
                StudentHomeActivity.this, this.allSeminars, this.attendedSeminarsId);
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

    private void setupDialogView() {
        this.layoutInflater = LayoutInflater.from(this);
        this.dialogView = layoutInflater.inflate(R.layout.seminar_detail_student, null);
        this.initializeDialogComponents();
    }

    private void initializeDialogComponents() {
        this.qrCodeButton = (Button) this.dialogView.findViewById(R.id.btn_qr_code_student);
        this.bluetoothButton = (Button) this.dialogView.findViewById(R.id.btn_bluetooth_student);
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        this.alertDialog = builder.create();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.student_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.student_edit_profile) {
            Intent intent = new Intent(StudentHomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra("nusp", this.nusp);
            intent.putExtra("type", "student");
            startActivity(intent);
        } else if (id == R.id.student_logout) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSeminarClick(final Seminar seminar) {
        this.alertDialog.setTitle(seminar.getName());

        this.qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentQRCodeActivity.class);
                intent.putExtra("id", seminar.getId());
                intent.putExtra("name", seminar.getName());
                intent.putExtra("nusp", nusp);
                startActivityForResult(intent, 0);
            }
        });

        this.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentBluetoothActivity.class);
                intent.putExtra("id", seminar.getId());
                intent.putExtra("name", seminar.getName());
                startActivity(intent);
            }
        });

        this.alertDialog.setView(this.dialogView);
        this.alertDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == REFRESH) {
            this.updateFragmentsData();
            this.alertDialog.dismiss();
        }
    }

    private void updateFragmentsData() {
        this.networkController.getAttendedSeminars(this.nusp, this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if(response.contains("\"success\":true")) {
                    try {
                        attendedSeminarsId = Parser.parseAttendedSeminars(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    pagerAdapter.getAttendedSeminarsFragment().setData(response);
                } else {
                    String message = "We had some problems to fetch your attended seminar, sorry";
                    Toast.makeText(StudentHomeActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError() {}
        });
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Attended Seminars", "All Seminars"};
        private Context context;
        private String seminars;
        private String attendedSeminarsId;
        private SeminarsFragment seminarsFragment;
        private AttendedSeminarsFragment attendedSeminarsFragment;

        public PagerAdapter(FragmentManager fm, Context context, String seminars,
                            String attendedSeminarsId) {
            super(fm);
            this.context = context;
            this.seminars = seminars;
            this.attendedSeminarsId = attendedSeminarsId;
            this.seminarsFragment = new SeminarsFragment();
            this.attendedSeminarsFragment = new AttendedSeminarsFragment();

            this.setupSeminarsFragment();
            this.setupAttendedSeminarsFragment();
        }

        public SeminarsFragment getSeminarsFragment() {
            return this.seminarsFragment;
        }

        public AttendedSeminarsFragment getAttendedSeminarsFragment() {
            return this.attendedSeminarsFragment;
        }

        private void setupSeminarsFragment() {
            Bundle args = new Bundle();
            args.putString("response", seminars);
            args.putString("type", "student");

            this.seminarsFragment.setArguments(args);
            this.seminarsFragment.setListener(StudentHomeActivity.this);
        }

        private void setupAttendedSeminarsFragment() {
            Bundle args = new Bundle();
            args.putString("response", attendedSeminarsId);
            args.putString("allSeminars", seminars);

            this.attendedSeminarsFragment.setArguments(args);
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return this.attendedSeminarsFragment;
                case 1:
                    return this.seminarsFragment;
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
