package com.example.polygraph;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String name;
    TextView mBluetoothStatus;
    Button mScanBtn;
    Button mOffBtn;
    Button mListPairedDevicesBtn;
    Button mStartBtn;
    Button mShowBtn;
    Button mRecordBtn;
    Button mLogoutBtn;
    ListView mDevicesListView;
    ArrayList<Integer> data;

    // 블루투스 페어링 관련 변수들
    String TAG = "MainActivity";
    UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private final static int REQUEST_ENABLE_BT = 1;
    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter<String> btArrayAdapter;
    ArrayList<String> deviceAddressArray;
    BluetoothSocket btSocket = null;
    ConnectedThread connectedThread;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        name = intent.getStringExtra("Login");

        // 위치 권한을 추가
        String[] permission_list = {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        };
        ActivityCompat.requestPermissions(MainActivity.this, permission_list, 1);

        // 블루투스 활성화
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // 일반 변수 설정
        mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        mScanBtn = (Button) findViewById(R.id.scan);
        mOffBtn = (Button) findViewById(R.id.off);
        mListPairedDevicesBtn = (Button) findViewById(R.id.PairedBtn);
        mStartBtn = (Button) findViewById(R.id.start);
        mDevicesListView = (ListView) findViewById(R.id.devicesListView);
        mLogoutBtn = (Button) findViewById(R.id.logout);
        data = new ArrayList<>();

        // 블루투스 관련 변수 설정
        btArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        deviceAddressArray = new ArrayList<>();
        mDevicesListView.setAdapter(btArrayAdapter);
        mDevicesListView.setOnItemClickListener(new myOnItemClickListener());
    }

    // 블루투스 ON, OFF
    @SuppressLint("MissingPermission")
    public void onClickButtonScan(View view){
        if (!btAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("블루투스 ON");
            Toast.makeText(getApplicationContext(),"블루투스가 켜집니다.",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"블루투스가 이미 켜져있습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    public void onClickButtonOff(View view){
        btAdapter.disable(); // turn off
        mBluetoothStatus.setText("블루투스 OFF");
        Toast.makeText(getApplicationContext(),"블루투스가 꺼집니다.", Toast.LENGTH_SHORT).show();
    }

    // 페이렁한 기기들을 보여주는 메소드
   @SuppressLint("MissingPermission")
   public void onClickButtonPaired(View view){
        btArrayAdapter.clear();
        if(deviceAddressArray!=null && !deviceAddressArray.isEmpty()){ deviceAddressArray.clear(); }
        pairedDevices = btAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                btArrayAdapter.add(deviceName);
                deviceAddressArray.add(deviceHardwareAddress);
            }
        }
    }

    // Start누르면 측정 시작
    public void onClickButtonStart(View view){
        connectedThread.write("1");
        Toast.makeText(getApplicationContext(),"측정을 시작합니다.", Toast.LENGTH_SHORT).show();
    }

    // 결과보기 누르면 측정 멈추고 결과를 보여준다.
    public void onClickButtonShow(View view){
        Toast.makeText(getApplicationContext(),"측정을 종료합니다.", Toast.LENGTH_SHORT).show();
        //connectedThread.write("0");
        //data = connectedThread.getResult();

        // 아두이노 없을 때 테스트용 코드
        data.add(28576);
        data.add(33333);
        data.add(54373);
        data.add(104112);
        Intent intent = new Intent(getApplicationContext(), ShowResult.class);
        intent.putExtra("Data", data);
        intent.putExtra("Username", name);

        startActivity(intent);
    }

    // 기록 확인 버튼
    public void onClickButtonRecord(View view){
        Intent intent = new Intent(getApplicationContext(), ShowRecord.class);
        intent.putExtra("Username", name);
        startActivity(intent);
    }

    // 로그아웃
    public void onClickButtonOut(View v) {
        Toast.makeText(getApplicationContext(),"로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    // 블루투스 기기와의 통신을 위한 클래스
    public class myOnItemClickListener implements AdapterView.OnItemClickListener {

        @SuppressLint("MissingPermission")
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), btArrayAdapter.getItem(position), Toast.LENGTH_SHORT).show();

            mBluetoothStatus.setText("연결중...");

            final String nameBT = btArrayAdapter.getItem(position); // get name
            final String address = deviceAddressArray.get(position); // get address
            boolean flag = true;

            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            // create & connect socket
            try {
                btSocket = createBluetoothSocket(device);
                btSocket.connect();
            } catch (IOException e) {
                flag = false;
                mBluetoothStatus.setText("연결 실패");
                e.printStackTrace();
            }

            if(flag){
                mBluetoothStatus.setText("연결기기 : "+nameBT);
                connectedThread = new ConnectedThread(btSocket);
                connectedThread.start();
            }

        }
    }

    @SuppressLint("MissingPermission")
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection",e);
        }
        return  device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
    }
}