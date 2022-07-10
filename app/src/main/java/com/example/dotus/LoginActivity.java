package com.example.dotus;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;

import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {

    ImageButton kakaoLoginBtn;
    final String baseUrl = "http://192.249.18.118:80";
    Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
        initListener();
        initSocket();

        loginCheck();
    }

    private void initView() {
        kakaoLoginBtn = findViewById(R.id.kakao_login_button);
    }

    private void initListener() {
        kakaoLoginBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(UserApiClient.getInstance().isKakaoTalkLoginAvailable(LoginActivity.this))
                    login();
                else
                    accountLogin();
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

    private void loginCheck() {
        UserApiClient.getInstance().accessTokenInfo((accessTokenInfo, throwable) -> {
            if(throwable != null) {
                Log.e(TAG, "토큰 없음", throwable);
            }
            else if(accessTokenInfo != null)
                getUserInfo();
            return null;
        });
    }

    public void login() {
        String TAG = "login()";
        UserApiClient.getInstance().loginWithKakaoTalk(LoginActivity.this,(oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }

    public void accountLogin() {
        String TAG = "accountLogin()";
        UserApiClient.getInstance().loginWithKakaoAccount(LoginActivity.this,(oAuthToken, error) -> {
            if (error != null) {
                Log.e(TAG, "로그인 실패", error);
            } else if (oAuthToken != null) {
                Log.i(TAG, "로그인 성공(토큰) : " + oAuthToken.getAccessToken());
                getUserInfo();
            }
            return null;
        });
    }

    public void getUserInfo() {
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

                addUser(user);

                //여기서부터는 마지막 mSocket.on event에 넣기
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("name", user.getKakaoAccount().getProfile().getNickname());
                intent.putExtra("profile", user.getKakaoAccount().getProfile().getProfileImageUrl());
                startActivity(intent);
                mSocket.disconnect();
                finish();
            }
            return null;
        });
    }

    public void addUser(User user) {
        mSocket.emit("existUser", user.getId() + "");
        Log.i("existUserNum", user.getId() + "");

        mSocket.on("exist_user", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("existUserNum", (boolean)args[0]+"");
                if (!(boolean) args[0]) {
                    mSocket.emit("addUser", user.getId() + "", user.getId() + "ABC", user.getKakaoAccount().getProfile().getProfileImageUrl(), new JSONObject());
                }
            }
        });
    }
}