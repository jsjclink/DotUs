package com.example.dotus;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class PaintActivity extends AppCompatActivity {
    PaintView paintView;
    FrameLayout stage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.paint);

        initView();
        initListener();

        paintView = new PaintView(this);
        stage.addView(paintView);
    }

    private void initView() {
        stage = findViewById(R.id.stage);
    }
    private void initListener() {
    }
}
