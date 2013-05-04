package com.bd17kaka.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;
import android.widget.EditText;

/**
 * @author bd17kaka
 * 概率查询Util
 */
public class LotterySearcherUtil {

	private EditText rsEditText = null;
	
	/**
	 * 将返回的数据流转换为字符串
	 * @param stream
	 * @param len
	 * @return
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 */
	private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
	    Reader reader = null;
	    reader = new InputStreamReader(stream, "UTF-8");        
	    char[] buffer = new char[len];
	    reader.read(buffer);
	    return new String(buffer);
	}

	
	/**
	 * 联网查询数据
	 * @param myurl
	 * @return
	 * @throws IOException
	 */
	private String downloadUrl(String myurl) throws IOException {
	    InputStream is = null;
	    // Only display the first 500 characters of the retrieved
	    // web page content.
	    int len = 500;
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);

	        // Starts the query
	        conn.connect();
	        is = conn.getInputStream();

	        // Convert the InputStream into a string
	        String contentAsString = readIt(is, len);
	        return contentAsString;
	        
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            is.close();
	        } 
	    }
	}

	
	/**
	 * @author bd17kaka
	 * 异步下载
	 */
	private class DownloadWebpageTask extends AsyncTask<String, Void, String> {

		// onPostExecute displays the results of the AsyncTask.
		@Override
		protected void onPostExecute(String result) {
			rsEditText.setText(result);
		}

		@Override
		protected String doInBackground(String... arg0) {
			try {
				return downloadUrl(arg0[0]);
			} catch (IOException e) {
				return "貌似出了点问题哦！";
			}
		}
	}
	
	/**
	 * 查询双色球红球的出现概率
	 * @param sshRedBalls 双色球红球序列，以空格隔开
	 * @param rsView 显示结果的控件
	 */
	public void getSSHProbability(String sshRedBalls, EditText rsEditText) {
		
		this.rsEditText = rsEditText;
		
		// url
		String url = "http://1120.pw/sshCalRedProbability.do?input=";
		
		// 将查询参数转换成以,分隔的球号序列
		String[] tokens = sshRedBalls.split(" ");
		if (tokens == null) {
			this.rsEditText.setText("不合法输入！");
			return;
		}
		String param = "";
		for (String item : tokens) {
			param += item + ",";
		}
		param = param.substring(0, param.length() - 1);
		
		// 执行查询
		new DownloadWebpageTask().execute(url + param);
	}
}
