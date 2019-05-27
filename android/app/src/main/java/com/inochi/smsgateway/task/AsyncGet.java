package com.inochi.smsgateway.task;

import android.os.AsyncTask;

import com.inochi.smsgateway.helper.HttpHelper;
import com.inochi.smsgateway.listener.GetListener;

public class AsyncGet extends AsyncTask<String, Integer, String> {
    private GetListener delegate;

    public AsyncGet() {

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            String url = params[0];
            HttpHelper httpHelper = new HttpHelper();
            result = httpHelper.getJson(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null){
            if (delegate != null)
                delegate.onGetResult(result);
        }
    }

    public void setDelegate(GetListener delegate) {
        this.delegate = delegate;
    }
}
