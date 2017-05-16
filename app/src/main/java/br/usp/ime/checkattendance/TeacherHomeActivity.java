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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import br.usp.ime.checkattendance.fragments.SeminarsFragment;
import br.usp.ime.checkattendance.models.Seminar;
import br.usp.ime.checkattendance.utils.ClickListener;
import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class TeacherHomeActivity extends AppCompatActivity implements ClickListener {
    private String nusp;
    private String allSeminars;
    private ViewPager viewPager;
    private TeacherHomeActivity.PagerAdapter pagerAdapter;
    private TabLayout tabLayout;
    private NetworkController networkController;

    private LayoutInflater layoutInflater;
    private View dialogView;
    private AlertDialog alertDialog;
    private Button editSeminarButton;
    private Button qrCodeButton;
    private Button listAttendeesButton;
    private Button bluetoothButton;

    private final static int REFRESH_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_home);

        this.getSentData();
        this.networkController = new NetworkController();
        this.setupSeminars();
        this.setupDialogView();
        this.createDialog();
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.nusp = intent.getStringExtra(getString(R.string.nusp));
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(TeacherHomeActivity.this, message, duration).show();
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
                        showMessage(getString(R.string.cannot_fetch_seminars), Toast.LENGTH_LONG);
                    }
                }
        );
    }

    private void setupDialogView() {
        this.layoutInflater = LayoutInflater.from(this);
        this.dialogView = layoutInflater.inflate(R.layout.seminar_detail_teacher, null);
        this.initializeDialogComponents();
    }

    private void initializeDialogComponents() {
        this.editSeminarButton = (Button) this.dialogView.findViewById(R.id.btn_edit_seminar);
        this.qrCodeButton = (Button) this.dialogView.findViewById(R.id.btn_qr_code_teacher);
        this.listAttendeesButton = (Button) this.dialogView.findViewById(R.id.btn_list_attendees);
        this.bluetoothButton = (Button) this.dialogView.findViewById(R.id.btn_bluetooth_teacher);
    }

    private void createDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        this.alertDialog = builder.create();
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
            intent.putExtra(getString(R.string.type), getString(R.string.teacher));
            startActivity(intent);
        } else if (id == R.id.item_teacher_logout) {
            finish();
        } else if (id == R.id.item_edit_teacher) {
            Intent intent = new Intent(TeacherHomeActivity.this, UpdateProfileActivity.class);
            intent.putExtra(getString(R.string.type), getString(R.string.teacher));
            intent.putExtra(getString(R.string.nusp), this.nusp);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager() {
        this.viewPager = (ViewPager) findViewById(R.id.viewpager);
        this.pagerAdapter = new TeacherHomeActivity.PagerAdapter(getSupportFragmentManager(),
                this.allSeminars);
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
            this.updateFragmentsData();
            this.alertDialog.dismiss();
        }
    }

    private void updateFragmentsData() {
        this.networkController.getAllSeminars(this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                allSeminars = response;
                pagerAdapter.getFragment().setData(response);
            }

            @Override
            public void onError() {}
        });
    }

    private void editSeminarClickListener(final String seminarId) {
        this.editSeminarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomeActivity.this, UpdateSeminarActivity.class);
                intent.putExtra(getString(R.string.seminar_id), seminarId);
                startActivityForResult(intent, 0);
            }
        });
    }

    private void listAttendeesClickListener(final String seminarId) {
        this.listAttendeesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomeActivity.this, ListAttendeesActivity.class);
                intent.putExtra(getString(R.string.seminar_id), seminarId);
                startActivity(intent);
            }
        });
    }

    private void qrCodeClickListener(final String seminarId, final String seminarName) {
        this.qrCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomeActivity.this, TeacherQRCodeActivity.class);
                intent.putExtra(getString(R.string.seminar_id), seminarId);
                intent.putExtra(getString(R.string.seminar_name), seminarName);
                startActivity(intent);
            }
        });
    }

    private void bluetoothClickListener(final String seminarId, final String seminarName) {
        this.bluetoothButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TeacherHomeActivity.this, TeacherBluetoothActivity.class);
                intent.putExtra(getString(R.string.seminar_id), seminarId);
                intent.putExtra(getString(R.string.seminar_name), seminarName);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onSeminarClick(final Seminar seminar) {
        this.alertDialog.setTitle(seminar.getName());

        this.editSeminarClickListener(seminar.getId());
        this.listAttendeesClickListener(seminar.getId());
        this.qrCodeClickListener(seminar.getId(), seminar.getName());
        this.bluetoothClickListener(seminar.getId(), seminar.getName());

        this.alertDialog.setView(this.dialogView);
        this.alertDialog.show();
    }

    public class PagerAdapter extends FragmentPagerAdapter {
        private String tabTitles[] = new String[] {"Seminars"};
        private String seminars;
        private Bundle args;
        private SeminarsFragment fragment;

        public PagerAdapter(FragmentManager fm, String seminars) {
            super(fm);
            this.seminars = seminars;

            this.setupFragmentArgs();
            this.setupFragments();
        }

        public SeminarsFragment getFragment() {
            return this.fragment;
        }

        private void setupFragmentArgs() {
            this.args = new Bundle();
            args.putString(getString(R.string.response), seminars);
            args.putString(getString(R.string.type), getString(R.string.teacher));
        }

        private void setupFragments() {
            this.fragment = new SeminarsFragment();
            this.fragment.setArguments(this.args);
            this.fragment.setListener(TeacherHomeActivity.this);
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
            View tab = LayoutInflater.from(TeacherHomeActivity.this).inflate(R.layout.custom_tab, null);
            TextView tv = (TextView) tab.findViewById(R.id.custom_text);
            tv.setText(tabTitles[position]);
            return tab;
        }
    }

}
