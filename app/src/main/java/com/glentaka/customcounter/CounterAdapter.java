package com.glentaka.customcounter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gtakahashi on 11/9/14.
 */
public class CounterAdapter extends BaseAdapter {
    private List<Counter> counters;
    private Context context;
    private LayoutInflater inflater;

    public CounterAdapter(Context context) {
        this.context = context;
        this.counters = new ArrayList<Counter>();
        this.inflater = LayoutInflater.from(context);
    }

    public void removeItem(int i) {
        counters.remove(i);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return counters.size();
    }

    @Override
    public Object getItem(int i) {
        return counters.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Button b = (Button) view;
        if (b == null) {
            b = new Button(viewGroup.getContext());
        }
        b.setFocusable(false);
        b.setFocusableInTouchMode(false);
        b.setClickable(false);
        b.setText(counters.get(i).getText());
        return b;
    }

    public void addCounter(String name) {
        counters.add(new Counter(name));
        this.notifyDataSetChanged();
    }

    public void reset() {
        for(Counter c : counters) {
            c.reset();
        }
        this.notifyDataSetChanged();
    }

    public List<Counter> getCounters() {
        return counters;
    }
}
