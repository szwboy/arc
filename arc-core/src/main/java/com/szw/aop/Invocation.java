package com.szw.aop;

/**
 * �����ʵ���Ǵ���ľ��������ڵ�������ݣ���Ϊ��̬�Ĵ������Ƭ�������е�ʱ�����в����������������������ݵ�
 * ��invocation����ľ��������ڵ�������ݡ�����joinpoit����Ǹ�method�Ļ�����invocation
 * ��Ҫ����ľ��Ƿ����Ĳ���
 * @author sunzhongwei
 *
 */
public interface Invocation extends Joinpoint {

	Object[] getArguments();
}
