package br.usp.ime.checkattendance;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import br.usp.ime.checkattendance.utils.NetworkController;
import br.usp.ime.checkattendance.utils.ServerCallback;

public class StudentBluetoothActivity extends AppCompatActivity {
    private final String TAG = "StudentBluetooth";
    private static final UUID appUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B1527");

    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private ConnectedThread thread;
    private Handler handler;

    private final int MSG_TO_READ = 0;
    private final int REFRESH = 0;
    private final int NOT_REFRESH = 1;

    private String seminarId;
    private String seminarName;
    private String nusp;
    private String mac_address_to_connect;
    private String seminarIdRead;
    private NetworkController networkController;

    private TextView tv_seminar_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bluetooth);

        this.setupActionBar();
        this.networkController = new NetworkController();
        this.getSentData();
        this.initializeUIComponents();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.seminar_auth));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.thread.cancel();
            setResult(NOT_REFRESH);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.seminarId = intent.getStringExtra(getString(R.string.seminar_id));
        this.seminarName = intent.getStringExtra(getString(R.string.seminar_name));
        this.nusp = intent.getStringExtra(getString(R.string.nusp));
    }

    private void initializeUIComponents() {
        this.tv_seminar_name = (TextView) findViewById(R.id.tv_seminar_name_bluetooth);
        this.tv_seminar_name.setText(this.seminarName);
    }

    public void scanDevices(View v) {
        Intent intent = new Intent(this, BluetoothDeviceListActivity.class);
        intent.putExtra(getString(R.string.seminar_id), this.seminarId);
        intent.putExtra(getString(R.string.seminar_name), this.seminarName);
        startActivityForResult(intent, 0);
    }

    private void confirmPresenceInSeminar(String seminarId) {
        this.networkController.confirmAttendance(this.nusp, seminarId, this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    Toast.makeText(StudentBluetoothActivity.this,
                            getString(R.string.confirm_attendance),
                            Toast.LENGTH_LONG).show();
                    setResult(REFRESH);
                    finish();
                } else {
                    Toast.makeText(StudentBluetoothActivity.this,
                            getString(R.string.problem_attendance_confirmation),
                            Toast.LENGTH_LONG).show();
                    setResult(NOT_REFRESH);
                    finish();
                }
            }

            @Override
            public void onError() {
                Toast.makeText(StudentBluetoothActivity.this,
                        getString(R.string.network_issue),
                        Toast.LENGTH_LONG).show();
                setResult(NOT_REFRESH);
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            this.mac_address_to_connect = data.getStringExtra(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS);
            this.startThread();
        }
    }

    private void setupHandler() {
        this.handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == MSG_TO_READ) {
                    seminarIdRead = (String) msg.obj;
                    Log.d(TAG, getString(R.string.seminar_id_readed_log) + seminarIdRead);
                    Log.d(TAG, getString(R.string.seminar_id_received_log) + seminarId);
                    if (seminarIdRead.equals(seminarId))
                        confirmPresenceInSeminar(seminarId);
                    else {
                        Toast.makeText(StudentBluetoothActivity.this,
                                getString(R.string.different_seminar),
                                Toast.LENGTH_LONG).show();
                        setResult(NOT_REFRESH);
                        finish();
                    }
                }
            }
        };
    }

    private void startThread() {
        this.setupHandler();
        this.adapter = BluetoothAdapter.getDefaultAdapter();

        if (this.adapter == null) {
            Toast.makeText(StudentBluetoothActivity.this,
                    getString(R.string.bluetooth_not_supported),
                    Toast.LENGTH_SHORT).show();
            setResult(NOT_REFRESH);
            finish();
        }

        this.device = this.adapter.getRemoteDevice(this.mac_address_to_connect);

        try {
            this.socket = this.device.createRfcommSocketToServiceRecord(this.appUUID);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(StudentBluetoothActivity.this,
                    getString(R.string.cannot_get_socket),
                    Toast.LENGTH_SHORT).show();
        }

        try {
            this.socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(StudentBluetoothActivity.this,
                    getString(R.string.cannot_connect_socket),
                    Toast.LENGTH_SHORT).show();
            try {
                this.socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            } finally {
                this.socket = null;
            }
        }

        this.thread = new ConnectedThread(this.socket, this.handler);
        this.thread.start();
    }

    public class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private Handler handler;
        private BluetoothSocket socket;

        public ConnectedThread(BluetoothSocket socket, Handler handler) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
            this.handler = handler;
            this.socket = socket;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    this.handler.obtainMessage(MSG_TO_READ, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }

        public void cancel() {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, getString(R.string.cannot_close_socket));
            }
        }
    }
}
