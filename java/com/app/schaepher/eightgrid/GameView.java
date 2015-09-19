package com.app.schaepher.eightgrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by Schaepher on 2015/9/13 0013.
 */
class GameView extends View
{
    /**
     * LEVEL 等级，这里设置为3，即3*3表
     * size 整个界面的长宽
     * cellSize 每个网格的长宽
     */
    private int LEVEL;
    private int top = 0, left = 0;

    private MyStateArray stateArray;
    private MyArrayHandler myArrayHandler;
    private MyAutoRun autoRun;

    private Point size;
    private Point cellSize;

    private Paint rectPaint;
    private Paint textPaint;

    private Canvas canvas;


    public GameView(Context context)
    {
        super(context);
        cellSize = new Point(0, 0);
        size = new Point(0, 0);
        stateArray = new MyStateArray();
        canvas = new Canvas();
        myArrayHandler = new MyArrayHandler();
        autoRun = new MyAutoRun();

        rectPaint = new Paint();
        rectPaint.setColor(Color.BLACK);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(10);

        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(80);

    }

    @Override
    public void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawColor(Color.parseColor("#1C86EE"));
        this.canvas = canvas;
        drawGame();
        winOrNot();
    }

    public void setSizeAndLevel(Point size, int level)
    {
        this.size = new Point(size);
        setLevel(level);
    }

    public void setLevel(int level)
    {
        LEVEL = level;
        cellSize.x = size.x / level;
        cellSize.y = size.y / level;
        myArrayHandler.setLevel(level);
        autoRun.setArrayList(myArrayHandler.getStateArray());
    }


    public void drawGame()
    {
        stateArray = myArrayHandler.getStateArray();
        for (int y = 0; y < LEVEL; y++)
        {
            for (int x = 0; x < LEVEL; x++)
            {
                String text = String.valueOf(stateArray.get(x + y * LEVEL));
                canvas.drawRect(left, top, left + cellSize.x, top + cellSize.y, rectPaint);
                if (!text.equals("0"))
                {
                    canvas.drawText(text, left + cellSize.x / 2, top + cellSize.y / 2, textPaint);
                }
                left += cellSize.x;
            }
            left = 0;
            top += cellSize.y;
        }
        top = 0;
    }

    //处理点击事件
    public boolean dispatchTouchEvent(MotionEvent event)
    {
        if (event.getAction() == MotionEvent.ACTION_DOWN)
        {
            int x = (int) event.getX() / cellSize.x;
            int y = (int) event.getY() / cellSize.y;
            int index = x + y * LEVEL;
            myArrayHandler.touchIndex(index, x, y);
        }
        invalidate();
        return true;
    }

    private void winOrNot()
    {
        //如果不在位数为0，即成功
        if (myArrayHandler.getWrongNumber() == 0)
        {
            Paint text = new Paint();
            text.setAntiAlias(true);
            text.setColor(Color.YELLOW);
            text.setTextSize(50);
            text.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Well done!!!", size.x / 2, size.y / 2, text);
        }
    }

    public void startAutoRun()
    {
        autoRun.setContext(getContext());
        autoRun.startAutoRun();
    }

    public void drawOneStepOfResult()
    {
        MyStateArray tempNumbers = autoRun.getResultFirst();
        if (tempNumbers == null)
        {
            tempNumbers = myArrayHandler.getStateArray();
        }
        myArrayHandler.updateArray(tempNumbers);
        autoRun.removeResultFirst();
        invalidate();
    }
}
