package br.usp.ime.checkattendance;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class UpdateProfileActivity extends AppCompatActivity {
    private EditText mNameEditText;
    private EditText mNuspEditText;
    private EditText mPasswdEditText;

    private String type;
    private String nusp_sent;
    private NetworkController networkController;
    private String current_name;
    private ServerCallback serverCallbackGetData;
    private ServerCallback serverCallbackUpdateData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        this.setupActionBar();
        getSentData();
        initializeUIComponents();

        this.networkController = new NetworkController();
        setupCallbacks();
        getCurrentData();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.update_profile_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.type = intent.getStringExtra(getString(R.string.type));
        this.nusp_sent = intent.getStringExtra(getString(R.string.nusp));
    }

    private void initializeUIComponents() {
        this.mNameEditText = (EditText) findViewById(R.id.et_name_edit);
        this.mNuspEditText = (EditText) findViewById(R.id.et_nusp_edit);
        this.mPasswdEditText = (EditText) findViewById(R.id.et_passwd_edit);
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(UpdateProfileActivity.this, message, duration).show();
    }

    private void setupCallbacks() {
        this.serverCallbackGetData = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    setCurrentDataInForm(response);
                }
            }

            @Override
            public void onError() {}
        };

        this.serverCallbackUpdateData = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true"))
                    showMessage(getString(R.string.profile_updated), Toast.LENGTH_LONG);
                else
                    showMessage(getString(R.string.profile_not_updated), Toast.LENGTH_LONG);

                UpdateProfileActivity.this.finish();

            }

            @Override
            public void onError() {
                showMessage(getString(R.string.profile_not_updated), Toast.LENGTH_LONG);
                UpdateProfileActivity.this.finish();
            }
        };
    }

    private void getCurrentData() {
        if (this.type.equals(getString(R.string.student))) {
            this.networkController.getStudentData(this.nusp_sent, this, this.serverCallbackGetData);
        } else {
            this.networkController.getTeacherData(this.nusp_sent, this, this.serverCallbackGetData);
        }
    }

    private void setCurrentDataInForm(String response) {
        try {
            this.current_name = Parser.parseData(response, getString(R.string.profile_name));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.mNuspEditText.setText(this.nusp_sent, TextView.BufferType.EDITABLE);
        this.mNameEditText.setText(this.current_name, TextView.BufferType.EDITABLE);
    }

    public void updateProfile(View v) {
        String name, nusp, passwd;

        name = this.mNameEditText.getText().toString();
        nusp = this.mNuspEditText.getText().toString();
        passwd = this.mPasswdEditText.getText().toString();

        this.validateFields(name, nusp, passwd);
    }

    private void validateFields(String name, String nusp, String passwd) {
        if (name.matches("")) name = this.current_name;
        if (nusp.matches("")) nusp = this.nusp_sent;
        if (passwd.matches(""))
            showMessage(getString(R.string.abscence_passwd), Toast.LENGTH_LONG);
        else
            this.doRequest(name, nusp, passwd);
    }

    private void doRequest(String name, String nusp, String passwd) {
        if (this.type.equals(getString(R.string.student)))
            this.networkController.updateStudentData(nusp, name, passwd, this,
                    this.serverCallbackUpdateData);
        else
            this.networkController.updateTeacherData(nusp, name, passwd, this,
                    this.serverCallbackUpdateData);
    }
}
