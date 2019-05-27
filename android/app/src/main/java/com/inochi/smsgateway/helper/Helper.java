package com.inochi.smsgateway.helper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;

import com.inochi.smsgateway.item.SMSItem;
import com.inochi.smsgateway.listener.GetListener;
import com.inochi.smsgateway.service.SMSReceiver;
import com.inochi.smsgateway.task.AsyncGet;

public class Helper {
    private Context context;
    private BundleSettings bundleSettings;
    private GetListener delegate;

    public Helper(Context context){
        this.context = context;
        this.bundleSettings = new BundleSettings(context);
        this.delegate = (GetListener) context;
    }

    public void ping(){
        try {
            String ipServer = bundleSettings.getIPServer();
            String urlConnect = "http://" + ipServer + "/sms-gateway/api/ping";

            AsyncGet asyncGet = new AsyncGet();
            asyncGet.setDelegate(delegate);
            asyncGet.execute(urlConnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readOutbox(){
        try {
            String ipServer = bundleSettings.getIPServer();
            String urlConnect = "http://" + ipServer + "/sms-gateway/api/sms/read";

            AsyncGet asyncGet = new AsyncGet();
            asyncGet.setDelegate(delegate);
            asyncGet.execute(urlConnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOutbox(int id){
        try {
            String ipServer = bundleSettings.getIPServer();
            String urlConnect = "http://" + ipServer + "/sms-gateway/api/sms/update/" + id;

            AsyncGet asyncGet = new AsyncGet();
            asyncGet.setDelegate(delegate);
            asyncGet.execute(urlConnect);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendSMS(SMSItem smsItem) {
        try {
            String phone = smsItem.getReceiver();
            int id = smsItem.getId();
            if (phone != null){
                if (!phone.isEmpty()){
                    String message = smsItem.getMessage();
                    if (message != null){
                        if (!message.isEmpty()){
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(phone, null, message, null, null);

                            Intent intent = new Intent(context, SMSReceiver.class);
                            intent.setAction(Constants.Action.SHOW_NOTIFY);

                            Bundle args = new Bundle();
                            args.putInt(Constants.Setting.NOTIF_ID, 1000 + id);
                            args.putString(Constants.Setting.NOTIF_TITLE, "SMS Send to " + phone);
                            args.putString(Constants.Setting.NOTIF_TEXT, "Message: " + message);
                            intent.putExtras(args);
                            context.sendBroadcast(intent);

                            updateOutbox(id);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setDelegate(GetListener delegate) {
        this.delegate = delegate;
    }
}
