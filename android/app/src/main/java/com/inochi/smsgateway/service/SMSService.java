package com.inochi.smsgateway.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;

import com.inochi.smsgateway.helper.BundleSettings;
import com.inochi.smsgateway.helper.Constants;
import com.inochi.smsgateway.helper.Helper;
import com.inochi.smsgateway.item.NotifItem;
import com.inochi.smsgateway.item.SMSItem;
import com.inochi.smsgateway.listener.GetListener;
import com.inochi.smsgateway.util.SMSNotification;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class SMSService extends Service implements GetListener {
    private volatile Looper mServiceLooper;
    private volatile ServiceHandler mServiceHandler;
    private String mName;

    private final class ServiceHandler extends Handler {
        ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
            stopSelf(msg.arg1);
        }
    }

    public SMSService() {
        super();
        mName = "MainService";
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("IntentService[" + mName + "]");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public void onHandleIntent(Intent intent){
        String action = intent.getAction();

        if (action == null) action = "";

        if (!action.isEmpty()){
            Bundle args = intent.getExtras();
            NotifItem notifItem = null;

            int notifId = 0;

            String notifTitle;
            String notifText;

            if (args != null){
                notifId = args.getInt(Constants.Setting.NOTIF_ID);
                notifTitle = args.getString(Constants.Setting.NOTIF_TITLE);
                notifText = args.getString(Constants.Setting.NOTIF_TEXT);
                if (notifId > 0){
                    notifItem = new NotifItem();
                    notifItem.setId(notifId);
                    notifItem.setTicker(notifTitle);
                    notifItem.setTitle(notifTitle);
                    notifItem.setMessage(notifText);
                }
            }

            switch (action){
                case "com.htc.intent.action.QUICKBOOT_POWERON":
                case "android.intent.action.QUICKBOOT_POWERON":
                case "android.intent.action.BOOT_COMPLETED":
                case Constants.Action.CREATE_DAILY:
                    enableService();
                    break;
                case Constants.Action.REMOVE_DAILY:
                    disableService();
                    stopSelf();
                    break;
                case Constants.Action.CHECK_OUTBOX:
                    checkOutbox();
                    break;
                case Constants.Action.SHOW_NOTIFY:
                    if (notifId > 0)
                        SMSNotification.notify(this, notifItem);
                    break;
                case Constants.Action.CLOSE_NOTIFY:
                    if (notifId > 0)
                        SMSNotification.cancel(this, notifId);
                    break;
            }
        }

        SMSReceiver.completeWakefulIntent(intent);
    }

    private void enableService(){
        BundleSettings bundleSettings = new BundleSettings(this);
        String ipServer = bundleSettings.getIPServer();
        int duration = bundleSettings.getCheckDuration();

        if (!ipServer.isEmpty()){
            Calendar calendar = Calendar.getInstance();

            AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, SMSReceiver.class);
            intent.setAction(Constants.Action.CHECK_OUTBOX);

            PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    duration * 60 * 1000, alarmIntent);

            PackageManager pm  = getPackageManager();
            ComponentName componentName = new ComponentName(this, SMSReceiver.class);
            pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);

            checkOutbox();
        }
    }

    private void disableService(){
        AlarmManager alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, SMSReceiver.class);
        intent.setAction(Constants.Action.CHECK_OUTBOX);

        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmMgr.cancel(alarmIntent);
    }

    private void checkOutbox(){
        Helper helper = new Helper(this);
        helper.readOutbox();
    }

    @Override
    public void onGetResult(String result) {
        if (result != null){
            if (!result.isEmpty()){
                try {
                    JSONArray jsonArray = new JSONArray(result);
                    if (jsonArray.length() > 0){
                        ArrayList<SMSItem> smsItems = new ArrayList<>();
                        for (int j = 0; j < jsonArray.length(); j++) {
                            JSONObject jsonItem = jsonArray.getJSONObject(j);
                            SMSItem smsItem = new SMSItem();
                            if (jsonItem.has("id")){
                                smsItem.setId(jsonItem.getInt("id"));
                            }
                            if (jsonItem.has("status")){
                                smsItem.setStatus(jsonItem.getInt("status"));
                            }
                            if (jsonItem.has("message")){
                                smsItem.setMessage(jsonItem.getString("message"));
                            }
                            if (jsonItem.has("receiver")){
                                smsItem.setReceiver(jsonItem.getString("receiver"));
                            }
                            smsItems.add(smsItem);
                        }

                        if (smsItems.size() > 0){
                            Helper helper = new Helper(this);
                            for (SMSItem smsItem : smsItems){
                                helper.sendSMS(smsItem);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
