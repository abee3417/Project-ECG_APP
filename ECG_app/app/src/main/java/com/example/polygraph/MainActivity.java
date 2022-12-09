package com.example.polygraph;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.graphics.Color;
import android.os.SystemClock;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.time.temporal.TemporalQueries;
import java.util.ArrayList;
import java.util.Queue;
import java.util.LinkedList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    String name;
    TextView mBluetoothStatus;
    TextView mShowBPM;
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

    private Queue<Integer> timeList;
    private Queue<Integer> dataList;
    private ArrayList<Integer> resultList;
    private ArrayList<Integer> returnList;
    private int last_time;
    private int returnBPM[] = new int[2];
    private int cntBPM = 0;
    private int avgSUM = 0;
    private TextView showBPM;
    private double avgBPM = 0;

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
        mShowBPM = (TextView) findViewById(R.id.time_bpm);
        mShowBPM.setText("평균값 측정중...");
        //connectedThread.setText(mShowBPM);
        connectedThread.write("1");
        Toast.makeText(getApplicationContext(),"측정을 시작합니다.", Toast.LENGTH_SHORT).show();
    }

    // 결과보기 누르면 측정 멈추고 결과를 보여준다.
    public void onClickButtonShow(View view){
        mShowBPM.setText("");
        mShowBPM.setTextColor(Color.parseColor("#000000"));
        Toast.makeText(getApplicationContext(),"측정을 종료합니다.", Toast.LENGTH_SHORT).show();
        connectedThread.write("0");
        mShowBPM.setText("측정을 시작해주세요");
        data = connectedThread.getResult();

        // 아두이노 없을 때 테스트용 코드
        /*
        data.add(28576);
        data.add(33333);
        data.add(54373);
        data.add(104112);

         */
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
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
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

    // Thread 부분 시작
    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private Queue<Integer> timeList;
        private Queue<Integer> dataList;
        private ArrayList<Integer> resultList;
        private ArrayList<Integer> returnList;
        private int last_time;
        private int returnBPM[] = new int[2];
        private int cntBPM = 0;
        private int avgSUM = 0;
        private TextView showBPM;
        private double avgBPM = 0;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            timeList = new LinkedList<>();
            dataList = new LinkedList<>();
            resultList = new ArrayList<>();
            returnList = new ArrayList<>();
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];  // 아두이노에서 받아온 데이터를 저장하는 buffer
            boolean time_flag = false;
            boolean data_flag = false;
            int bytes;
            String str_time = "";
            int int_time;
            String str_data = "";
            int int_data;
            String str_BPM = "";
            while (true) {
                try {
                    bytes = mmInStream.available(); // 아두이노에서 한번에 받을 수 있는 양을 bytes에 저장합니다.
                    if (bytes != 0) {
                        cntBPM++;
                        buffer = new byte[1024]; // Buffer을 이용해서 아두이노로부터 ECG 데이터를 받습니다.
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read

                        for (int i = 0; i < bytes; i++){
                            if ('/' == (char)buffer[i]) time_flag = false;
                            if (time_flag) str_time = str_time + (char)buffer[i];
                            if (data_flag) str_data = str_data + (char)buffer[i];

                            if ('_' == (char)buffer[i]) time_flag = true;
                            else if ('-' == (char)buffer[i]) data_flag = true;
                        }

                        int_time = str2int(str_time);
                        int_data = str2int(str_data);
                        data_flag = false;
                        System.out.println("int_time : " + int_time + " | int_data : " + int_data);

                        if (cntBPM > 5){ // 평균 측정이 완료되면
                            if (timeList.peek() != null && dataList.peek() != null){ // 데이터가 이미 들어있을 때 거짓말 판별 시작
                                str_BPM = "BPM : " + Integer.toString(60000 / int_data); // BPM 공식 (60 * 1000 / RR Interval)
                                mShowBPM.setText(str_BPM);
                                if ((dataList.poll() - int_data) >= avgBPM * 0.15){ // 심박수 차이가 평균값의 15%정도 빨라졌을 경우
                                    //returnBPM[1] = 1;
                                    mShowBPM.setTextColor(Color.parseColor("#ff0000"));
                                    resultList.add(int_time); // 결과 큐에 이상심박수를 감지한 시간대만을 넣어준다.
                                }
                                else{
                                    mShowBPM.setTextColor(Color.parseColor("#00ff00"));
                                }

                                timeList.poll(); // 데이터리스트도 빼줬으므로 타임리스트도 똑같이 빼준다.
                            }

                            //returnBPM[0] = int_data;
                            //textBPM();
                        }


                        timeList.add(int_time);
                        dataList.add(int_data);

                        last_time = int_time;
                        avgSUM += int_data;
                        avgBPM = (float)avgSUM / (float)cntBPM;

                        str_time = "";
                        str_data = "";


                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

        public int str2int(String data) {
            int sum = 0;
            int temp;
            int len = data.length();

            for (int i = 0; i < len; i++) {
                temp = data.charAt(i)-48;
                for (int j = len-i; j > 1; j--) temp *= 10;
                sum += temp;
            }
            return sum;
        }

        public ArrayList<Integer> getResult(){ // stop 눌렀을 때 resultList 반환
            resultList.add(last_time); // 마지막 측정 시간을 저장하여 측정시간을 보내준다.
            for (int i = 0; i < resultList.size(); i++){
                returnList.add(resultList.get(i));
            }
            resultList.clear();
            return returnList;
        }

    /*
    public void textBPM(){
            if (returnBPM[1] == 1){ // 이상심박수일 시 빨강글씨
                showBPM.setText(returnBPM[0]);
                showBPM.setTextColor(0xFFFF00);
            }
            else{ // 정상이면 초록글씨
                showBPM.setText(returnBPM[0]);
                showBPM.setTextColor(0xFF00FF);
            }
    }



    public void setText(TextView txt){
        showBPM = txt;
    }
    */

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            if (input == "1"){
                returnList.clear();
                resultList.clear();
                avgBPM = 0;
                avgSUM = 0;
                cntBPM = 0;
            }
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
            }
        }
    }
}