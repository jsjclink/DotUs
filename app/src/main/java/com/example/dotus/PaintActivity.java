package com.example.dotus;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);

        initView();

        Intent intent = this.getIntent();
        String namespace = intent.getStringExtra("namespace");
        //지워!!
        namespace = "debug";

        switch(namespace){
            case "paint":
                String user_id = intent.getStringExtra("target_user_id");
                initListener_paint();
                initSocket_paint(user_id);
                break;
            case "global":
                initListener_global();
                initSocket_global();
                break;
            case "channel":
                initListener_channel();
                initSocket_channel();
                break;
            case "debug":
                initListener_debug();
                initSocket_debug();
        }

        paintView = new PaintView(this);
        stage.addView(paintView);
    }

    private void initView() {
        stage = findViewById(R.id.stage);
        sendBtn = findViewById(R.id.send);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
        finish();
    }

    private void initListener_paint(){

    }
    private void initListener_global(){

    }
    private void initListener_channel(){

    }
    private void initListener_debug(){
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user_id = "global_test";
                int pixel_size = 300;
                if(pixel_size <= 100){
                    Bitmap image =  Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.game), pixel_size, pixel_size, true);
                    int[] array = new int[image.getWidth()*image.getHeight()];
                    image.getPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
                    mSocket.emit("putData", Arrays.toString(array), user_id, image.getWidth(), image.getHeight());
                }
                else{/*
                    int[] array = new int[pixel_size * pixel_size];
                    for(int i = 0; i < pixel_size*pixel_size; i++){
                        array[i] = Color.BLACK;
                    }
                    for(int i = 0; i < pixel_size; i++){

                        mSocket.emit("putDataLong", Arrays.toString(Arrays.copyOfRange(array, i*pixel_size, (i+1)*pixel_size)), user_id, pixel_size, pixel_size, i);
                    }*/
                }
            }
        });
    }
    private void initSocket_paint(String user_id){
        try {
            mSocket = IO.socket(baseUrl + "paint");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
        mSocket.emit("join", user_id);

        mSocket.on("img_ret", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String img_str = (String) args[0];
                int width = (int) args[1];
                int height = (int) args[2];
                String[] str_arr = img_str.replaceAll("[\\[\\]]", "").split(",");
                int[] array = new int[str_arr.length];
                for(int i = 0;  i < str_arr.length; i++){
                    array[i] = Integer.parseInt(str_arr[i].trim());
                }
                paintView.initPixelInfo(array, width, height);
            }
        });
        mSocket.on("change_dot", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                paintView.changePixelArray((int) args[0], (int) args[1]);
            }
        });
    }
    private void initSocket_global(){
        try {
            mSocket = IO.socket(baseUrl + "global");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
    }
    private void initSocket_channel(){
        try {
            mSocket = IO.socket(baseUrl + "channel");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
    }
    private void initSocket_debug(){
        try {
            mSocket = IO.socket(baseUrl + "debug");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();
    }

    public void pixelChanged(int index, int color){
        Log.d("pixelChanged", "pixelChanged");
        mSocket.emit("imgChange", index, color);
    }
}
