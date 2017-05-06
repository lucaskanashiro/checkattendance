package br.usp.ime.checkattendance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import br.usp.ime.checkattendance.utils.RequestQueueSingleton;

public class LoginActivity extends AppCompatActivity {

    private EditText mNuspEditText;
    private EditText mPasswdEditText;

    private String nusp;
    private String passwd;
    private boolean tryLoginAsTeacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUIComponents();
    }

    private void initializeUIComponents() {
        this.mNuspEditText = (EditText) findViewById(R.id.et_nusp);
        this.mPasswdEditText = (EditText) findViewById(R.id.et_passwd);
    }

    private void getInputs() {
        this.nusp = this.mNuspEditText.getText().toString();
        this.passwd = this.mPasswdEditText.getText().toString();
    }

    public void register(View v) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void login(View v) {
        getInputs();
        this.tryLoginAsTeacher = false;
        loginStudent();
    }

    private void loginStudent() {
        String url = "http://207.38.82.139:8001/login/student";
        this.login(url, this.nusp, this.passwd);
    }

    private void loginTeacher() {
        String url = "http://207.38.82.139:8001/login/teacher";
        this.login(url, this.nusp, this.passwd);
    }

    private void login(String url, final String nusp, final String passwd) {
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("LOGIN", response +  "\n");
                        if (response.contains("\"success\":true")) {
                            LoginActivity.this.successedLogin();
                        } else {
                            LoginActivity.this.failureLogin();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                LoginActivity.this.failureRequest();
            }
        }
        ) {
            String body = "nusp=" + nusp + "&pass=" + passwd;
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

    private void successedLogin() {
        String type = (!this.tryLoginAsTeacher) ? "student" : "teacher";
        String message = "You are logged in as " + type;
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    private void failureLogin() {
        if (!this.tryLoginAsTeacher) {
            this.tryLoginAsTeacher = true;
            loginTeacher();
        } else {
            String message = "Wrong credentials. Please, try again";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();

            this.mNuspEditText.setText("");
            this.mPasswdEditText.setText("");
        }
    }

    private void failureRequest() {
        String message = "We had some problem. Please, try again later";
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
