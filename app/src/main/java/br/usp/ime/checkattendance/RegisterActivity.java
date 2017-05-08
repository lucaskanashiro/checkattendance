package br.usp.ime.checkattendance;

import android.content.Intent;
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

    private String nusp;
    private String passwd;
    private String name;
    private String type;
    private NetworkController networkController;
    private ServerCallback serverCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setupActionBar();
        getSentData();

        this.networkController = new NetworkController();
        initializeUIComponents();
        initializeCallback();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Register");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.type = intent.getStringExtra("type");
    }

    private void initializeUIComponents() {
        this.mNameEditText = (EditText) findViewById(R.id.et_name);
        this.mNuspEditText = (EditText) findViewById(R.id.et_nusp_register);
        this.mPasswdEditText = (EditText) findViewById(R.id.et_passwd_register);
    }

    private void initializeCallback() {
        this.serverCallback = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                String message = "You are registered as new " + type;
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError() {
                String message = "We had some problem. Please, try again later";
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void getInputs() {
        this.name = this.mNameEditText.getText().toString();
        this.nusp = this.mNuspEditText.getText().toString();
        this.passwd = this.mPasswdEditText.getText().toString();
    }

    public void signUp(View v) {
        getInputs();

        if (this.type.equals("student")) {
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
