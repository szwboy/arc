package arc.ioc.util;

import java.lang.ref.WeakReference;

public abstract class FinalizableWeakReference<T> extends WeakReference<T>
		implements FinalizableReference {

	public FinalizableWeakReference(T referent){
		super(referent, FinalizableReferenceQueue.getInstance());
	}

}
