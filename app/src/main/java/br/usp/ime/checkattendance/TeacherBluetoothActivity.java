package br.usp.ime.checkattendance;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class TeacherBluetoothActivity extends AppCompatActivity {

    private String seminarId;
    private String seminarName;
    private int counter = 0;

    private static final UUID appUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B1527");

    private Handler handler;
    private BluetoothAdapter adapter;
    private BluetoothDevice device;
    private BluetoothServerSocket socket;
    private final int MSG_TO_READ = 0;
    private final int MSG_TOAST = 8;

    private AcceptThread thread;

    private TextView tv_seminar_name;
    private TextView tv_bluetooth_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_bluetooth);

        this.setupActionBar();
        this.getSentData();
        this.checkBluetoothState();
        this.initializeUIComponents();
        this.runThread();
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle("Seminar authentication");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.thread.cancel();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.seminarId = intent.getStringExtra("id");
        this.seminarName = intent.getStringExtra("name");
    }

    private void initializeUIComponents() {
        this.tv_seminar_name = (TextView) findViewById(R.id.tv_seminar_name_bluetooth_teacher);
        this.tv_seminar_name.setText(this.seminarName + "\n\nYou are accepting connections to send the seminar's token for students via bluetooth");
        this.tv_bluetooth_name = (TextView) findViewById(R.id.tv_bluetooth_name);
        this.tv_bluetooth_name.setText("Attendees should connect with: " + this.adapter.getName());
    }

    private void checkBluetoothState() {
        this.adapter=BluetoothAdapter.getDefaultAdapter();

        if(this.adapter == null) {
            Toast.makeText(getBaseContext(), "Device does not support Bluetooth", Toast.LENGTH_SHORT).show();
            finish();
        } else if (! this.adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    private void runThread() {
        this.thread = new AcceptThread();
        this.thread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = adapter.listenUsingRfcommWithServiceRecord("checkattendance", appUUID);
            } catch (IOException e) {
                Log.e("SOCKET", "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            OutputStream mmOutStream = null;

            while (true) {
                try {
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e("SOCKET", "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    OutputStream tmpOut = null;

                    try {
                        tmpOut = socket.getOutputStream();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    mmOutStream = tmpOut;

                    if(mmOutStream != null) {
                        byte[] msgBuffer = seminarId.getBytes();
                        try {
                            mmOutStream.write(msgBuffer);
                            Log.d("WRITING..", msgBuffer.toString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        mmServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e("SOCKET", "Could not close the connect socket", e);
            }
        }
    }
}
