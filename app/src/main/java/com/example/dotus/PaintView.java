package com.example.dotus;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class PaintView extends View {
    Paint rectPaint;
    float px, py, curOriginX, curOriginY;
    int[] pixelArray; boolean[] pixelSet;
    int color = Color.BLUE; //기본 색
    long timeMilli;
    int pixelWidth = 100; int pixelHeight = 100;
    int scale = 20;
    float sensitivity = 0.1f;
    int targetX, targetY;
    Context context;

    public PaintView(Context context) {
        super(context);
        this.context = context;

        rectPaint = new Paint();
        rectPaint.setColor(color);

        px = 0f; py = 0f; curOriginX = 0f; curOriginY = 0f;

        pixelArray = new int[pixelWidth *pixelHeight];
        pixelSet = new boolean[pixelWidth *pixelHeight];

        Log.d("width, height", Integer.toString(getWidth()) + ", " + getHeight());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int intOriginX = ((int)curOriginX)/scale*scale;
        int intOriginY = ((int)curOriginY)/scale*scale;

        //drawTargetPicker(canvas);

        for(int i = 0; i < pixelWidth; i++){
            for(int j = 0; j < pixelHeight; j++){
                if(pixelSet[j*pixelHeight + i]) {
                    rectPaint.setColor(pixelArray[j * pixelHeight + i]);
                    canvas.drawRect(
                            -1 * intOriginX + (float) i * scale,
                            -1 * intOriginY + (float) j * scale,
                            -1 * intOriginX + (float) i * scale + scale,
                            -1 * intOriginY + (float) j * scale + scale,
                            rectPaint
                    );
                }
            }
        }
        rectPaint.setColor(color);
        super.onDraw(canvas);
    }

    private void drawTargetPicker(Canvas canvas) {
        int targetX = ((int)getWidth())/scale/2;
        int targetY = ((int)getHeight())/scale/2;
        canvas.drawRect(
                (float)targetX*scale,
                (float)targetY*scale,
                (float)targetX*scale + scale,
                (float)targetY*scale + scale,
                rectPaint
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
                px = event.getX();
                py = event.getY();
                timeMilli = System.currentTimeMillis();
                break;
            case MotionEvent.ACTION_UP:
                px = event.getX();
                py = event.getY();

                long curMilli = System.currentTimeMillis();
                long timeInterval = curMilli - timeMilli;
                timeMilli = curMilli;
                if(timeInterval > 150) break;

                int x = ((int)(curOriginX + event.getX()))/scale;
                int y = ((int)(curOriginY+ event.getY()))/scale;
                if(x < pixelWidth && y < pixelHeight) { //0아래론 화면이 안넘어감
                    pixelSet[y*pixelHeight + x] = true;
                    pixelArray[y*pixelHeight + x] = color;
                    PaintActivity paintActivity = (PaintActivity) context;
                    System.out.println("pixel changed called in paintView");
                    paintActivity.pixelChanged(y*pixelHeight + x, color);
                }
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = px - event.getX();
                float dy = py - event.getY();
                curOriginX += dx*sensitivity;
                if(curOriginX < 0f) curOriginX = 0f;
                curOriginY += dy*sensitivity;
                if(curOriginY < 0f) curOriginY = 0f;
                break;
        }
        invalidate();
        return true;
    }
    public void initPixelInfo(int[] array, int width, int height){
        this.pixelWidth = width;
        this.pixelHeight = height;
        this.pixelArray = array.clone();
        this.pixelSet = new boolean[width * height];
        for(int i = 0; i < pixelWidth; i++){
            for(int j = 0; j < pixelHeight; j++){
                this.pixelSet[j*pixelHeight + i] = true;
            }
        }
        invalidate();
    }
    public void changePixelArray(int index, int color){
        this.pixelArray[index] = color;
        invalidate();
    }
    public void setColor(int color){
        this.color = color;
    }
    public void sizeUp(){
        this.scale += 5;
        invalidate();
    }
    public void sizeDown(){
        this.scale -= 5;
        if(this.scale <= 0) this.scale = 5;
        invalidate();
    }
}
