package arc.aop.autoproxy;

public class ProxyConfig {

	/*identifier to identify whether to make the proxy implements the advised*/
	private boolean opaque;
	
	/*identifier to identify whether to expose the proxy*/
	private boolean exposeProxy;
	
	/*identifier to identify whether to proxy the class or proxy the interface*/
	private boolean proxyTargetClass;
	
	/*identifier to identify whether to allow change the advised after advised config completion*/
	private boolean frozen;

	public boolean isOpaque() {
		return opaque;
	}

	public void setOpaque(boolean opaque) {
		this.opaque = opaque;
	}

	public boolean isExposeProxy() {
		return exposeProxy;
	}

	public void setExposeProxy(boolean exposeProxy) {
		this.exposeProxy = exposeProxy;
	}

	public boolean isProxyTargetClass() {
		return proxyTargetClass;
	}

	public void setProxyTargetClass(boolean proxyTargetClass) {
		this.proxyTargetClass = proxyTargetClass;
	}

	public boolean isFrozen() {
		return frozen;
	}

	public void setFrozen(boolean frozen) {
		this.frozen = frozen;
	}
	
}
