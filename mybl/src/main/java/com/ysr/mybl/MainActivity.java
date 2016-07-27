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
        init();
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
                print();
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

    public void print() {
        Thread trd = new Thread(new Runnable() {
            @Override
            public void run() {
                String externalStorageDir = Environment.getExternalStorageDirectory().toString();
                // define printer and printer setting information
                Printer printer = new Printer();
                PrinterInfo printInfo = new PrinterInfo();
                printInfo.printerModel = PrinterInfo.Model.RJ_3150;
                printInfo.port = PrinterInfo.Port.BLUETOOTH;
                printInfo.customPaper = externalStorageDir + "/rj3150_76mm.bin";
                printInfo.macAddress = "00:11:EE:BB:AA:CC";
                printer.setPrinterInfo(printInfo);
                // Pass Bluetooth adapter to the library (Bluetooth only)
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                printer.setBluetooth(bluetoothAdapter);
                //print
                String srcPath = externalStorageDir + "/sample.png";
                Log.e("srcPath", srcPath);
                PrinterStatus status = printer.printFile(srcPath);
            }
        });
        trd.start();
    }

    /**
     * copy from raw in resource
     */
    private void raw2file(String fileName, int fileID) throws Exception {
        File newdir = new File(Common.CUSTOM_PAPER_FOLDER);
        if (!newdir.exists()) {
            newdir.mkdir();
        }
        File dstFile = new File(Common.CUSTOM_PAPER_FOLDER + fileName);
        if (!dstFile.exists()) {
            try {
                InputStream input = null;
                OutputStream output = null;
                input = this.getResources().openRawResource(fileID);
                output = new FileOutputStream(dstFile);
                int DEFAULT_BUFFER_SIZE = 1024 * 4;
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                int n = 0;
                while (-1 != (n = input.read(buffer))) {
                    output.write(buffer, 0, n);
                }
                input.close();
                output.close();
            } catch (FileNotFoundException e1) {
            } catch (IOException e) {
            }
        }
    }

    private void init() {
         /*
         * copy .bin file (RJ paper size info.) to
		 * /mnt/sdcard/customPaperFileSet/ .bin file is made by Printer Setting
		 * Tool which can be downloaded from the Brother Net Site
		 */
        try {
            raw2file("RJ4030_102mm.bin", R.raw.rj4030_102mm);
            raw2file("RJ4030_102mm152mm.bin", R.raw.rj4030_102mm152mm);
            raw2file("RJ4040_102mm.bin", R.raw.rj4040_102mm);
            raw2file("RJ4040_102mm152mm.bin", R.raw.rj4040_102mm152mm);
            raw2file("RJ4030Ai_102mm.bin", R.raw.rj4030ai_102mm);
            raw2file("RJ4030Ai_102mm152mm.bin", R.raw.rj4030ai_102mm152mm);
            raw2file("RJ3050_76mm.bin", R.raw.rj3050_76mm);
            raw2file("RJ3150_76mm.bin", R.raw.rj3150_76mm);
            raw2file("RJ3150_76mm44mm.bin", R.raw.rj3150_76mm44mm);
            raw2file("TD2020_57mm.bin", R.raw.td2020_57mm);
            raw2file("TD2020_40mm40mm.bin", R.raw.td2020_40mm40mm);
            raw2file("TD2120_57mm.bin", R.raw.td2120_57mm);
            raw2file("TD2120_40mm40mm.bin", R.raw.td2120_40mm40mm);
            raw2file("TD2130_57mm.bin", R.raw.td2130_57mm);
            raw2file("TD2130_40mm40mm.bin", R.raw.td2130_40mm40mm);
            raw2file("TD4100N_102mm.bin", R.raw.td4100n_102mm);
            raw2file("TD4100N_102mm152mm.bin", R.raw.td4100n_102mmx152mm);
            raw2file("TD4000_102mm.bin", R.raw.td4000_102mm);
            raw2file("TD4000_102mm152mm.bin", R.raw.td4000_102mmx152mm);
        } catch (Exception e) {
        }
    }
}
