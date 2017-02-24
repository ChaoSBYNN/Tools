/*
 * 当试图对空树进行操作时，本意外将被抛出
 */

package dsa;

public class ExceptionTreeEmpty extends RuntimeException {
	public ExceptionTreeEmpty(String err) {
		super(err);
	}
}
