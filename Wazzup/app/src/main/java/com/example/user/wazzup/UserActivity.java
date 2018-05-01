package com.example.user.wazzup;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.messaging.Message;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 02/07/2017.
 */

public class UserActivity extends AppCompatActivity
{
    ListView chatlist;
    String username,userid;
    ProgressBar p;
    ArrayList<String> chatnames = new ArrayList<>();
    int i = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getSupportActionBar().setTitle(getIntent().getStringExtra("uname"));
        username = getIntent().getStringExtra("uname");
        userid = getIntent().getStringExtra("uid");
        chatlist = (ListView) findViewById(R.id.chatlist);
        p = (ProgressBar)findViewById(R.id.pbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadChats();
        chatlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(UserActivity.this, chatlist.getAdapter().getItem(position).toString(), Toast.LENGTH_SHORT).show();
                Intent i = new Intent(UserActivity.this,ChatActivity.class);
                i.putExtra("name",chatlist.getAdapter().getItem(position).toString());
                i.putExtra("uid",userid);

                startActivity(i);
            }
        });
    }

    public void loadChats()
    {
        p.setVisibility(View.VISIBLE);
        ObjectAnimator animation = ObjectAnimator.ofInt(p, "progress", 0, 500);
        animation.setDuration(5000);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();
        String whereclause = "userID = '"+userid+"'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereclause);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption( "created DESC" );
        query.setQueryOptions( queryOptions );
        Backendless.Persistence.of("Chatters").find(query, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {final List<Map> vals = mapBackendlessCollection.getData();
                for (Map map : vals) {
                    final String chatID = map.get("chatID").toString();
                    Backendless.Data.of("Chats").findById(chatID, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map map) {
                            if(!chatnames.contains(map.get("name").toString())) {
                                chatnames.add(0, map.get("name").toString());
                            }
                            i++;
                            if (i == vals.size()) {
                                i = 0;
                                p.setVisibility(View.GONE);
                                chatlist.setAdapter(new ChatAdapter(UserActivity.this, chatnames.toArray(new String[chatnames.size()])));
                            }
                        }
                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(UserActivity.this, "failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    public void addChat(View v)
    {
        Intent i = new Intent(this,AddChatActivity.class);
        i.putExtra("uname",username);
        i.putExtra("uid",userid);
        startActivity(i);
    }
}
