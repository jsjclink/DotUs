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
    float px, py; //이전 좌표
    float curOriginX, curOriginY;
    int[][] pixelArray;
    boolean[][] isSet;
    int color;
    boolean was_moving;

    public CustomView(Context context) {
        super(context);

        paint = new Paint();
        color = Color.RED;
        paint.setColor(color);

        pixelArray = new int[30][30];
        isSet = new boolean[30][30];

        curOriginX = 0f;
        curOriginY = 0f;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        for(int i = 0; i < 30; i++){
            for(int j = 0; j < 30; j++){
                int intOriginX = ((int)curOriginX)*100/100;
                int intOriginY = ((int)curOriginY)*100/100;
                if(isSet[i][j] == true) canvas.drawRect(-1*intOriginX + (float)i*100, -1*intOriginY + (float)j*100, -1*intOriginX + + (float)i*100 + 100,  -1*intOriginY + (float)j*100 + 100, paint);
            }
        }
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("width, height", Integer.toString(getWidth()) + ", " + getHeight());
        switch(event.getAction()){
            case MotionEvent.ACTION_UP:
                if(was_moving){
                    was_moving = false;
                    break;
                }
                Log.d("Action DOWN!!! x, y", Float.toString(event.getX()) + ", " + Float.toString(event.getY()));
                x = ((int)(curOriginX + event.getX()))/100;
                y = ((int)(curOriginY + event.getY()))/100;
                isSet[x][y] = true;
                pixelArray[x][y] = color;
                px = event.getX();
                py = event.getY();
                break;

            case MotionEvent.ACTION_DOWN:
                px = event.getX();
                py = event.getY();
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("ACTION MOVE!! x, y", Float.toString(event.getX()) + ", " + Float.toString(event.getY()));
                float dx, dy;
                dx = px - event.getX();
                dy = py - event.getY();
                px = event.getX();
                py = event.getY();
                curOriginX += dx;
                if(curOriginX < 0f) curOriginX = 0f;
                //오른쪽 경계도 해줘
                curOriginY += dy;
                if(curOriginY < 0f) curOriginY = 0f;
                //아래쪽 경계도 해줘
                Log.d("originx, originy", curOriginX + ", " + curOriginY);
                was_moving = true;
                break;

        }
        invalidate();

        return true;
    }
}
