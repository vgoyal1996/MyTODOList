package com.dudeonfireandCO.vipul.mytodolist;


import java.util.Comparator;

public class PriorityCompare implements Comparator<MyObject> {
    @Override
    public int compare(MyObject lhs, MyObject rhs) {
        if(lhs.getPrior()>rhs.getPrior())
            return -1;
        else if(lhs.getPrior()<rhs.getPrior())
            return 1;
        else
            return 0;
    }
}
