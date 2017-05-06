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

import br.usp.ime.checkattendance.utils.RequestQueueSingleton;

public class RegisterActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private EditText mNuspEditText;
    private EditText mPasswdEditText;
    private RadioGroup mTypeRadioGroup;

    private String nusp;
    private String passwd;
    private String name;
    private boolean isStudent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Register");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        initializeUIComponents();
    }

    private void initializeUIComponents() {
        this.mNameEditText = (EditText) findViewById(R.id.et_name);
        this.mNuspEditText = (EditText) findViewById(R.id.et_nusp_register);
        this.mPasswdEditText = (EditText) findViewById(R.id.et_passwd_register);
        this.mTypeRadioGroup = (RadioGroup) findViewById(R.id.rg_type);
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
        this.register(url, this.name, this.nusp, this.passwd);
    }

    private void registerTeacher() {
        String url = "http://207.38.82.139:8001/teacher/add";
        this.register(url, this.name, this.nusp, this.passwd);
    }

    private void register(String url, final String name, final String nusp,
                          final String passwd) {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("REGISTER", response +  "\n");
                        if (response.contains("\"success\":true")) {
                            RegisterActivity.this.successedRegister();
                        } else {
                            RegisterActivity.this.failureRegister();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                RegisterActivity.this.failureRequest();
            }
        }
        ) {
            String body = "nusp=" + nusp + "&pass=" + passwd + "&name=" + name;
            @Override
            public byte[] getBody() throws AuthFailureError {
                return body.getBytes();
            }
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded; charset=UTF-8";
            }
        };

        RequestQueueSingleton.getInstance(this).addToRequestQueue(request);
    }

    private void successedRegister() {
        String type = (this.isStudent) ? "student" : "teacher";
        String message = "You are registered as new " + type;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        NavUtils.navigateUpFromSameTask(this);
    }

    private void failureRegister() {
        String message = "This NUSP is already registered";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        this.mNuspEditText.setText("");
        this.mPasswdEditText.setText("");
    }

    private void failureRequest() {
        String message = "We had some problem. Please, try again later";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        NavUtils.navigateUpFromSameTask(this);
    }
}
