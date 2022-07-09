package com.example.dotus;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class PaintActivity extends AppCompatActivity {
    PaintView paintView;
    FrameLayout stage;
    Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);

        initView();
        initListener();
        initSocket();

        paintView = new PaintView(this);
        stage.addView(paintView);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
        finish();
    }

    private void initView() {
        stage = findViewById(R.id.stage);
    }
    private void initListener() {
    }
    private void initSocket(){
        try {
            mSocket = IO.socket("http://192.249.18.118:443");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
    }
}
