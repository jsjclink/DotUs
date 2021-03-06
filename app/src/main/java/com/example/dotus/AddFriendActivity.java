package com.example.dotus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.net.URISyntaxException;
import java.nio.file.Path;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AddFriendActivity extends AppCompatActivity {

    private EditText enterId;
    private TextView notify;
    private Button addFriendBtn;
    private String friend, myId;
    final String baseUrl = "http://192.249.18.118:443/";
    Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initView();
        initListener();
        initSocket();

        Intent intent = getIntent();
        myId = intent.getStringExtra("myId");
    }

    private void initView() {
        enterId = findViewById(R.id.search);
        notify = findViewById(R.id.notify);
        addFriendBtn = findViewById(R.id.add_friend);
    }

    private void initListener() {
        addFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                friend = enterId.getText().toString();

                if(friend.length() == 0)
                    notify.setText("아이디를 입력하세요.");
                else if(friend.equals(myId)) {
                    notify.setText("본인 Id입니다.");
                }
                else {
                    mSocket.emit("verifyFriend", friend, myId);
                    mSocket.on("verify_friend", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String received = args[0].toString();
                                    if(received.equals("invalid_id")) notify.setText("존재하지 않는 ID입니다.");
                                    else if(received.equals("already added")) notify.setText("이미 추가된 친구입니다.");
                                    else notify.setText("친구가 정상적으로 추가되었습니다.");
                                }
                            });
                            if (args[0].toString().equals("add friend success")){
                                mSocket.emit("addFriend", friend, myId);
                            }
                        }
                    });
                }
            }
        });
    }

    private void initSocket() {
        try {
            mSocket = IO.socket(baseUrl + "account");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
        setResult(200);
        finish();
    }
}