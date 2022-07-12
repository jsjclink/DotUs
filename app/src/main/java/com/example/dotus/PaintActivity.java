package com.example.dotus;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Array;
import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import yuku.ambilwarna.AmbilWarnaDialog;

public class PaintActivity extends AppCompatActivity {
    final String baseUrl = "http://192.249.18.118:443/";
    PaintView paintView;
    FrameLayout stage;
    Socket mSocket;
    Button colorPickBtn, spoidBtn;
    ImageButton sizeUpBtn, sizeDownBtn;
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    LinearLayoutManager linearLayoutManager;
    ArrayList<Integer> colorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);

        initView();
        initListener_common();

        Intent intent = this.getIntent();
        String namespace = intent.getStringExtra("namespace");
        //지워!!
        //namespace = "debug";

        System.out.println(namespace);
        switch(namespace){
            case "paint":
                String user_id = intent.getStringExtra("target_user_id");
                initSocket_paint(user_id);
                break;
            case "global":
                initSocket_global();
                break;
            case "insideRoom":
                String room_name = intent.getStringExtra("target_room_name");
                boolean is_opener = intent.getBooleanExtra("is_opener", true);

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
        colorPickBtn = findViewById(R.id.color_pick);
        sizeUpBtn = findViewById(R.id.sizeup);
        sizeDownBtn = findViewById(R.id.sizedown);
        spoidBtn = findViewById(R.id.spoid);
        recyclerView = findViewById(R.id.colorblocklist);

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        colorList = new ArrayList<>();
        colorList.add(Color.BLUE);
        colorList.add(Color.BLACK);
        colorList.add(Color.RED);
        colorList.add(Color.GREEN);
        colorList.add(Color.GRAY);
        colorList.add(Color.YELLOW);

        recyclerAdapter = new RecyclerAdapter();
        recyclerView.setAdapter(recyclerAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
        finish();
    }

    private void initListener_common(){
        colorPickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openColorPicker();

            }
        });
        sizeUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.sizeUp();
            }
        });
        sizeDownBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                paintView.sizeDown();
            }
        });
        spoidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch((String)spoidBtn.getText()){
                    case "스포이드":
                        paintView.setSpoid();
                        spoidBtn.setText("색 선택");
                        break;
                    case "팔레트에 추가":
                        colorList.add(paintView.getSpoid());
                        recyclerAdapter.notifyDataSetChanged();
                        spoidBtn.setText("스포이드");
                        break;
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
                System.out.println("strarrlength" + str_arr.length);
                for(int i = 0;  i < str_arr.length; i++){
                    array[i] = Integer.parseInt(str_arr[i].trim());
                }
                System.out.println("PIXEL SIZE!!" + width + " " + height);
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
    private void openColorPicker(){
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(PaintActivity.this, Color.BLACK, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {

            }
            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                paintView.setColor(color);
                colorList.add(color);
                recyclerAdapter.notifyDataSetChanged();
            }
        });
        colorPicker.show();
    }
    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ItemViewHolder>{

        @NonNull
        @Override
        public RecyclerAdapter.ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(PaintActivity.this);
            View view = inflater.inflate(R.layout.color_layout, parent, false);
            return new PaintActivity.RecyclerAdapter.ItemViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerAdapter.ItemViewHolder holder, int position) {
            holder.cardView.setBackgroundColor(colorList.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    paintView.setColor(colorList.get(holder.getAdapterPosition()));
                }
            });
        }

        @Override
        public int getItemCount() {
            return colorList.size();
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder{
            CardView cardView;
            public ItemViewHolder(@NonNull View itemView) {
                super(itemView);
                cardView = itemView.findViewById(R.id.colorblock);
            }
        }
    }
}
