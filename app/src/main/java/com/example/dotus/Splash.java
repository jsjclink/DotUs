package com.example.dotus;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.Account;

import org.json.JSONArray;

import io.socket.emitter.Emitter;

public class Splash extends AppCompatActivity {

    Animation down_anim, delayed_anim;

    View a1, a2, a3, a4;
    View b1, b2, b3;
    View c1, c2, c3, c4, c5, c6;
    View d1, d2, d3, d4, d5;
    View e1, e2, e3, e4, e5;
    View f1, f2, f3, f4, f5;
    View g1, g2, g3, g4, g5, g6, g7, g8;
    View h1, h2, h3, h4, h5;
    View i1, i2, i3, i4;
    View j1, j2, j3;
    View k1, k2, k3, k4, k5;
    View l1, l2, l3;
    View m1, m2, m3, m4;
    View n1, n2, n3, n4, n5;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        initAnim();
        initView();
        setAnim();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                UserApiClient.getInstance().accessTokenInfo((accessTokenInfo, throwable) -> {
                    if(throwable != null) {
                        Log.e(TAG, "토큰 없음", throwable);
                        Intent intent = new Intent(Splash.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else if(accessTokenInfo != null){
                        Log.i("TOKEN", "토큰 있음");
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
                                Intent intent = new Intent(Splash.this, MainActivity.class);
                                intent.putExtra("name", user.getKakaoAccount().getProfile().getNickname());
                                intent.putExtra("profile", user.getKakaoAccount().getProfile().getProfileImageUrl());
                                intent.putExtra("id", user.getId() + "ABC");
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Log.i("Login User", "NULL");
                            }
                            return null;
                        });
                    }
                    return null;
                });
            }
        }, 3300);
    }

    private void initAnim() {
        down_anim = AnimationUtils.loadAnimation(this, R.anim.down_anim);
        delayed_anim = AnimationUtils.loadAnimation(this, R.anim.delayed_anim);
    }

    private void initView() {
        a1= findViewById(R.id.a1);
        a2= findViewById(R.id.a2);
        a3= findViewById(R.id.a3);
        a4= findViewById(R.id.a4);
        b1= findViewById(R.id.b1);
        b2= findViewById(R.id.b2);
        b3= findViewById(R.id.b3);
        c1= findViewById(R.id.c1);
        c2= findViewById(R.id.c2);
        c3= findViewById(R.id.c3);
        c4= findViewById(R.id.c4);
        c5= findViewById(R.id.c5);
        c6= findViewById(R.id.c6);
        d1= findViewById(R.id.d1);
        d2= findViewById(R.id.d2);
        d3= findViewById(R.id.d3);
        d4= findViewById(R.id.d4);
        d5= findViewById(R.id.d5);
        e1= findViewById(R.id.e1);
        e2= findViewById(R.id.e2);
        e3= findViewById(R.id.e3);
        e4= findViewById(R.id.e4);
        e5= findViewById(R.id.e5);
        f1= findViewById(R.id.f1);
        f2= findViewById(R.id.f2);
        f3= findViewById(R.id.f3);
        f4= findViewById(R.id.f4);
        f5= findViewById(R.id.f5);
        g1= findViewById(R.id.g1);
        g2= findViewById(R.id.g2);
        g3= findViewById(R.id.g3);
        g4= findViewById(R.id.g4);
        g5= findViewById(R.id.g5);
        g6= findViewById(R.id.g6);
        g7= findViewById(R.id.g7);
        g8= findViewById(R.id.g8);
        h1= findViewById(R.id.h1);
        h2= findViewById(R.id.h2);
        h3= findViewById(R.id.h3);
        h4= findViewById(R.id.h4);
        h5= findViewById(R.id.h5);
        i1= findViewById(R.id.i1);
        i2= findViewById(R.id.i2);
        i3= findViewById(R.id.i3);
        i4= findViewById(R.id.i4);
        j1= findViewById(R.id.j1);
        j2= findViewById(R.id.j2);
        j3= findViewById(R.id.j3);
        k1= findViewById(R.id.k1);
        k2= findViewById(R.id.k2);
        k3= findViewById(R.id.k3);
        k4= findViewById(R.id.k4);
        k5= findViewById(R.id.k5);
        l1= findViewById(R.id.l1);
        l2= findViewById(R.id.l2);
        l3= findViewById(R.id.l3);
        m1= findViewById(R.id.m1);
        m2= findViewById(R.id.m2);
        m3= findViewById(R.id.m3);
        m4= findViewById(R.id.m4);
        n1= findViewById(R.id.n1);
        n2= findViewById(R.id.n2);
        n3= findViewById(R.id.n3);
        n4= findViewById(R.id.n4);
        n5= findViewById(R.id.n5);
    }

    private void setAnim() {
        try{
            a1.setAnimation(down_anim);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {b1.setVisibility(View.VISIBLE);}
            }, 100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {c1.setVisibility(View.VISIBLE);}
            }, 200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {d1.setVisibility(View.VISIBLE);}
            }, 300/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {e1.setVisibility(View.VISIBLE);}
            }, 400/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {f1.setVisibility(View.VISIBLE);}
            }, 500/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g1.setVisibility(View.VISIBLE);}
            }, 600/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {a2.setVisibility(View.VISIBLE);}
            }, 700/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {a3.setVisibility(View.VISIBLE);}
            }, 800/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {b2.setVisibility(View.VISIBLE);}
            }, 900/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {c2.setVisibility(View.VISIBLE);}
            }, 1000/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {d2.setVisibility(View.VISIBLE);}
            }, 1100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {e2.setVisibility(View.VISIBLE);}
            }, 1200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {f2.setVisibility(View.VISIBLE);}
            }, 1300/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g3.setVisibility(View.VISIBLE);}
            }, 1400/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g2.setVisibility(View.VISIBLE);}
            }, 1500/2);

            //D

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {d3.setVisibility(View.VISIBLE);}
            }, 1600/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {e3.setVisibility(View.VISIBLE);}
            }, 1700/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {f3.setVisibility(View.VISIBLE);}
            }, 1800/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g4.setVisibility(View.VISIBLE);}
            }, 1900/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g5.setVisibility(View.VISIBLE);}
            }, 2000/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {f4.setVisibility(View.VISIBLE);}
            }, 2100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {e4.setVisibility(View.VISIBLE);}
            }, 2200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {d4.setVisibility(View.VISIBLE);}
            }, 2300/2);
            //o

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {a4.setVisibility(View.VISIBLE);}
            }, 2400/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {b3.setVisibility(View.VISIBLE);}
            }, 2500/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {c4.setVisibility(View.VISIBLE);}
            }, 2600/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {d5.setVisibility(View.VISIBLE);}
            }, 2700/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {e5.setVisibility(View.VISIBLE);}
            }, 2800/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {f5.setVisibility(View.VISIBLE);}
            }, 2900/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g6.setVisibility(View.VISIBLE);}
            }, 3000/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g7.setVisibility(View.VISIBLE);}
            }, 3100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {g8.setVisibility(View.VISIBLE);}
            }, 3200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {c3.setVisibility(View.VISIBLE);}
            }, 3300/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {c5.setVisibility(View.VISIBLE);}
            }, 3400/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {c6.setVisibility(View.VISIBLE);}
            }, 3500/2);
            //t

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {h1.setVisibility(View.VISIBLE);}
            }, 3600/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {i1.setVisibility(View.VISIBLE);}
            }, 3700/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {j1.setVisibility(View.VISIBLE);}
            }, 3800/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {k1.setVisibility(View.VISIBLE);}
            }, 3900/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {l1.setVisibility(View.VISIBLE);}
            }, 4000/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {m1.setVisibility(View.VISIBLE);}
            }, 4100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {n1.setVisibility(View.VISIBLE);}
            }, 4200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {n2.setVisibility(View.VISIBLE);}
            }, 4300/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {m2.setVisibility(View.VISIBLE);}
            }, 4400/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {l2.setVisibility(View.VISIBLE);}
            }, 4500/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {k2.setVisibility(View.VISIBLE);}
            }, 4600/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {j2.setVisibility(View.VISIBLE);}
            }, 4700/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {i2.setVisibility(View.VISIBLE);}
            }, 4800/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {h2.setVisibility(View.VISIBLE);}
            }, 4900/2);
            //U

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {i4.setVisibility(View.VISIBLE);}
            }, 5000/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {h5.setVisibility(View.VISIBLE);}
            }, 5100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {h4.setVisibility(View.VISIBLE);}
            }, 5200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {h3.setVisibility(View.VISIBLE);}
            }, 5300/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {i3.setVisibility(View.VISIBLE);}
            }, 5400/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {j3.setVisibility(View.VISIBLE);}
            }, 5500/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {k3.setVisibility(View.VISIBLE);}
            }, 5600/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {k4.setVisibility(View.VISIBLE);}
            }, 5700/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {k5.setVisibility(View.VISIBLE);}
            }, 5800/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {l3.setVisibility(View.VISIBLE);}
            }, 5900/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {m4.setVisibility(View.VISIBLE);}
            }, 6000/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {n5.setVisibility(View.VISIBLE);}
            }, 6100/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {n4.setVisibility(View.VISIBLE);}
            }, 6200/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {n3.setVisibility(View.VISIBLE);}
            }, 6300/2);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {m3.setVisibility(View.VISIBLE);}
            }, 6400/2);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}