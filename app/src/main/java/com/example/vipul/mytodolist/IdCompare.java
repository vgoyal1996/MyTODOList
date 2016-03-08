package com.example.vipul.mytodolist;


import java.util.Comparator;

public class IdCompare implements Comparator<MyObject> {
    @Override
    public int compare(MyObject lhs, MyObject rhs) {
        if(lhs.getId()<rhs.getId())
            return 1;
        else if(lhs.getId()>rhs.getId())
            return -1;
        else
            return 0;
    }
}
