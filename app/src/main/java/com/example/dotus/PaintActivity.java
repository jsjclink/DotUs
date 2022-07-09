package com.example.dotus;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.net.URISyntaxException;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class PaintActivity extends AppCompatActivity {
    final String baseUrl = "http://192.249.18.118:443/";
    PaintView paintView;
    FrameLayout stage;
    Socket mSocket;
    Button sendBtn, colorPickBtn;
    int[] array;

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
        sendBtn = findViewById(R.id.send);
    }
    private void initListener() {
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {/*
                Bitmap image =  Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.game), 100, 100, true);
                array = new int[image.getWidth()*image.getHeight()];
                image.getPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
                mSocket.emit("putData", Arrays.toString(array));*/
                mSocket.emit("getData", "");
            }
        });
    }
    private void initSocket(){
        try {
            mSocket = IO.socket(baseUrl + "paint");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
        //param id 받아서 id값 넣게
        String id = "user_02";
        //이 정보 이용해서 room에 넣기
        mSocket.emit("join", id);

        mSocket.on("img_ret", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("HIHI", "HIHIHI");
                Log.d("recieved", (String) args[0]);
                String img_str = (String) args[0];
                String[] str_arr = img_str.replaceAll("[\\[\\]]", "").split(",");
                array = new int[str_arr.length];
                for(int i = 0;  i < str_arr.length; i++){
                    array[i] = Integer.parseInt(str_arr[i].trim());
                }

                paintView.setPixelArrayPixelSet(array);
            }
        });
        mSocket.on("change_dot", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.d("img_change", String.format("%d, %d, %d", (int) args[0], (int) args[1], (int )args[2]));
                paintView.changePixelArray((int) args[0], (int) args[1], (int )args[2]);
            }
        });
    }
    public void pixelChanged(int x, int y, int color){
        Log.d("pixelChanged", "pixelChanged");
        mSocket.emit("imgChange", x, y, color);
    }
}
