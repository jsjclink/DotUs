package com.example.dotus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kakao.sdk.common.util.KakaoCustomTabsClient;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.link.LinkClient;
import com.kakao.sdk.link.WebSharerClient;
import com.kakao.sdk.link.model.LinkResult;
import com.kakao.sdk.share.ShareClient;
import com.kakao.sdk.talk.TalkApi;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Channel;
import com.kakao.sdk.talk.model.Friend;
import com.kakao.sdk.talk.model.Friends;
import com.kakao.sdk.template.model.Content;
import com.kakao.sdk.template.model.FeedTemplate;
import com.kakao.sdk.template.model.ItemContent;
import com.kakao.sdk.template.model.ItemInfo;
import com.kakao.sdk.template.model.Link;
import com.kakao.sdk.template.model.Social;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private Button kakaoShareBtn, kakaoLogoutBtn, gotoChannelBtn, gotoGlobalBtn;
    private FloatingActionButton add_btn;
    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView profileImg;
    private TextView nickName, noFriend;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    FeedTemplate feedTemplate;
    String name, profile, id;
    final String baseUrl = "http://192.249.18.118:80/";
    Socket mSocket;
    private int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initObject();
        initListener();
        initSocket();

        updateKakaoLoginUi();
        loadFriends();
    }

    private void initView() {
        kakaoShareBtn = findViewById(R.id.kakao_share);
        kakaoLogoutBtn = findViewById(R.id.kakao_logout_button);
        recyclerView = findViewById(R.id.rv);
        add_btn = findViewById(R.id.add_btn);
        profileImg = findViewById(R.id.my_img);
        nickName = findViewById(R.id.my_name);
        noFriend = findViewById(R.id.no_friend);
        gotoChannelBtn = findViewById(R.id.gotochannel);
        gotoGlobalBtn = findViewById(R.id.gotoglobal);
    }

    private void initObject() {
        feedTemplate = new FeedTemplate(
                new Content("DotUs에 당신을 초대합니다!",
                        "https://i.im.ge/2022/07/08/ukC3Eh.png",
                        new Link("kakao698ef6feb4968fb876642447e2007a20://kakaolink",
                                "kakao698ef6feb4968fb876642447e2007a20://kakaolink"),
                        "닷어스라고 읽으셨나요? '도투스'입니다^___^"
                ),
                new ItemContent(),
                new Social(286, 45, 845),
                Arrays.asList(new com.kakao.sdk.template.model.Button("앱으로 보기", new Link("kakao698ef6feb4968fb876642447e2007a20://kakaolink", "kakao698ef6feb4968fb876642447e2007a20://kakaolink")))
        );

        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        mainAdapter = new MainAdapter(arrayList, MainActivity.this);
        recyclerView.setAdapter(mainAdapter);
    }

    private void initListener() {
        kakaoShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)) {
                    kakaoLink();
                } else {
                    webKakaoLink();
                }
            }
        });

        kakaoLogoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String TAG = "logout()";
                UserApiClient.getInstance().logout(error -> {
                    if (error != null) {
                        Log.e(TAG, "로그아웃 실패, SDK에서 토큰 삭제됨", error);
                    }else{
                        Log.e(TAG, "로그아웃 성공, SDK에서 토큰 삭제됨");
                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                    return null;
                });
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddFriendActivity.class);
                intent.putExtra("myId", id);
                startActivityForResult(intent, 200);
            }
        });

        gotoChannelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChannelRoomActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadFriends();
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

    private void updateKakaoLoginUi() {
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        profile = intent.getStringExtra("profile");
        id = intent.getStringExtra("id");
        nickName.setText(name);
        Glide.with(profileImg).load(profile).circleCrop().into(profileImg);
        Log.i("프로필 사진", profile);
        Log.i("url 길이", profile.length() + "");
    }

    private void loadFriends() {
        mSocket.emit("getFriendList", id);
        mSocket.on("get_friend_info", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONArray friendInfo = (JSONArray) args[0];
                for(; count<friendInfo.length(); count++) {
                    try{
                        JSONObject friend = (JSONObject)friendInfo.get(count);
                        String name  = (String) friend.get("nickname");
                        String profile = (String) friend.get("img");
                        String id = (String) friend.get("id");
                        MainData mainData = new MainData(profile, name, id);
                        arrayList.add(mainData);
                        Log.i("mainData", profile);
                        Log.i("mainData", name);
                        Log.i("mainData", id);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mainAdapter.notifyDataSetChanged();
                            }
                        });
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                Log.i("친구 몇명?", arrayList.size() + "");
                if(arrayList.size() != 0)
                    noFriend.setVisibility(View.INVISIBLE);
            }
        });
    }

    public void kakaoLink() {
        LinkClient.getInstance().defaultTemplate(getApplicationContext(), feedTemplate,null,new Function2<LinkResult, Throwable,Unit>() {
            @Override
            public Unit invoke(LinkResult linkResult, Throwable throwable) {
                if (throwable != null) {
                    Log.e("TAG", "카카오링크 보내기 실패", throwable);
                }
                else if (linkResult != null) {
                    getApplicationContext().startActivity(linkResult.getIntent());
                    Log.w("TAG", "Warning Msg: "+ linkResult.getWarningMsg());
                    Log.w("TAG", "Argument Msg: "+ linkResult.getArgumentMsg());
                }
                return null;
            }
        });
    }

    public void webKakaoLink() {
        Uri sharerUrl = WebSharerClient.getInstance().defaultTemplateUri(feedTemplate);
        try {
            KakaoCustomTabsClient.INSTANCE.openWithDefault(MainActivity.this, sharerUrl);
        } catch (UnsupportedOperationException e) {
            e.printStackTrace();
        }

        try {
            KakaoCustomTabsClient.INSTANCE.open(MainActivity.this, sharerUrl);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

}