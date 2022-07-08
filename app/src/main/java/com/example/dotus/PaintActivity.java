package com.example.dotus;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class PaintActivity extends AppCompatActivity {
    CustomView customView;
    FrameLayout stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);

        initView();
        initListener();

        customView = new CustomView(this);
        stage.addView(customView);
    }

    private void initView() {
        stage = findViewById(R.id.stage);
    }
    private void initListener() {
    }
}
