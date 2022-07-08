package com.example.dotus;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.talk.TalkApi;
import com.kakao.sdk.talk.TalkApiClient;
import com.kakao.sdk.talk.model.Friend;
import com.kakao.sdk.talk.model.Friends;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    Button kakaoLoginBtn, gotoPaintBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("KeyHash", Utility.INSTANCE.getKeyHash(this));

        initView();
        initListener();

    }

    private void initView() {
        kakaoLoginBtn = findViewById(R.id.kakao_login_button);
        gotoPaintBtn = findViewById(R.id.gotoPaint);

    }
    private void initListener() {
        kakaoLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(MainActivity.this)){
                    login();
                }
                else{
                    accountLogin();
                }
            }
        });

        gotoPaintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PaintActivity.class);
                startActivity(intent);
                //finish() 하면 뒤로가기 눌러도 못돌아감
            }
        });
    }

    public void login(){
        String TAG = "login()";
        UserApiClient.getInstance().loginWithKakaoTalk(MainActivity.this,(oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }

    public void accountLogin(){
        String TAG = "accountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(MainActivity.this,(oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }

    public void getUserInfo(){
        String TAG = "getUserInfo()";
        UserApiClient.getInstance().me((user, meError) -> {
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else {
                System.out.println("로그인 완료");
                Log.i(TAG, user.toString());
                {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: "+user.getId() +
                            "\n이메일: "+user.getKakaoAccount().getEmail());
                }

                Account user1 = user.getKakaoAccount();
                System.out.println("사용자 계정" + user1);
            }
            return null;
        });
    }
}