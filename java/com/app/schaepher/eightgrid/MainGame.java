/*
 *  Copyright [c] 2015 By ChenShiFa All Rights Reserved.
 */
package com.app.schaepher.eightgrid;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

public class MainGame extends Activity
{
    /**
     * gameView 游戏视图
     * size 游戏视图的长和宽
     * firstFocusChanged 保证只有在onCreate后的第一次调用才有效
     */
    private LinearLayout linearLayout;
    private GameView gameView;
    private Point size;
    private boolean firstFocusChanged = true;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        linearLayout = (LinearLayout) findViewById(R.id.gameLayout);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        super.onWindowFocusChanged(hasFocus);

        if (firstFocusChanged)
        {
            firstFocusChanged = false;
            gameView = new GameView(this);
            linearLayout.addView(gameView);
            size = new Point();
            size.x = linearLayout.getWidth();
            size.y = linearLayout.getHeight();
            gameView.setSizeAndLevel(size, 3);

            SharedPreferences settings = getSharedPreferences("settings", Activity.MODE_PRIVATE);

            if (settings.getBoolean("firstLaunch", true))
            {
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("firstLaunch", false);
                editor.apply();

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("提示：");
                builder.setMessage("1.按“音量键上”打乱数字\n" +
                        "2.按“菜单键”自动搜索\n" +
                        "3.不断按“音量键下”将逐步显示结果");
                builder.setPositiveButton("确定", null);
                builder.show();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_MENU)
        {
            gameView.startAutoRun();

            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            gameView.drawOneStepOfResult();
            return true;
        }
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            gameView.setLevel(3);
            gameView.invalidate();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    // 完全退出程序
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        System.exit(0);
    }
}
