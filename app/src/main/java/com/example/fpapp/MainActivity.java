package com.example.fpapp;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;





public class MainActivity extends AppCompatActivity {
    BufferedReader socket_in;
    PrintWriter socket_out;
    private Socket socket;
    String data = "home";
    private FragmentManager fragmentManager = getSupportFragmentManager();
    public static final String NOTIFICATION_CHANNEL_ID = "10001";

    private menu1Fragment menu1Fragment = new menu1Fragment();
    private menu2Fragment menu2Fragment = new menu2Fragment();
    ConstraintLayout container;

    TextView output;
    @Override
    protected void onStop() {
        // TODO Auto-generated method stub
        super.onStop();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // 노티피케이션 기본 설정
    private void createNotification() {
        PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, 0,
                new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(MainActivity.this, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle("낙상 사고 위험 감지")
                .setContentText("홍길동\t103호")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("홍길동\t103호"))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(mPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());

    }

    // 알림 채널 생성하기
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library.
        // API 26 이상에서는 기존에 있던 라이브러리에 없어서 따로 채널을 생성해야함
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        output = (TextView) findViewById(R.id.textv2);
        Thread worker = new Thread() {
            public void run() {
                try {
                    socket = new Socket("192.168.0.29", 8080);// 서버 ip와 port에 연결
                    socket_out = new PrintWriter(socket.getOutputStream(), true); //ouput 스트림에 객체를 가져옴
                    socket_in = new BufferedReader(new InputStreamReader(socket.getInputStream())); //input 스트림에 객체를 가져옴
                    createNotificationChannel();
                    createNotification();
                } catch (IOException e) {
                    e.printStackTrace(); //에러 메세지의 발생 근원지를 찾아 단계별로 에러 출력
                }
                try {
                    while (true) {
                        data = socket_in.readLine(); //읽어들인 소켓을 data에 저장
                        output.post(new Runnable() {
                            public void run() {
                                output.setText(data); //data에 저장된 메세지를 output 텍스트뷰로 날림
                            }
                        });
                    }

                } catch (Exception e) {
                }
            }
        };
        worker.start();

        // 화면 하단부분의 메뉴 버튼 설정
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.frame_layout, menu1Fragment).commitAllowingStateLoss();

        // 경보, 알림 버튼 설정
        final Button btnAlert = findViewById(R.id.btnAlert);
        final Button btnNoti = findViewById(R.id.btnNoti);
        // 버튼 기능 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_menu1: {
                        transaction.replace(R.id.frame_layout, menu1Fragment).commitAllowingStateLoss();

                        // 홈 버튼을 눌러 보여지는 화면에서는 버튼들이 활성화됨
                        btnAlert.setVisibility(View.VISIBLE);
                        btnNoti.setVisibility(View.VISIBLE);
                        // 경보 팝업 컨테이너 창 시각화 설정
                        container.setVisibility(View.VISIBLE);
                        break;
                    }
                    case R.id.navigation_menu2: {
                        transaction.replace(R.id.frame_layout, menu2Fragment).commitAllowingStateLoss();
                        // 하단 2번쨰 네비 메뉴 화면에서는 기존에 있던 버튼들 비활성화
                        btnAlert.setVisibility(View.GONE);
                        btnNoti.setVisibility(View.GONE);
                        container.setVisibility(View.GONE);
                        break;
                    }
                }
                return true;
            }
        });

        Button btnPass = findViewById(R.id.btnPass);
        Button btnAccept = findViewById(R.id.btnAccept);

        // 경보 눌러서 나오는 layout을 담는 container layout
        container = (ConstraintLayout) findViewById(R.id.container);

        btnAlert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                inflater.inflate(R.layout.fp_alert, container, true);

            }
        });


        // 알림 버튼 설정
        btnNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNotificationChannel();
                createNotification();
            }
        });


    }



}


