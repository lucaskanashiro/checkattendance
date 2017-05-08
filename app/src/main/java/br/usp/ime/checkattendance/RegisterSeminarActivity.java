package br.usp.ime.checkattendance;

import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class RegisterSeminarActivity extends AppCompatActivity {

    private EditText mNameEditText;
    private String name;
    private NetworkController networkController;
    private ServerCallback serverCallback;

    private final static int REFRESH_PAGE = 1;
    private final static int NOT_REFRESH_PAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_seminar);

        this.setupActionBar();
        this.initializeUIComponents();
        this.networkController = new NetworkController();
        this.setupCallback();
    }

    private void initializeUIComponents() {
        this.mNameEditText = (EditText) findViewById(R.id.et_seminar_name);
    }

    private void getInput() {
        this.name = this.mNameEditText.getText().toString();
    }

    private void setupCallback() {
        this.serverCallback = new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                String message = "The seminar was registered successfully";
                Toast.makeText(RegisterSeminarActivity.this, message, Toast.LENGTH_LONG).show();
                setResult(REFRESH_PAGE);
                finish();
            }

            @Override
            public void onError() {
                String message = "We had some problem. Please, try again later";
                Toast.makeText(RegisterSeminarActivity.this, message, Toast.LENGTH_LONG).show();
                setResult(NOT_REFRESH_PAGE);
                finish();
            }
        };
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Register Seminar");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            setResult(NOT_REFRESH_PAGE);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void registerSeminar(View v) {
        this.getInput();

        if (this.name.matches("")) {
            String message = "You must provide the name of the seminar";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        } else {
            this.networkController.registerSeminar(this.name, this, this.serverCallback);
        }
    }
}
