/*
 * 优先队列接口
 */

package dsa;

public interface PQueue {
//统计优先队列的规模
	public int getSize();

//判断优先队列是否为空
	public boolean isEmpty();

//若Q非空，则返回其中的最小条目（并不删除）;否则，报错
	public Entry getMin() throws ExceptionPQueueEmpty;

//将对象obj与关键码k合成一个条目，将其插入Q中，并返回该条目
	public Entry insert(Object key, Object obj) throws ExceptionKeyInvalid;

//若Q非空，则从其中摘除关键码最小的条目，并返回该条目；否则，报错
	public Entry delMin() throws ExceptionPQueueEmpty;
}