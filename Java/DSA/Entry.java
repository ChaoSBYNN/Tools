/*
 * 条目接口
 */

package	dsa;

public interface Entry {
//取条目的关键码
	public Object getKey();
//修改条目的关键码，返回此前存放的关键码
	public Object setKey(Object k);

//取条目的数据对象
	public Object getValue();
//修改条目的数据对象，返回此前存放的数据对象
	public Object setValue(Object v);
}