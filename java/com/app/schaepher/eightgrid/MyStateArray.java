package com.app.schaepher.eightgrid;

import java.util.ArrayList;

/**
 * Created by Schaepher on 2015/9/13 0013.
 */
public class MyStateArray extends ArrayList<Integer>
{
    /**
     * fatherArray 记录其父节点
     * wrongNumber 不在位的个数
     * cost 相当于h(n)
     * depth 深度
     * direction 0移动的方向
     */
    private MyStateArray fatherArray;
    private int wrongNumber = 0;
    private int cost = 0;
    private int depth = -1;
    private int direction = 0;

    public MyStateArray()
    {
        super();
    }

    public void setFatherArray(MyStateArray myStateArray)
    {
        fatherArray = myStateArray;
    }

    public MyStateArray getFatherArray()
    {
        return fatherArray;
    }

    public void setAllCost(int cost)
    {
        this.cost = cost;
    }

    public void setWrongNumber(int wrongNumber)
    {
        this.wrongNumber = wrongNumber;
    }

    public int getWrongNumber()
    {
        return wrongNumber;
    }

    public int getAllCost()
    {
        return cost;
    }

    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    public int getDepth()
    {
        return depth;
    }

    public void depthPlusPlus()
    {
        depth++;
    }

    public void setDirection(int direction)
    {
        this.direction = direction;
    }

    public int getDirection()
    {
        return direction;
    }
}
