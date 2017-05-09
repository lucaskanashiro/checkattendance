package br.usp.ime.checkattendance;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.Parser;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class UpdateSeminarActivity extends AppCompatActivity {

    private EditText mSeminarNameEditText;
    private String seminarId;
    private String seminarCurrentName;
    private NetworkController networkController;
    private ServerCallback serverCallback;
    private ServerCallback serverCallbackUpdateData;

    private final static int REFRESH_PAGE = 1;
    private final static int NOT_REFRESH_PAGE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_seminar);

        this.setupActionBar();
        this.getSentData();
        this.initializeUIComponents();
        this.networkController = new NetworkController();
        this.initializeCallback();
        this.fillWithCurrentData();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Update Seminar");
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

    private void initializeUIComponents() {
        this.mSeminarNameEditText = (EditText) findViewById(R.id.et_seminar_name_update);
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.seminarId = intent.getStringExtra("id");
    }

    private void initializeCallback() {
        this.serverCallback = new ServerCallback() {
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
                if (response.contains("\"success\":true")) {
                    String message = "Seminar updated!";
                    Toast.makeText(UpdateSeminarActivity.this, message, Toast.LENGTH_LONG).show();
                    setResult(REFRESH_PAGE);
                    UpdateSeminarActivity.this.finish();
                } else {
                    String message = "Seminar was not updated. Try again later";
                    Toast.makeText(UpdateSeminarActivity.this, message, Toast.LENGTH_LONG).show();
                    setResult(NOT_REFRESH_PAGE);
                    UpdateSeminarActivity.this.finish();
                }
            }

            @Override
            public void onError() {
                String message = "Seminar was not updated. Try again later";
                Toast.makeText(UpdateSeminarActivity.this, message, Toast.LENGTH_LONG).show();
                setResult(NOT_REFRESH_PAGE);
                UpdateSeminarActivity.this.finish();
            }
        };
    }

    private void fillWithCurrentData() {
        this.networkController.getSeminar(this.seminarId, this, this.serverCallback);
    }

    private void setCurrentDataInForm(String response) {
        try {
            this.seminarCurrentName = Parser.parseData(response, "name");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        this.mSeminarNameEditText.setText(this.seminarCurrentName, TextView.BufferType.EDITABLE);
    }

    private String getInput() {
        return this.mSeminarNameEditText.getText().toString();
    }

    public void updateSeminar(View view) {
        String name = this.getInput();
        this.networkController.updateSeminar(this.seminarId, name, this, this.serverCallbackUpdateData);
    }
}
