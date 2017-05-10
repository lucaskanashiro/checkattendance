package br.usp.ime.checkattendance;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.StringBufferInputStream;

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.ServerCallback;

import static android.R.attr.onClick;
import static android.R.attr.type;

public class StudentQRCodeActivity extends AppCompatActivity {

    private TextView tvScanFormat, tvScanContent;
    private LinearLayout llSearch;
    private NetworkController networkController;
    private String studentNusp;
    private String selectedSeminarId;
    private String selectedSeminarName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qrcode);

        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Scan QR code");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        this.getSentData();
        this.networkController = new NetworkController();

        tvScanFormat = (TextView) findViewById(R.id.tvScanFormat);
        tvScanContent = (TextView) findViewById(R.id.tvScanContent);
        llSearch = (LinearLayout) findViewById(R.id.llSearch);

        llSearch.setVisibility(View.GONE);
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt("Scan the seminar QR code");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.selectedSeminarId = intent.getStringExtra("id");
        this.studentNusp = intent.getStringExtra("nusp");
        this.selectedSeminarName = intent.getStringExtra("name");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                llSearch.setVisibility(View.GONE);
                Toast.makeText(this, "Cancelled QR code scan", Toast.LENGTH_LONG).show();
                finish();
            } else {
                if (result.getFormatName().equals("QR_CODE")) {
                    String scannedSeminarId = result.getContents();

                    if (this.selectedSeminarId.equals(scannedSeminarId)) {

                        this.confirmPresenceInSeminar(scannedSeminarId);
                    } else {
                        String msg = "The scanned seminar isn't the same that you selected. Try again";
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        finish();
                    }
                } else {
                    llSearch.setVisibility(View.GONE);
                    Toast.makeText(this, "Format not supported. Try again.", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void confirmPresenceInSeminar(String seminarId) {
        this.networkController.confirmAttendance(this.studentNusp, seminarId, this,
                new ServerCallback() {

            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    llSearch.setVisibility(View.VISIBLE);
                    tvScanContent.setText("QR_CODE");
                    tvScanFormat.setText(selectedSeminarName + " -> " + selectedSeminarId);
                } else {
                    String message = "We had some problem during your attendance confirmation. Please, try again later";
                    Toast.makeText(StudentQRCodeActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onError() {
                String message = "We had some network problem. Please, try again later";
                Toast.makeText(StudentQRCodeActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }
}
