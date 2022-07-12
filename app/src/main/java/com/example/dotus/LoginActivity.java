package com.example.dotus;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;
import com.kakao.sdk.user.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginActivity extends AppCompatActivity {

    ImageButton kakaoLoginBtn;
    final String baseUrl = "http://192.249.18.118:443/"; //namespace 사용하려면 끝에 /붙이기
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
            mSocket = IO.socket(baseUrl + "account");
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
            else if(accessTokenInfo != null){
                Log.i("TOKEN", "토큰 있음");
                getUserInfo(false);
            }
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
                getUserInfo(true);
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
                getUserInfo(true);
            }
            return null;
        });
    }

    public void getUserInfo(boolean add) {
        String TAG = "getUserInfo()";
        Log.i("Getuserinfo", "실행됨");
        UserApiClient.getInstance().me((user, meError) -> {
            Log.i("LoginFunc", "실행됨");
            if (meError != null) {
                Log.e(TAG, "사용자 정보 요청 실패", meError);
            } else if(user != null){
                System.out.println("로그인 완료");
                Log.i(TAG, user.toString());
                {
                    Log.i(TAG, "사용자 정보 요청 성공" +
                            "\n회원번호: "+user.getId() +
                            "\n이메일: "+user.getKakaoAccount().getEmail());
                }
                Account user1 = user.getKakaoAccount();
                System.out.println("사용자 계정" + user1);
                if(add){
                    mSocket.emit("existUserKakaoNum", user.getId() + "");
                    mSocket.on("exist_user_kakaonum", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.i("existKakaoNum", args[0]+"");
                            if ((int)args[0] == 0) {
                                mSocket.emit("addUser", user.getKakaoAccount().getProfile().getNickname(), user.getId() + "", user.getId() + "ABC", user.getKakaoAccount().getProfile().getProfileImageUrl(), new JSONArray());

                                //첫 로그인 시 putData
                                try {
                                    int pixel_size = 100;
                                    URL url = new URL(user.getKakaoAccount().getProfile().getProfileImageUrl());
                                    Bitmap image =  Bitmap.createScaledBitmap(BitmapFactory.decodeStream(url.openConnection().getInputStream()), pixel_size, pixel_size, true);
                                    System.out.println(image.getWidth());
                                    int[] array = new int[image.getWidth()*image.getHeight()];
                                    image.getPixels(array, 0, image.getWidth(), 0, 0, image.getWidth(), image.getHeight());
                                    System.out.println("IMG SIZE!!!!!!!!!" + image.getWidth() + " " + image.getHeight());
                                    mSocket.emit("putData", Arrays.toString(array), user.getId() + "ABC", image.getWidth(), image.getHeight());
                                } catch(IOException e) {
                                    System.out.println(e);
                                }
                            }
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("name", user.getKakaoAccount().getProfile().getNickname());
                            intent.putExtra("profile", user.getKakaoAccount().getProfile().getProfileImageUrl());
                            intent.putExtra("id", user.getId() + "ABC");
                            startActivity(intent);
                            //mSocket.disconnect();
                            //finish();
                        }
                    });
                }
                else {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("name", user.getKakaoAccount().getProfile().getNickname());
                    intent.putExtra("profile", user.getKakaoAccount().getProfile().getProfileImageUrl());
                    intent.putExtra("id", user.getId() + "ABC");
                    startActivity(intent);
                    mSocket.disconnect();
                    finish();
                }
            }
            else{
                Log.i("Login User", "NULL");
            }
            return null;
        });
    }
}