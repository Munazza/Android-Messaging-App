package com.example.user.wazzup;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.backendless.messaging.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by User on 12/07/2017.
 */

public class MessageAdapter extends BaseAdapter {
    private final Message[] messages;
    private final Context context;
    public MessageAdapter(Context context, Message [] msgs){
        this.messages = msgs;
        this.context = context;
    }
    @Override//how many Views to add
    public int getCount() {
        return messages.length;
    }
    @Override//return the raw Item object by index
    public Message getItem(int i) {//in our case Item is String
        return messages[i];
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
        if(getItem(i).getContent().contains("https://api.backendless.com"))
        {
            ImageView s = new ImageView(recycledView.getContext());
            try {
                s = getimg(s,getItem(i).getContent());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return s;
        }
        if(!getItem(i).getSender().toString().equals("")) {
            ((TextView) recycledView).setText(getItem(i).getSender().toString() + "\n" + getItem(i).getContent().toString());
        }
        else
        {
            ((TextView) recycledView).setText(getItem(i).getContent().toString());
        }
        //return to caller
        return recycledView;
    }

    public ImageView getimg(final ImageView v, final String url) throws InterruptedException {
        Thread t;
        t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myurl = new URL(url);
                    Bitmap bmp = BitmapFactory.decodeStream(myurl.openConnection().getInputStream());
                    v.setImageBitmap(bmp);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        t.start();
        t.join();
        return v;
    }
}
