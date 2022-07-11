package com.example.dotus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChannelRoomActivity extends AppCompatActivity {
    final String baseUrl = "http://192.249.18.118:443/";
    Socket mSocket;
    Button makeRoomBtn, joinRoomBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel);

        initView();
        initListener();
        initSocket();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
        finish();
    }

    private void initView() {
        makeRoomBtn = findViewById(R.id.makeroom);
        joinRoomBtn = findViewById(R.id.joinroom);
    }

    private void initListener() {
        makeRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String room_name = "custom_test_room";
                String pwd = "qwerty123";
                mSocket.emit("makeRoom", room_name, pwd);
            }
        });
        /*
        joinRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String room_name = "custom_test_room";
                mSocket.emit("joinRoom", room_name);
            }
        });*/
    }

    private void initSocket(){
        try {
            mSocket = IO.socket(baseUrl + "roomInfo");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();

        mSocket.on("user_room_list", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
                //Log.d("get user room list", args[0]);
            }
        });
        mSocket.on("makeRoom_res", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String res = (String) args[0];
                switch(res){
                    case "success":
                        System.out.println("success");
                        break;
                    case "failure":
                        System.out.println("failure");
                        break;
                }
            }
        });
    }
}
