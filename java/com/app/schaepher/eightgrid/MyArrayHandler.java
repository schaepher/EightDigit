package com.app.schaepher.eightgrid;

import android.graphics.Point;

import java.util.Random;

/**
 * Created by Schaepher on 2015/9/14 0014.
 */
public class MyArrayHandler
{
    /**
     * LEVEL 即有多少行或者列
     * stateArray 记录当前状态
     * numberIntArray 默认的正确结果
     * zeroXY 0所在的坐标
     */
    private static final int LEFT = 0;
    private static final int UP = 1;
    private static final int RIGHT = 2;
    private static final int DOWN = 3;
    private static int LEVEL;
    private MyStateArray stateArray;
    private int[] numberIntArray = {1, 2, 3, 8, 0, 4, 7, 6, 5};
    private Point zeroXY;

    public MyArrayHandler()
    {
        zeroXY = new Point();
        stateArray = new MyStateArray();
    }

    public void setLevel(int level)
    {
        LEVEL = level;
        //清除上一次的状态
        stateArray.clear();
        int size = level * level;
        for (int index = 0; index < size; index++)
        {
            stateArray.add(numberIntArray[index]);
        }
        setZeroPosition();
        messNumbers();
    }

    // 打乱数字实际是让0随便走
    public void setZeroPosition()
    {
        for (int index = 0; index < stateArray.size(); index++)
        {
            if (stateArray.get(index) == 0)
            {
                zeroXY.x = index % LEVEL;
                zeroXY.y = index / LEVEL;
            }
        }
    }

    // 打乱数字
    public void messNumbers()
    {
        Random random = new Random();
        int randomTimes = LEVEL * 32;
        for (int index = 0; index < randomTimes; index++)
        {
            int direction = random.nextInt(4);
            nextStep(direction);
        }
        if (getWrongNumber() == 0)
        {
            messNumbers();
        }
    }

    //走下一步
    public void nextStep(int direction)
    {
        setZeroPosition();
        int zeroPosition = zeroXY.x + zeroXY.y * LEVEL;
        switch (direction)
        {
            case LEFT:
            {
                if (zeroXY.x > 0)
                {
                    swap(zeroPosition, zeroPosition - 1);
                    zeroXY.x -= 1;
                    stateArray.setDirection(direction);
                }
                break;
            }
            case UP:
            {
                if (zeroXY.y > 0)
                {
                    swap(zeroPosition, zeroPosition - LEVEL);
                    zeroXY.y -= 1;
                    stateArray.setDirection(direction);
                }
                break;
            }
            case RIGHT:
            {
                if (zeroXY.x < LEVEL - 1)
                {
                    swap(zeroPosition, zeroPosition + 1);
                    zeroXY.x += 1;
                    stateArray.setDirection(direction);
                }
                break;
            }
            case DOWN:
            {
                if (zeroXY.y < LEVEL - 1)
                {
                    swap(zeroPosition, zeroPosition + LEVEL);
                    zeroXY.y += 1;
                    stateArray.setDirection(direction);
                }
            }
        }
    }

    //交换数组中两个位置的值
    public void swap(int x, int y)
    {
        int temp = stateArray.get(x);
        stateArray.set(x, stateArray.get(y));
        stateArray.set(y, temp);
    }

    //不在位的个数
    public int getWrongNumber()
    {
        int wrongCount = 0;
        for (int index = 0; index < stateArray.size(); index++)
        {
            if (stateArray.get(index) != numberIntArray[index])
            {
                wrongCount++;
            }
        }
        return wrongCount;
    }

    //最优
    public int getAllCost()
    {
        int step = 0;
        int sN = 0;
        int stepTemp;
        //外层循环，逐一判断当前状态的数字 P(n)
        for (int indexNumber = 0; indexNumber < stateArray.size(); indexNumber++)
        {
            //内层循环，找到当前状态被选中的数字与其相符的位置的差距。
            for (int indexArray = 0; stateArray.get(indexNumber) != 0 && indexArray < numberIntArray.length; indexArray++)
            {
                if (stateArray.get(indexNumber) == numberIntArray[indexArray])
                {
                    stepTemp = Math.abs(indexNumber - indexArray);
                    step = step + stepTemp / LEVEL + stepTemp % LEVEL;

                    //以下是计算后继者是否相同 S(n)
                    //中间
                    if (indexNumber == (numberIntArray.length - 1) / 2)
                    {
                        if (stateArray.get(indexNumber) != 0)
                        {
                            sN++;
                        }
                    }
                    else
                    {
                        //非中间
                        if (indexArray == 0)
                        {   //左上角，向右+1
                            sN += stepCost(RIGHT, indexNumber, indexArray);
                        }
                        else if (indexArray == LEVEL - 1)
                        {
                            //右上角，向下+1
                            sN += stepCost(DOWN, indexNumber, indexArray);
                        }
                        else if (indexArray == numberIntArray.length - 1)
                        {
                            //右下角，向左-1
                            sN += stepCost(LEFT, indexNumber, indexArray);
                        }
                        else if (indexArray == numberIntArray.length - LEVEL)
                        {
                            //左下角，向上+1
                            sN += stepCost(UP, indexNumber, indexArray);
                        }
                        else
                        {
                            //只支持3*3
                            if (indexArray / LEVEL == 0)
                            {
                                //第一行，向右+1
                                sN += stepCost(RIGHT, indexNumber, indexArray);
                            }
                            else if (indexArray % LEVEL == LEVEL - 1)
                            {
                                sN += stepCost(DOWN, indexNumber, indexArray);
                            }
                            else if (indexArray / LEVEL == LEVEL - 1)
                            {
                                sN += stepCost(LEFT, indexNumber, indexArray);
                            }
                            else if (indexArray % LEVEL == 0)
                            {
                                sN += stepCost(UP, indexNumber, indexArray);
                            }
                        }
                    }
                    break;
                }
            }
        }
        return step + 3 * sN;
    }

    // 后续节点是否相同
    public int stepCost(int direction, int indexNumber, int indexArray)
    {
        int sN = 0;
        switch (direction)
        {
            //左
            case LEFT:
                if (!(indexNumber % LEVEL > 0 &&
                        stateArray.get(indexNumber - 1) == numberIntArray[indexArray - 1]))
                {
                    sN += 2;
                }
                break;
            //上
            case UP:
                if (!(indexNumber - LEVEL >= 0 &&
                        stateArray.get(indexNumber - LEVEL) == numberIntArray[indexArray - LEVEL]))
                {
                    sN += 2;
                }
                break;
            //右
            case RIGHT:
                if (!(indexNumber % LEVEL < LEVEL - 1 &&
                        stateArray.get(indexNumber + 1) == numberIntArray[indexArray + 1]))
                {
                    sN += 2;
                }
                break;
            //下
            case DOWN:
                if (!(indexNumber + LEVEL <= numberIntArray.length - 1 &&
                        stateArray.get(indexNumber + LEVEL) == numberIntArray[indexArray + LEVEL]))
                {
                    sN += 2;
                }
                break;
            default:
                break;
        }

        return sN;
    }

    // 根据触碰的坐标做出反馈
    public void touchIndex(int index, int x, int y)
    {
        if (stateArray.get(index) != 0)
        {

            if (x > 0 && stateArray.get(index - 1) == 0)
            {
                swap(index, index - 1);
            }
            else if (x < LEVEL - 1 && stateArray.get(index + 1) == 0)
            {
                swap(index, index + 1);
            }
            else if (y > 0 && stateArray.get(index - LEVEL) == 0)
            {
                swap(index, index - LEVEL);
            }
            else if (y < LEVEL - 1 && stateArray.get(index + LEVEL) == 0)
            {
                swap(index, index + LEVEL);
            }
        }
    }

    public void updateArray(MyStateArray array)
    {
        //如果不用clone，会改变实际的状态，因为传递的是指针
        stateArray = (MyStateArray) array.clone();
    }

    public MyStateArray getStateArray()
    {
        return stateArray;
    }

    public void updateParams()
    {
        stateArray.depthPlusPlus();
        int wrong = getAllCost();
        stateArray.setAllCost(wrong + stateArray.getDepth());
        stateArray.setWrongNumber(wrong);
    }

}
