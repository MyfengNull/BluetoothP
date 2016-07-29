package com.ysr.mybl;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.brother.ptouch.sdk.Printer;
import com.brother.ptouch.sdk.PrinterInfo;
import com.brother.ptouch.sdk.PrinterStatus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends Activity implements View.OnClickListener {

    private Button btN,btN2;
    private BluetoothAdapter mBluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        btN = (Button) findViewById(R.id.btN);
        btN2 = (Button) findViewById(R.id.btN2);
        btN.setOnClickListener(this);
        btN2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btN:
//                if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
//                    //打开蓝牙
//                   Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                   startActivity(enableIntent);
                // set the adapter when printing by way of Bluetooth
//                }
                getBluetoothAdapter();

                break;
            case R.id.btN2:
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    /**
     * 开启蓝牙
     * get the BluetoothAdapter
     */
    protected BluetoothAdapter getBluetoothAdapter() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            final Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        }
        return bluetoothAdapter;
    }






}
