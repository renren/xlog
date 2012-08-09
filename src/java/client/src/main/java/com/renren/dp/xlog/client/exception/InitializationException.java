package com.renren.dp.xlog.client.exception;

public class InitializationException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public InitializationException(String msg){
		super(msg);
	}
	
	public InitializationException(String msg,Throwable cause){
		super(msg,cause);
	}
	
	public InitializationException(Throwable cause){
		super(cause);
	}
}
