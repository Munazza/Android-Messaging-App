package com.example.user.wazzup;

import com.backendless.push.BackendlessBroadcastReceiver;
import com.backendless.push.BackendlessPushService;

public class MyPushReceiver extends BackendlessBroadcastReceiver
{
    public Class getServiceClass()
    {
        return MyPushService.class;
    }
}
