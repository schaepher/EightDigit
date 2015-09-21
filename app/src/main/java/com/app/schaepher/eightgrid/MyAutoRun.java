package com.app.schaepher.eightgrid;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

/**
 * Created by Schaepher on 2015/9/13 0013.
 */
public class MyAutoRun
{
    /**
     * openLists OPEN表
     * closedLists CLOSED表
     * resultLists 保存自动搜索的结果的表
     * arrayHandler 用来处理MyStateArray对象
     * tempArray 临时数组
     */
    private LinkedList<MyStateArray> openLists;
    private LinkedList<MyStateArray> closedLists;
    private LinkedList<MyStateArray> resultLists;
    private MyArrayHandler arrayHandler;
    private MyStateArray tempArray;
    private Context context;
    private ProgressDialog progressDialog;

    public MyAutoRun()
    {
        openLists = new LinkedList<>();
        closedLists = new LinkedList<>();
        resultLists = new LinkedList<>();
        arrayHandler = new MyArrayHandler();
        tempArray = new MyStateArray();
    }

    // 给arrayHandler里的stateArray赋值
    public void setArrayList(MyStateArray array)
    {
        arrayHandler.updateArray(array);
        arrayHandler.updateParams();
    }

    //开始自动搜索
    public void startAutoRun()
    {
        openLists.clear();
        closedLists.clear();
        arrayHandler.getStateArray().setFatherArray(null);

        openLists.addLast(arrayHandler.getStateArray());
        MyTask task = new MyTask();
        task.execute();
    }

    //放到新线程里进行后台处理
    private class MyTask extends AsyncTask<Void, Void, Void>
    {
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("正在搜索解...");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params)
        {
            while (openLists.getFirst().getWrongNumber() != 0)
            {
                tempArray = openLists.getFirst();
                closedLists.addLast(tempArray);
                openLists.removeFirst();
                for (int index = 0; index < 4; index++)
                {
                    arrayHandler.updateArray(tempArray);
                    arrayHandler.nextStep(index);
                    arrayHandler.updateParams();
                    arrayHandler.getStateArray().setFatherArray(tempArray);
                    if (!alreadyExists(arrayHandler.getStateArray()))
                    {
                        openLists.addLast(arrayHandler.getStateArray());
                    }
                }
                sortOpenLists();
            }
            closedLists.addLast(openLists.getFirst());
            updateResultLists();
            return null;
        }

        @Override
        protected void onPostExecute(Void avoid)
        {
            super.onPostExecute(avoid);
            progressDialog.dismiss();
            Toast.makeText(context, "成功找到解！", Toast.LENGTH_SHORT).show();
        }

    }

    //是否已经在OPEN或CLOSED表中
    public boolean alreadyExists(MyStateArray myStateArray)
    {
        boolean exists = false;
        for (MyStateArray array : openLists)
        {
            for (int index = 0; index < myStateArray.size(); index++)
            {
                if (array.equals(myStateArray))
                {
                    exists = true;
                    break;
                }
            }
            if (exists)
            {
                break;
            }
        }

        if (!closedLists.isEmpty() || !exists)
        {
            for (MyStateArray array : closedLists)
            {
                for (int index = 0; index < myStateArray.size(); index++)
                {

                    if (array.equals(myStateArray))
                    {
                        exists = true;
                        break;
                    }
                }
                if (exists)
                {
                    break;
                }
            }
        }
        return exists;
    }


    public void sortOpenLists()
    {
        if (openLists.size() > 1)
        {
            Collections.sort(openLists, new SortByCost());
        }
    }

    class SortByCost implements Comparator<MyStateArray>
    {
        @Override
        public int compare(MyStateArray lhs, MyStateArray rhs)
        {
            //从小到大 lhs > rhs, return 1表示交换
            if (lhs.getAllCost() >= rhs.getAllCost())
            {
                if (lhs.getAllCost() == rhs.getAllCost())
                {
                    //深度优先
                    if (lhs.getDepth() < rhs.getDepth())
                    {
                        return 1;
                    }
                }
                else
                {
                    return 1;
                }

            }
            return -1;
        }
    }

    //只获取解路径，但仍保存所有过程，过程在closedLists中
    public void updateResultLists()
    {
        resultLists.clear();
        resultLists.addFirst(closedLists.getLast());
        MyStateArray fatherArray = closedLists.getLast().getFatherArray();
        while (fatherArray != null)
        {
            resultLists.addFirst(fatherArray);
            fatherArray = fatherArray.getFatherArray();
        }
        resultLists.removeFirst();
    }

    public void setContext(Context context)
    {
        this.context = context;
    }

    public MyStateArray getResultFirst()
    {
        if (resultLists.size() != 0)
        {
            return resultLists.getFirst();
        }
        return null;
    }

    public void removeResultFirst()
    {
        if (resultLists.size() != 0)
        {
            resultLists.removeFirst();
        }
    }

}
