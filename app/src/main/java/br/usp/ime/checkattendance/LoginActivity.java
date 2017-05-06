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

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.RequestQueueSingleton;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class LoginActivity extends AppCompatActivity {

    private EditText mNuspEditText;
    private EditText mPasswdEditText;

    private String nusp;
    private String passwd;
    private boolean tryLoginAsTeacher;
    private NetworkController networkController;
    private ServerCallback serverCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.networkController = new NetworkController();
        initializeUIComponents();
        initializeCallback();
    }

    private void initializeUIComponents() {
        this.mNuspEditText = (EditText) findViewById(R.id.et_nusp);
        this.mPasswdEditText = (EditText) findViewById(R.id.et_passwd);
    }

    private void initializeCallback() {
        this.serverCallback = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    String type = (!tryLoginAsTeacher) ? "student" : "teacher";
                    String message = "You are logged in as " + type;
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                    if (type.equals("student")) {
                        Intent intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
                        intent.putExtra("nusp", nusp);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(LoginActivity.this, TeacherHomeActivity.class);
                        intent.putExtra("nusp", nusp);
                        startActivity(intent);
                    }
                } else {
                    if (!tryLoginAsTeacher) {
                        tryLoginAsTeacher = true;
                        loginTeacher();
                    } else {
                        String message = "Wrong credentials. Please, try again";
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

                        mNuspEditText.setText("");
                        mPasswdEditText.setText("");
                    }
                }
            }

            @Override
            public void onError() {
                String message = "We had some problem. Please, try again later";
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        };
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
        this.networkController.login(url, this.nusp,this.passwd, this, this.serverCallback);
    }

    private void loginTeacher() {
        String url = "http://207.38.82.139:8001/login/teacher";
        this.networkController.login(url, this.nusp,this.passwd, this, this.serverCallback);
    }
}
