package br.usp.ime.checkattendance;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class BluetoothDeviceListActivity extends AppCompatActivity {
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    private BluetoothAdapter mBtAdapter;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private ArrayAdapter<String> pairedDevicesArrayAdapter;
    private ListView pairedListView;
    private ListView newDevicesListView;
    private IntentFilter filter;

    private void setupActionBar() {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.bluetooth_title));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bluetooth_device_list);
        this.setupActionBar();

        this.setupScanButton();
        this.initializeAdapters();
        this.setupPairedDevicesListView();
        this.setupNewDevicesListView();
        this.registerFilters();
        this.checkBluetoothState();
    }

    private void setupScanButton() {
        Button scanButton = (Button) findViewById(R.id.button_scan_bluetooth);
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                doDiscovery();
                v.setVisibility(View.GONE);
            }
        });
    }

    private void registerFilters() {
        this.filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        this.filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
    }

    private void initializeAdapters() {
        this.pairedDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.bluetooth_device_name);
        this.mNewDevicesArrayAdapter =
                new ArrayAdapter<String>(this, R.layout.bluetooth_device_name);
    }

    private void setupPairedDevicesListView() {
        this.pairedListView = (ListView) findViewById(R.id.lv_paired_devices);
        this.pairedListView.setAdapter(pairedDevicesArrayAdapter);
        this.pairedListView.setOnItemClickListener(mDeviceClickListener);
    }

    private void setupNewDevicesListView() {
        this.newDevicesListView = (ListView) findViewById(R.id.lv_new_devices);
        this.newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        this.newDevicesListView.setOnItemClickListener(mDeviceClickListener);
    }

    private void setPairedDevices() {
        if (this.mBtAdapter != null) {
            Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
                for (BluetoothDevice device : pairedDevices)
                    this.pairedDevicesArrayAdapter.insert(device.getName() + "\n" +
                            device.getAddress(), 0);
            } else
                pairedDevicesArrayAdapter.add(getString(R.string.bluetooth_no_paired_devices));
        }
    }

    private void showMessage(String message, int duration) {
        Toast.makeText(BluetoothDeviceListActivity.this, message, duration).show();
    }

    private void checkBluetoothState() {
        this.mBtAdapter=BluetoothAdapter.getDefaultAdapter();

        if (this.mBtAdapter != null && this.mBtAdapter.isEnabled())
            this.setPairedDevices();
        else if(mBtAdapter==null) {
            showMessage(getString(R.string.bluetooth_not_supported), Toast.LENGTH_SHORT);
            finish();
        } else if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 1);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK)
            this.setPairedDevices();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtAdapter != null)
            mBtAdapter.cancelDiscovery();

        this.unregisterReceiver(mReceiver);
    }

    private void doDiscovery() {
        showMessage(getString(R.string.bluetooth_scanning), Toast.LENGTH_SHORT);

        if (mBtAdapter.isDiscovering())
            mBtAdapter.cancelDiscovery();

        mBtAdapter.startDiscovery();
    }

    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            mBtAdapter.cancelDiscovery();

            String address = getAddress(v);

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);

            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    private String getAddress(View v) {
        String info = ((TextView) v).getText().toString();
        return info.substring(info.length() - 17);
    }

    private void deviceFound(Intent intent) {
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        if (device.getBondState() != BluetoothDevice.BOND_BONDED)
            mNewDevicesArrayAdapter.insert(device.getName() + "\n" + device.getAddress(), 0);
    }

    private void finishDiscovery() {
        showMessage(getString(R.string.bluetooth_select_device), Toast.LENGTH_SHORT);
        if (mNewDevicesArrayAdapter.getCount() == 0)
            mNewDevicesArrayAdapter.insert(getString(R.string.bluetooth_no_device), 0);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action))
                deviceFound(intent);
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action))
                finishDiscovery();
        }
    };
}
