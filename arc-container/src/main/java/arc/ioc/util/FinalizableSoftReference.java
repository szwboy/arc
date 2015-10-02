package arc.ioc.util;

import java.lang.ref.SoftReference;

public abstract class FinalizableSoftReference<T> extends SoftReference<T> implements FinalizableReference{
	
	public FinalizableSoftReference(T referent) {
		super(referent, FinalizableReferenceQueue.getInstance());
	}
	
}
