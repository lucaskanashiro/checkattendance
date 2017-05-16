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

    private void showMessage(String message, int duration) {
        Toast.makeText(LoginActivity.this, message, duration).show();
    }

    private String getType() {
        return (!tryLoginAsTeacher) ? getString(R.string.student) : getString(R.string.teacher);
    }

    private void callNextActivity(String type) {
        Intent intent;

        if (type.equals(getString(R.string.student)))
            intent = new Intent(LoginActivity.this, StudentHomeActivity.class);
        else
            intent = new Intent(LoginActivity.this, TeacherHomeActivity.class);

        intent.putExtra(getString(R.string.nusp), nusp);
        startActivity(intent);
    }

    private void initializeCallback() {
        this.serverCallback = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    String type = getType();

                    showMessage(getString(R.string.successfull_login) + " " + type,
                            Toast.LENGTH_LONG);

                    callNextActivity(type);
                    cleanFields();
                } else {
                    if (!tryLoginAsTeacher) {
                        tryLoginAsTeacher = true;
                        loginTeacher();
                    } else {
                        showMessage(getString(R.string.login_error), Toast.LENGTH_LONG);
                        cleanFields();
                    }
                }
            }

            @Override
            public void onError() {
                showMessage(getString(R.string.network_issue), Toast.LENGTH_LONG);
            }
        };
    }

    private void cleanFields() {
        this.mNuspEditText.setText("");
        this.mPasswdEditText.setText("");
    }

    private void getInputs() {
        this.nusp = this.mNuspEditText.getText().toString();
        this.passwd = this.mPasswdEditText.getText().toString();
    }

    public void register(View v) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        intent.putExtra(getString(R.string.type), getString(R.string.student));
        startActivity(intent);
    }

    public void login(View v) {
        getInputs();
        this.tryLoginAsTeacher = false;
        loginStudent();
    }

    private void loginStudent() {
        this.networkController.login(getString(R.string.student), this.nusp,this.passwd, this,
                this.serverCallback);
    }

    private void loginTeacher() {
        this.networkController.login(getString(R.string.teacher), this.nusp,this.passwd, this,
                this.serverCallback);
    }
}
