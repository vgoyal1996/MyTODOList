package com.example.vipul.mytodolist;


import java.util.Comparator;

public class DateCompare implements Comparator<MyObject> {
    @Override
    public int compare(MyObject lhs, MyObject rhs) {
        return rhs.getModified().compareTo(lhs.getModified());
    }
}
