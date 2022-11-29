package com.example.polygraph;

import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private Queue<Integer> timeList;
    private Queue<Integer> dataList;
    private ArrayList<Integer> resultList;
    private int last_time;

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
        while (true) {
            try {
                bytes = mmInStream.available(); // 아두이노에서 한번에 받을 수 있는 양을 bytes에 저장합니다.
                if (bytes != 0) {
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
                    str_time = "";
                    str_data = "";
                    data_flag = false;
                    System.out.println("int_time : " + int_time + " | int_data : " + int_data);

                    if (timeList.peek() != null && dataList.peek() != null){ // 데이터가 이미 들어있을 때 거짓말 판별 시작
                        if (Math.abs(dataList.poll() - int_data) >= 100){ // 심박수 차이가 100이상 (비정상)일 경우, 즉 심박수가 확 뛰었을 경우
                            resultList.add(int_time); // 결과 큐에 이상심박수를 감지한 시간대만을 넣어준다.
                        }
                        timeList.poll(); // 데이터리스트도 빼줬으므로 타임리스트도 똑같이 빼준다.
                    }

                    timeList.add(int_time);
                    dataList.add(int_data);

                    last_time = int_time;

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
        return resultList;
    }

    /* Call this from the main activity to send data to the remote device */
    public void write(String input) {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
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