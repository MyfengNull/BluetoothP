package com.ysr.mybl;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.lvrenyang.io.BTPrinting;
import com.lvrenyang.io.IOCallBack;
import com.lvrenyang.io.Pos;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
2.0蓝牙
 */
public class SearchActivity extends AppCompatActivity implements View.OnClickListener, IOCallBack {

    private Button btnSearch;
    private LinearLayout linearLayoutdevices;
    private IntentFilter intentFilter;
    private BroadcastReceiver broadcastReceiver;
    private ProgressBar pb;
    static SearchActivity mActivity;
    private final int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 00011;
    //蓝牙匹配
    private ExecutorService es = Executors.newScheduledThreadPool(30);
    Pos mPos = new Pos();
    BTPrinting mBt = new BTPrinting();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mActivity = this;
        linearLayoutdevices = (LinearLayout) findViewById(R.id.lineLayoutdevices);
        pb = (ProgressBar) findViewById(R.id.pb);

        btnSearch = (Button) findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);

        mPos.Set(mBt);
        mBt.SetCallBack(this);
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
                                //搜索按钮禁用
                                btnSearch.setEnabled(false);
                                Button btn = (Button) linearLayoutdevices.getChildAt(i);
                                btn.setEnabled(false);
                            }
                            es.submit(new TaskOpen(mBt, address));
                        }
                    });
                    button.getBackground().setAlpha(100);
                    //显示
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

    //回调
    @Override
    public void OnOpen() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < linearLayoutdevices.getChildCount(); i++) {
                    Button btn = (Button) linearLayoutdevices.getChildAt(i);
                    btn.setEnabled(false);
                }
                Toast.makeText(mActivity, "Connected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnOpenFailed() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayoutdevices.setEnabled(true);
                for (int i = 0; i < linearLayoutdevices.getChildCount(); i++) {
                    Button btn = (Button) linearLayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
                Toast.makeText(mActivity, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void OnClose() {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                linearLayoutdevices.setEnabled(true);
                for (int i = 0; i < linearLayoutdevices.getChildCount(); i++) {
                    Button btn = (Button) linearLayoutdevices.getChildAt(i);
                    btn.setEnabled(true);
                }
                Toast.makeText(mActivity, "Close", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public class TaskOpen implements Runnable {
        BTPrinting bt = null;
        String address = null;

        public TaskOpen(BTPrinting bt, String address) {
            this.bt = bt;
            this.address = address;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            bt.Open(address);
        }
    }

}
