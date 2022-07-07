package com.example.dotus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CustomView extends View {
    Paint paint;
    int x, y;
    int[][] pixelArray;
    boolean[][] isSet;
    int color;

    public CustomView(Context context) {
        super(context);

        paint = new Paint();
        color = Color.RED;
        paint.setColor(color);

        pixelArray = new int[10][10];
        isSet = new boolean[10][10];

    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d("int x int y", Integer.toString(x) + ", " + Integer.toString(y));
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if(isSet[i][j] == true) canvas.drawRect((float)i*100, (float)j*100, (float)i*100 + 100, (float)j*100 + 100, paint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("width, height", Integer.toString(getWidth()) + ", " + getHeight());
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                Log.d("x, y", Float.toString(event.getX()) + ", " + Float.toString(event.getY()));
                x = ((int) event.getX())/100;
                y = ((int) event.getY())/100;
                isSet[x][y] = true;
                pixelArray[x][y] = color;

            case MotionEvent.ACTION_MOVE:
                Log.d("x, y", Float.toString(event.getX()) + ", " + Float.toString(event.getY()));
        }
        invalidate();

        return true;
    }
}
