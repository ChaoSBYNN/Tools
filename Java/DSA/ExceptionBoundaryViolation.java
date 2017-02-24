/*
 * 当作为参数的数组下标、向量的秩或列表的位置越界时，本意外将被抛出
 */

package dsa;

public class ExceptionBoundaryViolation extends RuntimeException {
	public ExceptionBoundaryViolation(String err) {
		super(err);
	}
}
