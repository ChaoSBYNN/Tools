/*
 * 有序词典接口
 */

package dsa;

public interface SortedDictionary extends Dictionary {
//若词典非空，则返回其中关键码最小的条目；否则，返回null
	public Entry first();

//若词典非空，则返回其中关键码最大的条目；否则，返回null
	public Entry last();

//返回由关键码不小于key的条目依非降序组成的迭代器
	public Iterator successors(Object key);

//返回由关键码不大于key的条目依非升序组成的迭代器
	public Iterator predecessors(Object key);
}