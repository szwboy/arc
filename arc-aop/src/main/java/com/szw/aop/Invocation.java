package com.szw.aop;

/**
 * 这个其实就是代表的就是运行期的相关数据，因为静态的代码分子片段在运行的时候是有参数或者其他的运行期数据的
 * 而invocation代表的就是运行期的相关数据。例如joinpoit如果是个method的话，则invocation
 * 主要代表的就是方法的参数
 * @author sunzhongwei
 *
 */
public interface Invocation extends Joinpoint {

	Object[] getArguments();
}
