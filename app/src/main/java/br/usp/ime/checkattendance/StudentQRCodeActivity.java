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
    private NetworkController networkController;
    private String studentNusp;
    private String selectedSeminarId;

    private final int REFRESH = 0;
    private final int NOT_REFRESH = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_qrcode);

        this.setupActionBar();
        this.getSentData();
        this.networkController = new NetworkController();
        this.initScan();
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.selectedSeminarId = intent.getStringExtra(getString(R.string.seminar_id));
        this.studentNusp = intent.getStringExtra(getString(R.string.nusp));
    }

    private void initScan() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setPrompt(getString(R.string.qr_code_prompt_camera));
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.qr_code_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(StudentQRCodeActivity.this, message, duration).show();
    }

    private void closeActivity(int refresh) {
        if (refresh == REFRESH)
            setResult(REFRESH);
        else
            setResult(NOT_REFRESH);

        finish();
    }

    private void qrCodeScanCancelled() {
        this.showMessage(getString(R.string.cancel_qr_code_scan), Toast.LENGTH_LONG);
        this.closeActivity(NOT_REFRESH);
    }

    private void formatNotSupported() {
        this.showMessage(getString(R.string.qr_code_format_not_supported), Toast.LENGTH_LONG);
        this.closeActivity(NOT_REFRESH);
    }

    private void scannedSeminarIsDifferent() {
        this.showMessage(getString(R.string.different_seminar), Toast.LENGTH_LONG);
        this.closeActivity(NOT_REFRESH);
    }

    private boolean isQrCodeFormat(String format) {
        return format.equals(getString(R.string.qr_code_format));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() == null)
                this.qrCodeScanCancelled();
            else {
                if (this.isQrCodeFormat(result.getFormatName())) {
                    String scannedSeminarId = result.getContents();

                    if (this.selectedSeminarId.equals(scannedSeminarId))
                        this.confirmPresenceInSeminar(scannedSeminarId);
                    else
                        this.scannedSeminarIsDifferent();
                } else
                    this.formatNotSupported();
            }
        }
    }

    private void confirmPresenceInSeminar(String seminarId) {
        this.networkController.confirmAttendance(this.studentNusp, seminarId, this,
                new ServerCallback() {

            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    showMessage(getString(R.string.confirm_attendance), Toast.LENGTH_LONG);
                    closeActivity(REFRESH);
                } else {
                    showMessage(getString(R.string.problem_attendance_confirmation), Toast.LENGTH_LONG);
                    closeActivity(NOT_REFRESH);
                }
            }

            @Override
            public void onError() {
                showMessage(getString(R.string.network_issue), Toast.LENGTH_LONG);
                closeActivity(NOT_REFRESH);
            }
        });
    }
}
