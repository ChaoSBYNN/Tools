/*
 * 当试图对空队列应用front或dequeue方法时，本意外将被抛出
 */

package dsa;

public class ExceptionQueueEmpty extends RuntimeException {
	public ExceptionQueueEmpty(String err) {
		super(err);
	}
}
