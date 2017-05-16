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
        this.nusp = intent.getStringExtra(getString(R.string.nusp));

        this.networkController = new NetworkController();
        this.getSeminarsData();

        this.setupDialogView();
        this.createDialog();
    }

    private void getSeminarsData() {
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
                        showMessage(getString(R.string.cannot_fetch_seminars), Toast.LENGTH_LONG);
                    }
                });
            }

            @Override
            public void onError() {
                showMessage(getString(R.string.cannot_fetch_seminars), Toast.LENGTH_LONG);
            }
        });
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(StudentHomeActivity.this, message, duration).show();
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.pagerAdapter = new PagerAdapter(getSupportFragmentManager(), this.allSeminars,
                this.attendedSeminarsId);
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
            intent.putExtra(getString(R.string.nusp), this.nusp);
            intent.putExtra(getString(R.string.type), getString(R.string.student));
            startActivity(intent);
        } else if (id == R.id.student_logout)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void qrCodeClickListener(final String seminarId, final String seminarName) {
        this.qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentQRCodeActivity.class);
                intent.putExtra(getString(R.string.seminar_id), seminarId);
                intent.putExtra(getString(R.string.seminar_name), seminarName);
                intent.putExtra(getString(R.string.nusp), nusp);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void bluetoothClickListener(final String seminarId, final String seminarName) {
        this.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StudentHomeActivity.this, StudentBluetoothActivity.class);
                intent.putExtra(getString(R.string.seminar_id), seminarId);
                intent.putExtra(getString(R.string.seminar_name), seminarName);
                intent.putExtra(getString(R.string.nusp), nusp);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    public void onSeminarClick(final Seminar seminar) {
        this.alertDialog.setTitle(seminar.getName());

        this.qrCodeClickListener(seminar.getId(), seminar.getName());
        this.bluetoothClickListener(seminar.getId(), seminar.getName());

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
                } else
                    showMessage(getString(R.string.cannot_fetch_attended_seminar), Toast.LENGTH_LONG);
            }

            @Override
            public void onError() {}
        });
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] { "Attended Seminars", "All Seminars"};
        private String seminars;
        private String attendedSeminarsId;
        private SeminarsFragment seminarsFragment;
        private AttendedSeminarsFragment attendedSeminarsFragment;

        public PagerAdapter(FragmentManager fm, String seminars, String attendedSeminarsId) {
            super(fm);
            this.seminars = seminars;
            this.attendedSeminarsId = attendedSeminarsId;
            this.seminarsFragment = new SeminarsFragment();
            this.attendedSeminarsFragment = new AttendedSeminarsFragment();

            this.setupSeminarsFragment();
            this.setupAttendedSeminarsFragment();
        }

        public AttendedSeminarsFragment getAttendedSeminarsFragment() {
            return this.attendedSeminarsFragment;
        }

        private void setupSeminarsFragment() {
            Bundle args = new Bundle();
            args.putString(getString(R.string.response), seminars);
            args.putString(getString(R.string.type), getString(R.string.student));

            this.seminarsFragment.setArguments(args);
            this.seminarsFragment.setListener(StudentHomeActivity.this);
        }

        private void setupAttendedSeminarsFragment() {
            Bundle args = new Bundle();
            args.putString(getString(R.string.response), attendedSeminarsId);
            args.putString(getString(R.string.allSeminars), seminars);

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
            View tab = LayoutInflater.from(StudentHomeActivity.this)
                    .inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

}
