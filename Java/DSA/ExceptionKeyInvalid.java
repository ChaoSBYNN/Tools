/*
 * 当试图使用非法关键码时，本意外将被抛出
 */

package dsa;

public class ExceptionKeyInvalid extends RuntimeException {
	public ExceptionKeyInvalid(String err) {
		super(err);
	}
}
