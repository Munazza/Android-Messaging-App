package com.example.user.wazzup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.BackendlessCallback;

/**
 * Created by User on 30/06/2017.
 */

public class RegisterActivity extends Activity
{
    Button register;
    EditText usr,pss;
    Handler h;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Backendless.initApp(this, BackendlessSettings.APPLICATION_ID, BackendlessSettings.ANDROID_SECRET_KEY, BackendlessSettings.VERSION);
        usr = (EditText) findViewById(R.id.user);
        pss = (EditText) findViewById(R.id.pass);
        register = (Button)findViewById(R.id.Register);
        h= new Handler(getMainLooper());
    }

    public void addUser(View v)
    {
        BackendlessUser user = new BackendlessUser();
        user.setProperty("name",usr.getText().toString());
        user.setPassword(pss.getText().toString());

        Backendless.UserService.register( user, new BackendlessCallback<BackendlessUser>()
        {
            public void handleResponse( final BackendlessUser backendlessUser )
            {
                h.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegisterActivity.this, "Registration for "+ backendlessUser.getProperty("name") + " completed successfully", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } );
        returnMain(v);
    }

    public void returnMain(View v)
    {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
    }
}
