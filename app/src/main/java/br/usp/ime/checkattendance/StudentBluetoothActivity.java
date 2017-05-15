package br.usp.ime.checkattendance;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class StudentBluetoothActivity extends AppCompatActivity {

    private static final UUID appUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B1527");

    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private ConnectedThread thread;
    private Handler handler;
    private final int MSG_TO_READ = 0;
    private final int MSG_TOAST = 8;

    private String seminarId;
    private String seminarName;
    private String mac_address_to_connect;
    private String seminarIdRead;

    private TextView tv_seminar_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_bluetooth);

        this.getSentData();
        this.initializeUIComponents();
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.seminarId = intent.getStringExtra("id");
        this.seminarName = intent.getStringExtra("name");
    }

    private void initializeUIComponents() {
        this.tv_seminar_name = (TextView) findViewById(R.id.tv_seminar_name_bluetooth);
        this.tv_seminar_name.setText(this.seminarName);
    }

    public void scanDevices(View v) {
        Intent intent = new Intent(this, BluetoothDeviceListActivity.class);
        intent.putExtra("id", this.seminarId);
        intent.putExtra("name", this.seminarName);
        startActivityForResult(intent, 0);
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
                    Log.d("SEMINAR ID READED", seminarIdRead);
                }
                if (msg.what == MSG_TOAST) {
                    String message = (String) msg.obj;
                    Toast.makeText(StudentBluetoothActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void startThread() {

        this.setupHandler();
        this.adapter = BluetoothAdapter.getDefaultAdapter();

        if (this.adapter == null) {
            Toast.makeText(this, "Bluetooth not available", Toast.LENGTH_SHORT).show();
            finish();
        }

        this.device = this.adapter.getRemoteDevice(this.mac_address_to_connect);

        try {
            this.socket = this.device.createRfcommSocketToServiceRecord(this.appUUID);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Socket creation failed", Toast.LENGTH_SHORT).show();
        }

        try {
            this.socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Cannot connect via socket", Toast.LENGTH_SHORT).show();
            try {
                this.socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        this.thread = new ConnectedThread(this.socket, this.handler);
        this.thread.start();
    }

    public class ConnectedThread extends Thread {

        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private Handler handler;

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

        public void write(String input) {
            byte[] msgBuffer = input.getBytes();

            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Message msg = this.handler.obtainMessage(MSG_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast", "Unable to connect device");
                msg.setData(bundle);
                this.handler.sendMessage(msg);
            }
        }
    }
}
