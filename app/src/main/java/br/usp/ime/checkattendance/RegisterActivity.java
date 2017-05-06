package br.usp.ime.checkattendance;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.RequestQueueSingleton;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mNuspEditText;
    private EditText mPasswdEditText;
    private RadioGroup mTypeRadioGroup;

    private String nusp;
    private String passwd;
    private String name;
    private boolean isStudent;
    private NetworkController networkController;
    private ServerCallback serverCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Register");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.networkController = new NetworkController();
        initializeUIComponents();
        initializeCallback();
    }

    private void initializeUIComponents() {
        this.mNameEditText = (EditText) findViewById(R.id.et_name);
        this.mNuspEditText = (EditText) findViewById(R.id.et_nusp_register);
        this.mPasswdEditText = (EditText) findViewById(R.id.et_passwd_register);
        this.mTypeRadioGroup = (RadioGroup) findViewById(R.id.rg_type);
    }

    private void initializeCallback() {
        this.serverCallback = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    String type = (isStudent) ? "student" : "teacher";
                    String message = "You are registered as new " + type;
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                    NavUtils.navigateUpFromSameTask(RegisterActivity.this);
                } else {
                    String message = "This NUSP is already registered";
                    Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();

                    mNuspEditText.setText("");
                    mPasswdEditText.setText("");
                }
            }

            @Override
            public void onError() {
                String message = "We had some problem. Please, try again later";
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                NavUtils.navigateUpFromSameTask(RegisterActivity.this);
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            NavUtils.navigateUpFromSameTask(this);

        return super.onOptionsItemSelected(item);
    }

    private void getInputs() {
        this.name = this.mNameEditText.getText().toString();
        this.nusp = this.mNuspEditText.getText().toString();
        this.passwd = this.mPasswdEditText.getText().toString();

        int selectedID = this.mTypeRadioGroup.getCheckedRadioButtonId();
        RadioButton selectedTypeRadioButton = (RadioButton) findViewById(selectedID);

        if (selectedTypeRadioButton.getText().toString().equals("Student")) {
            this.isStudent = true;
        } else {
            this.isStudent = false;
        }
    }

    public void signUp(View v) {
        getInputs();

        if (this.isStudent) {
            this.registerStudent();
        } else {
            this.registerTeacher();
        }
    }

    private void registerStudent() {
        String url = "http://207.38.82.139:8001/student/add";
        this.networkController.register(url, this.name, this.nusp, this.passwd, this,
                this.serverCallback);
    }

    private void registerTeacher() {
        String url = "http://207.38.82.139:8001/teacher/add";
        this.networkController.register(url, this.name, this.nusp, this.passwd, this,
                this.serverCallback);
    }

}
