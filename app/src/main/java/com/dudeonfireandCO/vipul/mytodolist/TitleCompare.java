package com.dudeonfireandCO.vipul.mytodolist;


import java.util.Comparator;

public class TitleCompare implements Comparator<MyObject> {
    @Override
    public int compare(MyObject lhs, MyObject rhs) {
        return lhs.getTask().compareToIgnoreCase(rhs.getTask());
    }
}
