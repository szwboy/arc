package com.szw.aop;

import java.lang.reflect.AccessibleObject;

/**
 * ��ʵ�϶���,joinpoint�����������һ��������������е�һ������Ƭ�Σ���ʵ����һ������ �����췽���������ԣ���
 * ���Ծ���accessibleObject�����ࡣ
 * @author sunzhongwei
 *
 */
public interface Joinpoint {

	Object getThis();
	
	Object proceed() throws Throwable;
	
	AccessibleObject getStaticPart();
}
