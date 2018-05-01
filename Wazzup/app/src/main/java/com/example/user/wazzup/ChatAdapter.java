package com.example.user.wazzup;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by User on 02/07/2017.
 */

public class ChatAdapter extends BaseAdapter {
    private final String [] chats;
    private final Context context;
    public ChatAdapter(Context context, String [] chats){
        this.chats = chats;
        this.context = context;
    }
    @Override//how many Views to add
    public int getCount() {
        return chats.length;
    }
    @Override//return the raw Item object by index
    public String getItem(int i) {//in our case Item is String
        return chats[i];
    }
    @Override//custom id for each item
    public long getItemId(int i) {//in our case index is actual ID
        return i;
    }
    @Override//called .getCount() times - to create each View
    public View getView(int i, View recycledView, ViewGroup parent) {
        //not created yet - create it
        if(recycledView==null) recycledView = new TextView(context);
        //assign to reused OR new object
        ((TextView)recycledView).setTextSize(30);
        ((TextView)recycledView).setText(getItem(i));
        //return to caller
        return recycledView;
    }
}
