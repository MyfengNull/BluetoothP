package com.ysr.mybl;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
2.0蓝牙
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSearch;
    private LinearLayout linearLayoutdevices;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;
    private ProgressBar pb;
    static SearchActivity mActivity;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 00011;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mActivity = this;
        linearLayoutdevices = (LinearLayout) findViewById(R.id.lineLayoutdevices);
        pb = (ProgressBar) findViewById(R.id.pb);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        initBroadcast();
 
    }


    private void initBroadcast() {
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    Toast.makeText(mActivity, "ACTION_FOUND...", Toast.LENGTH_SHORT).show();
                    if (device == null) {
                        return;
                    }

                    final String address = device.getAddress();
                    String name = device.getName();

                    if (name == null)
                        name = "BT";
                    else if (name.equals(address)) {

                        name = "BT";
                    }
                    Button button = new Button(context);
                    button.setText(name + ":" + address);
                    Log.e("", name + ":" + address);
                    for (int i = 0; i < linearLayoutdevices.getChildCount(); i++) {
                        Button btn = (Button) linearLayoutdevices.getChildAt(i);
                        if (btn.getText().equals(button.getText())) {
                            return;
                        }
                    }
                    button.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            for (int i = 0; i < linearLayoutdevices.getChildCount(); ++i) {
                                Button btn = (Button) linearLayoutdevices.getChildAt(i);
                                btn.setEnabled(false);
                            }

                        }
                    });
                    button.getBackground().setAlpha(100);
                    linearLayoutdevices.addView(button);
                } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED
                        .equals(action)) {
                    pb.setIndeterminate(true);
                    Toast.makeText(mActivity, "ACTION_DISCOVERY_STARTED...", Toast.LENGTH_SHORT).show();
                } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED
                        .equals(action)) {
                    Toast.makeText(mActivity, "ACTION_DISCOVERY_FINISHED...", Toast.LENGTH_SHORT).show();
                    pb.setIndeterminate(false);
                }
            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSearch:
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                if (null == bluetoothAdapter) {
                    finish();
                    break;
                }
                if (!bluetoothAdapter.isEnabled()) {
                    if (bluetoothAdapter.enable()) {
                        while (!bluetoothAdapter.isEnabled()) ;
                    } else {
                        finish();
                        break;
                    }
                }
                //取消搜索
                bluetoothAdapter.cancelDiscovery();
                linearLayoutdevices.removeAllViews();
                bluetoothAdapter.startDiscovery();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uninitBroadcast();
    }

    private void uninitBroadcast() {
        if (broadcastReceiver != null)
            unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
