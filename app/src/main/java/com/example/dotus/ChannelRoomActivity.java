package com.example.dotus;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChannelRoomActivity extends AppCompatActivity {
    final String baseUrl = "http://192.249.18.118:443/";
    Socket mSocket;
    Button makeRoomBtn;
    RecyclerView roomListRv;
    LinearLayoutManager linearLayoutManager;
    ArrayList<String> roomList;
    RoomListAdapter roomListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.channel);

        initView();
        initListener();
        initSocket();

        linearLayoutManager = new LinearLayoutManager(this);
        roomListRv.setLayoutManager(linearLayoutManager);

        roomList = new ArrayList<>();

        roomListAdapter = new RoomListAdapter();
        roomListRv.setAdapter(roomListAdapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mSocket.disconnect();
        finish();
    }

    private void initView() {
        makeRoomBtn = findViewById(R.id.makeroom);
        roomListRv = findViewById(R.id.roomlist);
    }

    private void initListener() {
        makeRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(ChannelRoomActivity.this);

                AlertDialog.Builder dialog = new AlertDialog.Builder(ChannelRoomActivity.this);
                final Typeface face = ResourcesCompat.getFont(ChannelRoomActivity.this, R.font.maenbal);
                editText.setTypeface(face);
                dialog.setTitle("방 번호 입력");
                dialog.setView(editText);
                dialog.setPositiveButton("입력", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String pwd = "qwerty123";
                        mSocket.emit("makeRoom", editText.getText().toString(), pwd);
                    }
                });
                dialog.show();
            }
        });
    }

    private void initSocket(){
        try {
            mSocket = IO.socket(baseUrl + "roomInfo");
        }
        catch (URISyntaxException e){
            e.printStackTrace();
        }
        //mSocket.connect하기 전에 on 해줘야함 on은 emit, connect전에 무조건

        mSocket.on("user_room_list", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                System.out.println(args[0]);
                JSONArray jsonArray = (JSONArray)args[0];
                for(int i = 0; i < jsonArray.length(); i++){
                    try{
                        roomList.add((String)jsonArray.get(i));
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        roomListAdapter.notifyDataSetChanged();
                    }
                });
                //Log.d("get user room list", args[0]);
            }
        });
        mSocket.on("makeRoom_res", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                String res = (String) args[0];
                switch(res){
                    case "success":
                        String room_name = (String) args[1];

                        System.out.println("success");
                        Intent intent = new Intent(ChannelRoomActivity.this, PaintActivity.class);
                        intent.putExtra("namespace", "insideRoom");
                        intent.putExtra("target_room_name", room_name);
                        intent.putExtra("is_opener", true);
                        startActivity(intent);
                        mSocket.disconnect();
                        finish();
                        break;
                    case "failure":
                        System.out.println("failure");
                        break;
                }
            }
        });

        mSocket.connect();
    }
    private class RoomListAdapter extends RecyclerView.Adapter<RoomListAdapter.MyViewHolder> {

        @NonNull
        @Override
        public RoomListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(ChannelRoomActivity.this);
            View view = inflater.inflate(R.layout.recycler_view_row_roomlist, parent, false);
            return new RoomListAdapter.MyViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RoomListAdapter.MyViewHolder holder, int position) {
            holder.tvRoomName.setText(roomList.get(position));

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String room_name = roomList.get(holder.getAdapterPosition());
                    System.out.println(room_name);

                    Intent intent = new Intent(ChannelRoomActivity.this, PaintActivity.class);
                    intent.putExtra("namespace", "insideRoom");
                    intent.putExtra("target_room_name", room_name);
                    intent.putExtra("is_opener", false);
                    startActivity(intent);
                    mSocket.disconnect();
                    finish();
                }
            });
        }

        @Override
        public int getItemCount() {
            return roomList.size();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder{
            TextView tvRoomName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);

                tvRoomName = itemView.findViewById(R.id.room_name);
            }
        }
    }
}
