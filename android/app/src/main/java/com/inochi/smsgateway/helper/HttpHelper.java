package com.inochi.smsgateway.helper;

import android.os.StrictMode;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class HttpHelper {
	public HttpHelper(){
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public String getJson(String url) {
		String strBody = "";
		try {
			Document document = Jsoup.connect(url).ignoreContentType(true).get();
			strBody = document.body().html();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return strBody;
	}

}
