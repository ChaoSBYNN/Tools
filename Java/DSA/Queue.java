/*
 * 队列接口
 */

package dsa;

public interface Queue {
	public int getSize();//返回队列中元素数目
	public boolean isEmpty();//判断队列是否为空
	public Object front()//取队首元素（但不删除）
	throws ExceptionQueueEmpty;
	public void enqueue (Object obj)
	throws ExceptionQueueFull;//入队
	public Object dequeue()//出队
	throws ExceptionQueueEmpty;
	public void Traversal();//遍历
}