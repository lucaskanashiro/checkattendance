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
        if (item.getItemId() == android.R.id.home)
            this.closeActivity(NOT_REFRESH);

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

    private void closeActivity(int refresh) {
        if(refresh == REFRESH)
            setResult(REFRESH);
        else
            setResult(NOT_REFRESH);

        if (this.thread != null) this.thread.cancel();
        finish();
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(StudentBluetoothActivity.this, message,duration).show();
    }

    private void confirmPresenceInSeminar(String seminarId) {
        this.networkController.confirmAttendance(this.nusp, seminarId, this, new ServerCallback() {
            @Override
            public void onSuccess(String response) {
                if (response.contains("\"success\":true")) {
                    showMessage(getString(R.string.confirm_attendance), Toast.LENGTH_LONG);
                    closeActivity(REFRESH);
                } else {
                    showMessage(getString(R.string.problem_attendance_confirmation),
                            Toast.LENGTH_LONG);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            this.mac_address_to_connect = data.getStringExtra(BluetoothDeviceListActivity.EXTRA_DEVICE_ADDRESS);
            this.setupThread();
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
                        showMessage(getString(R.string.different_seminar), Toast.LENGTH_LONG);
                        closeActivity(NOT_REFRESH);
                    }
                }
            }
        };
    }

    private void setupThread() {
        this.setupHandler();
        this.getBluetoothAdapter();
        this.getDevice();
        this.startThread();
    }

    private void startThread() {
        this.thread = new ConnectedThread(this.handler);
        this.thread.start();
    }

    private void getBluetoothAdapter() {
        this.adapter = BluetoothAdapter.getDefaultAdapter();

        if (this.adapter == null) {
            showMessage(getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT);
            closeActivity(NOT_REFRESH);
        }
    }

    private void getDevice() {
        this.device = this.adapter.getRemoteDevice(this.mac_address_to_connect);
    }

    public class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private Handler handler;
        private BluetoothSocket socket;

        public ConnectedThread(Handler handler) {
            this.handler = handler;
            this.getSocket();
            this.connectSocket();
            this.setInputStream();
        }

        private void getSocket() {
            try {
                this.socket = device.createRfcommSocketToServiceRecord(appUUID);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, getString(R.string.cannot_get_socket));
                showMessage(getString(R.string.cannot_get_socket), Toast.LENGTH_SHORT);
            }
        }

        public void cancel() {
            try {
                this.socket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, getString(R.string.cannot_close_socket));
            } finally {
                this.socket = null;
            }
        }

        private void connectSocket() {
            try {
                this.socket.connect();
            } catch (IOException e) {
                e.printStackTrace();
                showMessage(getString(R.string.cannot_connect_socket), Toast.LENGTH_SHORT);
                Log.d(TAG, getString(R.string.cannot_connect_socket));
                this.cancel();
            }
        }

        private void setInputStream() {
            InputStream tmpIn = null;

            try {
                tmpIn = this.socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, getString(R.string.cannot_get_inputstream_socket));
            }

            this.mmInStream = tmpIn;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer, 0, bytes);
                    Log.d(TAG, getString(R.string.reading) + readMessage);
                    this.handler.obtainMessage(MSG_TO_READ, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
    }
}
