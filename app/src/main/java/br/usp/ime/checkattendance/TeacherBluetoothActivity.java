package br.usp.ime.checkattendance;

import android.app.Activity;
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

    private final String TAG = "TeacherBluetooth";
    private final UUID appUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B1527");

    private BluetoothAdapter adapter;
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
    }

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();

        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.seminar_auth));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void closeActivity() {
        if (this.thread != null) this.thread.cancel();
        finish();
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(TeacherBluetoothActivity.this, message, duration).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            this.closeActivity();

        return super.onOptionsItemSelected(item);
    }

    private void getSentData() {
        Intent intent = getIntent();
        this.seminarId = intent.getStringExtra(getString(R.string.seminar_id));
        this.seminarName = intent.getStringExtra(getString(R.string.seminar_name));
    }

    private void initializeUIComponents() {
        this.tv_seminar_name = (TextView) findViewById(R.id.tv_seminar_name_bluetooth_teacher);
        this.tv_seminar_name.setText(this.seminarName +
                getString(R.string.bluetooth_teacher_accepting_connections));
        this.tv_bluetooth_name = (TextView) findViewById(R.id.tv_bluetooth_name);
        this.tv_bluetooth_name.setText(getString(R.string.bluetooth_teacher_text_to_present_device) +
                this.adapter.getName());
    }

    private void checkBluetoothState() {
        this.adapter=BluetoothAdapter.getDefaultAdapter();

        if (this.adapter != null && this.adapter.isEnabled())
            this.startThread();
        else if(this.adapter == null) {
            this.showMessage(getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT);
            this.closeActivity();
        } else if (! this.adapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            this.startThread();
    }

    private void startThread() {
        this.thread = new AcceptThread();
        this.thread.start();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket serverSocket;
        private OutputStream outputStream;
        private BluetoothSocket socket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = adapter.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), appUUID);
            } catch (IOException e) {
                Log.e(TAG, getString(R.string.cannot_get_socket), e);
            }
            this.serverSocket = tmp;
        }

        public void run() {
            while (true) {
                boolean success = this.acceptConnections();
                if (!success) break;

                if (this.socket != null) {
                    this.getOutputStream();
                    this.writeData();
                    this.cancel();

                    break;
                }
            }
        }

        private boolean acceptConnections() {
            try {
                this.socket = this.serverSocket.accept();
                return true;
            } catch (IOException e) {
                Log.e(TAG, getString(R.string.cannot_accept_socket_connection), e);
                return false;
            }
        }

        private void getOutputStream() {
            OutputStream tmpOut = null;

            try {
                tmpOut = this.socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, getString(R.string.cannot_get_outputstream_socket), e);
            }

            this.outputStream = tmpOut;
        }

        private void writeData() {
            if (this.outputStream != null) {
                byte[] msgBuffer = seminarId.getBytes();
                try {
                    this.outputStream.write(msgBuffer);
                    Log.d(TAG, getString(R.string.writing) + msgBuffer.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG, getString(R.string.cannot_write_buffer_socket), e);
                }
            }
        }

        public void cancel() {
            try {
                this.serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, getString(R.string.cannot_close_socket), e);
            }
        }
    }
}
