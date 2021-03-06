package com.example.milkbottle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.room.Room;
import androidx.room.TypeConverter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.AsyncQueryHandler;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;

import android.icu.text.SimpleDateFormat;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import app.akexorcist.bluetotohspp.library.BluetoothSPP;
import app.akexorcist.bluetotohspp.library.BluetoothState;
import app.akexorcist.bluetotohspp.library.DeviceList;

public class MainActivity extends AppCompatActivity {

    Button menuBtn;
    Button beforeBtn;
    Button afterBtn;
    Button recordBtn; //데이터 삽입버튼
//    Button deleteBtn; //데이터 삭제버튼

    EditText beforeTxt;
    EditText afterTxt;
//    TextView test;
    TextView bluetoothTest;

    Button graphBtn;
    Button bluetoothBtn;

    float befoData, afterData, quantity;
    Date date;
    Date currDate;
    Float currFloat;

    BluetoothSPP bt;
    public void test(){
        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        Calendar cal = Calendar.getInstance();
        cal.set(2021,1,10,3,49,47);
        Date day = cal.getTime();

        befoData = (float)  0.10;
        afterData = (float) 0.05;
        currDate = day;
        currFloat = (float)day.getTime();
        quantity = befoData - afterData;
        viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));

        cal.set(2021,2,11,8,49,47);
        day = cal.getTime();

        befoData = (float)  0.15;
        afterData = (float) 0.04;
        currDate = day;
        currFloat = (float)day.getTime();
        quantity = befoData - afterData;
        viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));

        cal.set(2021,3,12,11,49,47);
        day = cal.getTime();

        befoData = (float)  0.18;
        afterData = (float) 0.03;
        currDate = day;
        currFloat = (float)day.getTime();
        quantity = befoData - afterData;
        viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel  viewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        bt = new BluetoothSPP(this);
//        test();
        //UI 갱신
//       viewModel.getAll().observe(this, milkData -> {
//           Log.d("DB","date data "+milkData.toString());
//            test.setText(milkData.toString());
//        });
//        viewModel.deleteAll();
        menuBtn = (Button) findViewById(R.id.menu);

        beforeBtn = (Button) findViewById(R.id.beforVal);
        afterBtn = (Button) findViewById(R.id.afterVal);
        recordBtn = (Button) findViewById(R.id.recode);
//        deleteBtn = (Button) findViewById(R.id.delete);
        beforeTxt = (EditText) findViewById(R.id.beforData);
        afterTxt = (EditText) findViewById(R.id.afterData);
//        test = (TextView) findViewById(R.id.test);

        //TODO
//        graphBtn = (Button) findViewById(R.id.graph);
//        graphBtn.setOnClickListener(v -> {
//            Intent intent = new Intent(getApplicationContext(), GraphActivity.class);
//            startActivity(intent);
//        });
//
//        bluetoothBtn = (Button) findViewById(R.id.bluetooth);
//        bluetoothBtn.setOnClickListener(v -> {
//
//
//
//
//                });


//        test.setMovementMethod(ScrollingMovementMethod.getInstance());

        //메뉴버튼
        menuBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final PopupMenu popup = new PopupMenu(getApplicationContext(),v);

                getMenuInflater().inflate(R.menu.option_menu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener(){

                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.name1:
                                Toast.makeText(getApplication(),"블루투스 연결",Toast.LENGTH_SHORT).show();;
                                break;
                            case R.id.name2:
                                Toast.makeText(getApplication(),"그래",Toast.LENGTH_SHORT).show();;
                                break;
                            case R.id.name3:
                                Toast.makeText(getApplication(),"통",Toast.LENGTH_SHORT).show();;
                                break;
                        }
                        return false;
                    }
                });

                popup.show();
            }
        });

        //버튼 클릭 시 DB에 insert
        recordBtn.setOnClickListener(v -> {
            befoData= Float.parseFloat(beforeTxt.getText().toString());
            afterData = Float.parseFloat(afterTxt.getText().toString());
            currDate = new Date(System.currentTimeMillis());
            currFloat = (float)System.currentTimeMillis();
            quantity = befoData - afterData;
            viewModel.insert(new MilkData(befoData, afterData, quantity, currDate, currFloat));
            Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();

        });

//        deleteBtn.setOnClickListener(v -> {
//            viewModel.deleteAll(new MilkData(befoData, afterData, quantity, currDate, currFloat));
//        });

        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신

            public void onDataReceived(byte[] data, String message) {
                bluetoothTest.setText(message);

            }
        });


        //먹기 전 분유량 가져오기
        beforeBtn.setOnClickListener(v -> {
            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                public void onDataReceived(byte[] data, String message) {
                    beforeTxt.setText(message);
                }
            });
        });

        //먹은 후 분유량 가져오기
        afterBtn.setOnClickListener(v -> {
            bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() { //데이터 수신
                public void onDataReceived(byte[] data, String message) {
                    afterTxt.setText(message);
                }
            });
        });

        //블루투스 연결 관련
        if (!bt.isBluetoothAvailable()) { //블루투스 사용 불가라면
            // 사용불가라고 토스트 띄워줌
            Toast.makeText(getApplicationContext()
                    , "Bluetooth is not available"
                    , Toast.LENGTH_SHORT).show();
            // 화면 종료
            finish();
        }

        // 데이터를 받았는지 감지하는 리스너
        bt.setOnDataReceivedListener(new BluetoothSPP.OnDataReceivedListener() {
            //데이터 수신되면
            public void onDataReceived(byte[] data, String message) {
            }
        });
        // 블루투스가 잘 연결이 되었는지 감지하는 리스너
        bt.setBluetoothConnectionListener(new BluetoothSPP.BluetoothConnectionListener() { //연결됐을 때
            public void onDeviceConnected(String name, String address) {
                Toast.makeText(getApplicationContext()
                        , "Connected to " + name + "\n" + address
                        , Toast.LENGTH_SHORT).show();
            }

            public void onDeviceDisconnected() { //연결해제
                Toast.makeText(getApplicationContext()
                        , "Connection lost", Toast.LENGTH_SHORT).show();
            }

            public void onDeviceConnectionFailed() { //연결실패
                Toast.makeText(getApplicationContext()
                        , "Unable to connect", Toast.LENGTH_SHORT).show();
            }
        });

        bluetoothBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if (bt.getServiceState() == BluetoothState.STATE_CONNECTED) { // 현재 버튼의 상태에 따라 연결이 되어있으면 끊고, 반대면 연결
                    bt.disconnect();
                } else {
                    Intent intent = new Intent(getApplicationContext(), DeviceList.class);
                    startActivityForResult(intent, BluetoothState.REQUEST_CONNECT_DEVICE);
                }
            }
        });
    }
    // 앱 중단시 (액티비티 나가거나, 특정 사유로 중단시)
    public void onDestroy() {
        super.onDestroy();
        bt.stopService(); //블루투스 중지
    }
    // 앱이 시작하면
    public void onStart() {
        super.onStart();
        if (!bt.isBluetoothEnabled()) { // 앱의 상태를 보고 블루투스 사용 가능하면
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            // 새로운 액티비티 띄워줌, 거기에 현재 가능한 블루투스 정보 intent로 넘겨
            startActivityForResult(intent, BluetoothState.REQUEST_ENABLE_BT);
        } else {
            if (!bt.isServiceAvailable()) { // 블루투스 사용 불가
                // setupService() 실행하도록
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER); //DEVICE_ANDROID는 안드로이드 기기끼리
                // 셋팅 후 연결되면 setup()으로
            }
        }
    }

    // 새로운 액티비티 (현재 액티비티의 반환 액티비티?)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 아까 응답의 코드에 따라 연결 가능한 디바이스와 연결 시도 후 ok 뜨면 데이터 전송
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == BluetoothState.REQUEST_CONNECT_DEVICE) { // 연결시도
            if (resultCode == Activity.RESULT_OK) // 연결됨
                bt.connect(data);
        } else if (requestCode == BluetoothState.REQUEST_ENABLE_BT) { // 연결 가능
            if (resultCode == Activity.RESULT_OK) { // 연결됨
                bt.setupService();
                bt.startService(BluetoothState.DEVICE_OTHER);
            } else { // 사용불가
                Toast.makeText(getApplicationContext()
                        , "Bluetooth was not enabled."
                        , Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}