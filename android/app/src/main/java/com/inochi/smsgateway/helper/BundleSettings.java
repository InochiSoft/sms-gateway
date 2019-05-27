package com.inochi.smsgateway.helper;

import android.content.Context;

public class BundleSettings {
    private Context context;
    private Settings settings;

    public BundleSettings(Context context){
        this.context = context;
        this.settings = new Settings(context);
    }

    public int getBootPremission(){
        return settings.getIntSetting(Constants.Setting.BOOT_PERMISSION, 0);
    }

    public void setBootPremission(int value){
        settings.setIntSetting(Constants.Setting.BOOT_PERMISSION, value);
    }

    public String getIPServer(){
        return settings.getSetting(Constants.Setting.IP_SERVER, "");
    }

    public void setIPServer(String value){
        settings.setSetting(Constants.Setting.IP_SERVER, value);
    }

    public int getCheckDuration(){
        return settings.getIntSetting(Constants.Setting.CHECK_DURATION, 1);
    }

    public void setCheckDuration(int value){
        settings.setIntSetting(Constants.Setting.CHECK_DURATION, value);
    }

}
