package com.lihao.smartsoap;

import java.util.concurrent.Future;

import com.dzyd.auto_office_application.MyApplication;

public class RequestQueue {

	private volatile static RequestQueue mQueue;

	public static RequestQueue getQueue() {
		if (mQueue == null) {
			synchronized (RequestQueue.class) {		
				if (mQueue == null) {
					mQueue = new RequestQueue();
				}
			}
		}
		return mQueue;
	}

	public void add(Request request) {
		Future<Boolean> result = MyApplication.threadPool.submit(request);
		request.setTask(result);
	}

}
