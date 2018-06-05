package com.lyl.pkuhole.network;

public class Request<T> {
	
	private int code;
	private String msg;
	private T data;
	
	public boolean success() {
		return code == 0;
	}
	
	public String getErrorMsg() {
		return msg;
	}
	
	public T getData() {
		return data;
	}

}
