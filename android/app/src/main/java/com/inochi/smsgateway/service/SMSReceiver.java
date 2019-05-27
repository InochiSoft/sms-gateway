package com.inochi.smsgateway.service;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class SMSReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) action = "";

        if (!action.isEmpty()){
            Intent service = new Intent(context, SMSService.class);
            service.setAction(action);
            service.putExtras(intent);
            startWakefulService(context, service);
        }
    }
}
