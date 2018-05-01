package com.example.user.wazzup;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.Subscription;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.messaging.DeliveryOptions;
import com.backendless.messaging.Message;
import com.backendless.messaging.PublishOptions;
import com.backendless.messaging.PushBroadcastMask;
import com.backendless.messaging.SubscriptionOptions;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.persistence.QueryOptions;
import com.backendless.services.messaging.MessageStatus;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.media.MediaRecorder.VideoSource.CAMERA;

/**
 * Created by User on 11/07/2017.
 */

public class ChatActivity extends AppCompatActivity {

    TextView chatters;
    ListView messagelist;
    EditText msgtxt;
    String chatname;
    String userid;
    int imsg = 0;
    File imgFile;
    public final int CAMERA = 1, WRITE = 2;
    List<com.example.user.wazzup.Message> chatmessages = new ArrayList<>();
    Subscription sub;
    SubscriptionOptions so = new SubscriptionOptions();
    String msg;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getSupportActionBar().setTitle(getIntent().getStringExtra("name"));
        chatname = getIntent().getStringExtra("name");
        userid = getIntent().getStringExtra("uid");
        messagelist = (ListView)findViewById(R.id.messages);
        msgtxt = (EditText)findViewById(R.id.msgtext);
        chatRegister();
        loadMessages();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int a = 0;
        this.requestPermissions(new String[]{Manifest.permission.CAMERA},a);
    }

    public void chatRegister()
    {
        so.setSelector("chat='"+chatname+"'");
        so.setSubscriberId(userid);
        Backendless.Messaging.registerDevice("823846805005", "default", new AsyncCallback<Void>() {
            @Override
            public void handleResponse(Void aVoid) {
                Backendless.Messaging.subscribe("default", new AsyncCallback<List<Message>>() {
                    @Override
                    public void handleResponse(final List<Message> messages) {

                        Backendless.UserService.findById(messages.get(0).getPublisherId().toString(), new AsyncCallback<BackendlessUser>() {
                            @Override
                            public void handleResponse(BackendlessUser backendlessUser) {
                                if(!backendlessUser.getUserId().toString().equals(userid)) {
                                    chatmessages.add(new com.example.user.wazzup.Message(backendlessUser.getProperty("name").toString(), messages.get(0).getData().toString()));
                                }
                                else
                                {
                                    chatmessages.add(new com.example.user.wazzup.Message("", messages.get(0).getData().toString()));
                                }
                                messagelist.setAdapter(new MessageAdapter(ChatActivity.this, chatmessages.toArray(new com.example.user.wazzup.Message[chatmessages.size()])));
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {

                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(ChatActivity.this, "don`t have any message", Toast.LENGTH_LONG).show();
                    }
                },so, new AsyncCallback<Subscription>() {
                    @Override
                    public void handleResponse(Subscription subscription) {
                        Toast.makeText(ChatActivity.this, "listening to default", Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        Toast.makeText(ChatActivity.this, "does not listen to channel", Toast.LENGTH_LONG).show();

                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                Toast.makeText(ChatActivity.this, "Error: Can't use chat", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendmsg(View v)
    {
        if(msgtxt.getText().toString() != "") {
            PublishOptions po = new PublishOptions();
            po.putHeader("chat",chatname);
            po.putHeader( "android-ticker-text", "You just got a push notification!" );
            po.putHeader( "android-content-title", "You have a new message" );
            po.putHeader( "android-content-text", msgtxt.getText().toString() );
            po.setPublisherId(getIntent().getStringExtra("uid"));
            DeliveryOptions dO = new DeliveryOptions();
            dO.setPushBroadcast( PushBroadcastMask.ANDROID | PushBroadcastMask.IOS );
            Backendless.Messaging.publish("default",msgtxt.getText().toString(), po, dO,new AsyncCallback<MessageStatus>() {
                @Override
                public void handleResponse(MessageStatus messageStatus) {
                    Toast.makeText(ChatActivity.this, "sent", Toast.LENGTH_SHORT).show();
                    HashMap msg = new HashMap();
                    msg.put("data",msgtxt.getText().toString());
                    msg.put("chat",chatname);
                    Backendless.Data.of("Messages").save(msg, new AsyncCallback<Map>() {
                        @Override
                        public void handleResponse(Map map) {
                            Toast.makeText(ChatActivity.this, "message saved", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {
                            Toast.makeText(ChatActivity.this, "message failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                    msgtxt.setText("");
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(ChatActivity.this, "not sent", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public void loadMessages()
        {
        String whereclause = "chat='"+chatname+"'";
        BackendlessDataQuery query = new BackendlessDataQuery();
        query.setWhereClause(whereclause);
        QueryOptions queryOptions = new QueryOptions();
        queryOptions.addSortByOption( "created ASC" );
        query.setQueryOptions( queryOptions );
        Backendless.Persistence.of("Messages").find(query, new AsyncCallback<BackendlessCollection<Map>>() {
            @Override
            public void handleResponse(BackendlessCollection<Map> mapBackendlessCollection) {final List<Map> vals = mapBackendlessCollection.getData();
                for (Map map : vals) {
                    final String data = map.get("data").toString();
                    String userID = map.get("ownerId").toString();
                    Backendless.UserService.findById(userID, new AsyncCallback<BackendlessUser>() {
                        @Override
                        public void handleResponse(BackendlessUser usr) {
                            if(!usr.getUserId().toString().equals(userid)) {
                                chatmessages.add(0,new com.example.user.wazzup.Message(usr.getProperty("name").toString(), data));
                            }
                            else
                            {
                                chatmessages.add(0,new com.example.user.wazzup.Message("", data));
                            }
                            imsg++;
                            if (imsg == vals.size()) {
                                imsg = 0;
                                //p.setVisibility(View.GONE);
                                messagelist.setAdapter(new MessageAdapter(ChatActivity.this, chatmessages.toArray(new com.example.user.wazzup.Message[chatmessages.size()])));
                            }
                        }

                        @Override
                        public void handleFault(BackendlessFault backendlessFault) {

                        }
                    });
                }
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }
    public void attach(View v) {
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        tempFile();//create temporary File container for img - and pass to camera
        i.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imgFile));
        int permissionCheck = ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.WRITE_CALENDAR);
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)== PackageManager.PERMISSION_GRANTED) {
            startActivityForResult(i, CAMERA);
        }
    }

    private void tempFile() {
        String fileName = "IMG_" + Math.random() + System.currentTimeMillis() + ".jpg";
        imgFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent i) {
        super.onActivityResult(requestCode, resultCode, i);
        if (resultCode == RESULT_OK && requestCode == CAMERA) {
            msg = imgFile.getAbsolutePath();
            //Backendless.UserService.findById(userid, new AsyncCallback<BackendlessUser>() {
            //    @Override
            //    public void handleResponse(BackendlessUser backendlessUser) {
            //        if(!backendlessUser.getUserId().equals(userid)) {
            //            chatmessages.add(new com.example.user.wazzup.Message(backendlessUser.getProperty("name").toString(), msg));
            //        }
            //        else
            //        {
            //            chatmessages.add(new com.example.user.wazzup.Message("", msg));
            //        }
            //        messagelist.setAdapter(new MessageAdapter(ChatActivity.this, chatmessages.toArray(new com.example.user.wazzup.Message[chatmessages.size()])));
            //
            //    }
//
            //    @Override
            //    public void handleFault(BackendlessFault backendlessFault) {
//
            //    }
            //});
            saveImg();
        }
    }

    public void saveImg() {
        Backendless.Files.upload(imgFile, "/tzach/", new AsyncCallback<BackendlessFile>() {
            public void handleResponse(BackendlessFile res) {//success handling
                final String url = res.getFileURL();
                //save in Data - messages with given file
                Map msg = new HashMap();
                msg.put("data", url);
                msg.put("chat", chatname);
                Backendless.Data.of("Messages").save(msg, new AsyncCallback<Map>() {
                    public void handleResponse(Map res) {
                        Toast.makeText(ChatActivity.this, "image saved", Toast.LENGTH_LONG).show();
                        msgtxt.setText(url);
                    }

                    public void handleFault(BackendlessFault e) {
                        Toast.makeText(ChatActivity.this, e.getCode(), Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void handleFault(BackendlessFault e) {//error handling
                Log.e("File upload", "FAILED with error " + e.getCode());
            }
        });
    }
}
