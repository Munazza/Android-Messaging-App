package com.example.user.wazzup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 07/07/2017.
 */

public class AddChatActivity extends AppCompatActivity {
    EditText chatname,adduser;
    TextView added;
    Handler h;
    ArrayList<String> usersid = new ArrayList<String>();
    String chatid;
    int userindex = 1;
    int counter = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addchat);
        getSupportActionBar().setTitle(getIntent().getStringExtra("uname"));
        chatname = (EditText)findViewById(R.id.chatname);
        adduser = (EditText)findViewById(R.id.newuser);
        added = (TextView)findViewById(R.id.added);
        added.setText(getIntent().getStringExtra("uname")+"(Maker)");
        h = new Handler(getMainLooper());
        usersid.add(0,getIntent().getStringExtra("uid"));
    }

    public void adduser(View v)
    {
        if(adduser.getText().toString().isEmpty())
        {
            Toast.makeText(this, "No user name was entered", Toast.LENGTH_SHORT).show();
        }
        else if(added.getText().toString().contains(adduser.getText().toString()))
        {
            Toast.makeText(this, "user was already added", Toast.LENGTH_SHORT).show();
        }
        else
        {
            String whereclause = "name = '"+adduser.getText().toString().trim()+"'";
            BackendlessDataQuery dataQuery = new BackendlessDataQuery();
            dataQuery.setWhereClause( whereclause );
            Backendless.Data.of( BackendlessUser.class ).find(dataQuery, new AsyncCallback<BackendlessCollection<BackendlessUser>>() {
                @Override
                public void handleResponse(final BackendlessCollection<BackendlessUser> backendlessUserBackendlessCollection) {
                    h.post(new Runnable() {
                        @Override
                        public void run() {
                            List<BackendlessUser> list = backendlessUserBackendlessCollection.getCurrentPage();
                            BackendlessUser[] a = list.toArray(new BackendlessUser[1]);
                            if(a[0] != null) {
                                usersid.add(userindex, a[0].getUserId());
                                userindex++;
                                Toast.makeText(AddChatActivity.this, "User was added", Toast.LENGTH_SHORT).show();
                                added.setText(added.getText() + "," + adduser.getText().toString().trim());
                                adduser.setText("");
                            }
                            else
                            {
                                Toast.makeText(AddChatActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(AddChatActivity.this, "User not found", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void createChat(View v)
    {
        if (chatname.getText().toString() == "")
        {
            Toast.makeText(this, "Must add a chat name", Toast.LENGTH_SHORT).show();
        }
        else if(usersid.size() == 1)
        {
            Toast.makeText(this, "Add more users before you create a chat!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            HashMap chat = new HashMap();
            chat.put("name",chatname.getText().toString());
            Backendless.Data.of( "Chats" ).save( chat, new AsyncCallback<Map>() {
                public void handleResponse( Map response )
                {
                    Toast.makeText(AddChatActivity.this, "Chat was created", Toast.LENGTH_LONG).show();
                    chatid = response.get("objectId").toString();
                    for(String id:usersid)
                    {
                        HashMap chatter = new HashMap();
                        chatter.put("userID",id);
                        chatter.put("chatID",chatid);
                        Backendless.Data.of( "Chatters" ).save( chatter, new AsyncCallback<Map>() {
                            public void handleResponse( Map response )
                            {
                                if(counter==usersid.size()-1) {
                                    Toast.makeText(AddChatActivity.this, "Chat was added to list", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                                counter++;
                            }

                            public void handleFault( BackendlessFault fault )
                            {
                                Toast.makeText(AddChatActivity.this, "Error at chatters", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }

                public void handleFault( BackendlessFault fault )
                {
                    Toast.makeText(AddChatActivity.this, "Chat wasn't created", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
