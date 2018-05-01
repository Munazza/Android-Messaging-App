package com.example.user.wazzup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class LoginActivity extends Activity {

    Button login,register;
    EditText usr,pss;
    String[] chatlist;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank);
        Backendless.initApp(this, BackendlessSettings.APPLICATION_ID, BackendlessSettings.ANDROID_SECRET_KEY, BackendlessSettings.VERSION);
        if(!Backendless.UserService.loggedInUser().isEmpty())
        {
            String currentUserId = Backendless.UserService.loggedInUser();
            Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
                @Override
                public void handleResponse(BackendlessUser backendlessUser) {

                    Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                    intent.putExtra("uname", backendlessUser.getProperty("name").toString());
                    intent.putExtra("uid",backendlessUser.getUserId().toString());
                    startActivity(intent);
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {
                    Toast.makeText(LoginActivity.this, "bad", Toast.LENGTH_LONG).show();
                }
            });
        }
        else {
            setContentView(R.layout.activity_login);
            usr = (EditText) findViewById(R.id.user);
            pss = (EditText) findViewById(R.id.pass);
            login = (Button) findViewById(R.id.Login);
            register = (Button) findViewById(R.id.Register);
        }
    }

    public void register(View v)
    {
        Intent i = new Intent(this, RegisterActivity.class);
        startActivity(i);
    }

    public void login(View v)
    {
        Backendless.UserService.login(usr.getText().toString(), pss.getText().toString(), new AsyncCallback<BackendlessUser>()
        {
            public void handleResponse( BackendlessUser user )
            {
                Toast.makeText(LoginActivity.this, String.format("Welcome " + user.getProperty("name")), Toast.LENGTH_LONG).show();
                AsyncCallback<Boolean> isValidLoginCallback = new AsyncCallback<Boolean>() {
                    public void handleResponse(Boolean response) {

                        System.out.println("[ASYNC] Is login valid? - " + response);

                        Toast.makeText(LoginActivity.this, String.format("[ASYNC] Is login valid? - " + response), Toast.LENGTH_LONG).show();

                    }

                    public void handleFault(BackendlessFault fault)

                    {

                        System.err.println("Error - " + fault);

                        Toast.makeText(LoginActivity.this, String.format("Error - " + fault), Toast.LENGTH_LONG).show();

                    }

                };
                Backendless.UserService.isValidLogin(isValidLoginCallback);
                Intent intent = new Intent(LoginActivity.this, UserActivity.class);
                intent.putExtra("uname", user.getProperty("name").toString());
                intent.putExtra("uid",user.getUserId().toString());
                startActivity(intent);
            }

            public void handleFault( BackendlessFault fault )
            {
                Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_LONG).show();
            }
        },true);
    }
}
