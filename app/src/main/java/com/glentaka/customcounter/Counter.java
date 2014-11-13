package com.glentaka.customcounter;

import android.app.ActionBar;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by gtakahashi on 11/9/14.
 */
public class Counter {
    private String name;
    private List<Date> clicks;

    public Counter(String name) {
        this.name = name;
        this.clicks = new ArrayList<Date>();
    }

    public String getText() {
        return name + "\nCount: " + clicks.size();
    }

    public void setName(String name) {
        this.name = name;
    }

    public void click() {
        clicks.add(new Date());
    }

    public String getName() {
        return name;
    }

    public List<Date> getClicks() {
        return clicks;
    }

    public void reset() {
        this.clicks = new ArrayList<Date>();
    }
}
