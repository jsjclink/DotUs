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
    Button colorPickBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);

        initView();

        Intent intent = this.getIntent();
        String namespace = intent.getStringExtra("namespace");
        //지워!!
        //namespace = "debug";

        System.out.println(namespace);
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
            case "insideRoom":
                String room_name = intent.getStringExtra("target_room_name");
                boolean is_opener = intent.getBooleanExtra("is_opener", true);

                initListener_insideRoom();
                if(is_opener){
                    initSocket_insideRoom_Opener(room_name, 100, 100);
                }
                else{
                    initSocket_insideRoom_Joiner(room_name);
                }

                break;
        }

        paintView = new PaintView(this);
        stage.addView(paintView);
    }

    private void initView() {
        stage = findViewById(R.id.stage);
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
    private void initListener_insideRoom(){

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
    private void initSocket_insideRoom_Opener(String room_name, int width, int height){
        try {
            mSocket = IO.socket(baseUrl + "insideRoom");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();

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

        mSocket.emit("initRoom", room_name, width, height);
    }
    private void initSocket_insideRoom_Joiner(String room_name){
        try {
            mSocket = IO.socket(baseUrl + "insideRoom");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        mSocket.connect();

        mSocket.on("img_ret", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println("img_ret called in Joiner");
                System.out.println(args[0]);
                String img_str = (String) args[0];
                int width = (int) args[1];
                int height = (int) args[2];
                String[] str_arr = img_str.replaceAll("[\\[\\]]", "").split(",");
                System.out.println(str_arr.length);
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
                System.out.println("change_dot called in Joiner");
                paintView.changePixelArray((int) args[0], (int) args[1]);
            }
        });

        mSocket.emit("joinRoom", room_name);
    }

    public void pixelChanged(int index, int color){
        Log.d("pixelChanged", "pixelChanged");
        mSocket.emit("imgChange", index, color);
    }
}
