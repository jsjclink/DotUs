package com.example.dotus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
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
import com.kakao.sdk.talk.TalkApi;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friend;
import com.kakao.sdk.talk.model.Friends;
import com.kakao.sdk.template.model.Content;
import com.kakao.sdk.template.model.FeedTemplate;
import com.kakao.sdk.template.model.Link;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;

import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private Button gotoPaintBtn, kakaoShareBtn, kakaoLogoutBtn;
    private FloatingActionButton add_btn;
    private ArrayList<MainData> arrayList;
    private MainAdapter mainAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private ImageView profileImg;
    private TextView nickName;

    FeedTemplate feedTemplate = new FeedTemplate(new Content("DotUs에 당신을 초대합니다!","https://i.im.ge/2022/07/08/ukC3Eh.png",    //메시지 제목, 이미지 url
            new Link("https://www.naver.com"),"닷어스라고 읽으셨나요? '도투스'입니다^^",                    //메시지 링크, 메시지 설명
            300,300));                                                     //이미지 사이즈


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initListener();


        updateKakaoLoginUi();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();

        mainAdapter = new MainAdapter(arrayList);
        recyclerView.setAdapter(mainAdapter);
    }

    private void initView() {
        gotoPaintBtn = findViewById(R.id.gotoPaint);
        kakaoShareBtn = findViewById(R.id.kakao_share);
        kakaoLogoutBtn = findViewById(R.id.kakao_logout_button);
        recyclerView = findViewById(R.id.rv);
        add_btn = findViewById(R.id.add_btn);
        profileImg = findViewById(R.id.my_img);
        nickName = findViewById(R.id.my_name);
    }

    private void initListener() {
        gotoPaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PaintActivity.class);
                startActivity(intent);
                //finish() 하면 뒤로가기 눌러도 못돌아감
            }
        });

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
                    }
                    return null;
                });
            }
        });

        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainData mainData = new MainData(R.drawable.ic_launcher_background, "몰입캠프", "2주차");
                arrayList.add(mainData);
                mainAdapter.notifyDataSetChanged();
            }
        });
    }

    private void updateKakaoLoginUi() {
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String profile = intent.getStringExtra("profile");
        nickName.setText(name);
        Glide.with(profileImg).load(profile).circleCrop().into(profileImg);
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

                    // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                    Log.w("TAG", "Warning Msg: "+ linkResult.getWarningMsg());
                    Log.w("TAG", "Argument Msg: "+ linkResult.getArgumentMsg());
                }
                return null;
            }
        });
    }

    public void webKakaoLink() {
        // 카카오톡 미설치: 웹 공유 사용 권장
        // 웹 공유 예시 코드
        Uri sharerUrl = WebSharerClient.getInstance().defaultTemplateUri(feedTemplate);

        // CustomTabs으로 웹 브라우저 열기
        // 1. CustomTabs으로 Chrome 브라우저 열기
        try {
            KakaoCustomTabsClient.INSTANCE.openWithDefault(MainActivity.this, sharerUrl);
        } catch (UnsupportedOperationException e) {
            // Chrome 브라우저가 없을 때 예외처리
        }

        // 2. CustomTabs으로 디바이스 기본 브라우저 열기
        try {
            KakaoCustomTabsClient.INSTANCE.open(MainActivity.this, sharerUrl);
        } catch (ActivityNotFoundException e) {
            // 인터넷 브라우저가 없을 때 예외처리
        }
    }

}