package com.arc;

import com.ai.omframe.order.ivalues.IOVOrderCustomer;

public class OrderRequest implements Request<IOVOrderCustomer> {

	private long rType;
	private IOVOrderCustomer oCustomer;
	
	public OrderRequest(long rType, IOVOrderCustomer oCustomer){
		this.rType= rType;
		this.oCustomer= oCustomer;
	}
	
	@Override
	public IOVOrderCustomer get() {
		return oCustomer;
	}

	@Override
	public long getRequestType() {
		return rType;
	}

}
