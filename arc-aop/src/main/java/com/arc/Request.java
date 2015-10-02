package com.arc;


public interface Request<T> {

	T get();
	
	long getRequestType();
}
