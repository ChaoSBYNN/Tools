/*
 * 比较器接口
 */

package dsa;

public interface Comparator {
	public int compare(Object a, Object b);//若a>（=或<）b，返回正数、零或负数
}