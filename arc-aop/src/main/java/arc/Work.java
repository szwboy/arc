package com.arc;

public interface Work<T> {

	boolean support(long reqType);
	
	T service(Request<?> request);
}
