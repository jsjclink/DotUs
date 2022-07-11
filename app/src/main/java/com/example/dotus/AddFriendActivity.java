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
    private int existUser;
    private boolean existFriend;
    final String baseUrl = "http://192.249.18.118:80";
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
                Log.i("my id: ", myId);
                Log.i("entered id: ", friend);

                if(friend.length() == 0)
                    notify.setText("아이디를 입력하세요.");
                else if(friend.equals(myId)) {
                    notify.setText("본인 Id입니다.");
                }
                else {
                    mSocket.emit("existUserId", friend);
                    mSocket.on("exist_user_id", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            existUser = (int)args[0];
                        }
                    }); //user 존재 확인

                    mSocket.emit("existFriend", friend);
                    mSocket.on("exist_friend", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            existFriend = (boolean)args[0];
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(existUser != 0 && !existFriend) {
                                        mSocket.emit("addFriend", friend, myId);
                                        notify.setText("친구가 추가되었습니다.");
                                    }
                                    else if(existUser != 0 && existFriend) {
                                        notify.setText("이미 추가된 친구입니다.");
                                    }
                                    else {
                                        notify.setText("존재하지 않는 Id입니다.");
                                    }
                                } // on 받고 setText
                            });
                        }
                    }); //친구목록에 존재 확인
                }
            }
        });
    }

    private void initSocket() {
        try {
            mSocket = IO.socket(baseUrl);
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
        finish();
    }
}