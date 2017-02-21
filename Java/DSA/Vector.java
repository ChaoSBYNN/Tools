/*
 * 向量接口
 */

package dsa;

public interface Vector {
//返回向量中元素数目
	public int getSize();

//判断向量是否为空
	public boolean isEmpty();

//取秩为r的元素
	public Object getAtRank(int r)
	throws ExceptionBoundaryViolation;

//将秩为r的元素替换为obj
	public Object replaceAtRank(int r, Object obj)
	throws ExceptionBoundaryViolation;

//插入obj，作为秩为r的元素；返回该元素
	public Object insertAtRank(int r, Object obj)
	throws ExceptionBoundaryViolation;

//删除秩为r的元素
	public Object removeAtRank(int r)
	throws ExceptionBoundaryViolation;
}