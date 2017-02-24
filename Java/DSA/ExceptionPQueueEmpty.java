/*
 * 当试图对空的优先队列应用getMin()或delMin()方法时，本意外将被抛出
 */

package dsa;

public class ExceptionPQueueEmpty extends RuntimeException {
	public ExceptionPQueueEmpty(String err) {
		super(err);
	}
}
