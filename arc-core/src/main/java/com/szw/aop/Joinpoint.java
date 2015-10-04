package com.szw.aop;

import java.lang.reflect.AccessibleObject;

/**
 * 事实上而言,joinpoint代表的是这样一个概念：代表了类中的一个分子片段（其实就是一个方法 ，构造方法或者属性），
 * 所以就是accessibleObject的子类。
 * @author sunzhongwei
 *
 */
public interface Joinpoint {

	Object getThis();
	
	Object proceed() throws Throwable;
	
	AccessibleObject getStaticPart();
}
