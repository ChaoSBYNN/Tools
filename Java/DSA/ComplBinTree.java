/*
 * 完全二叉树接口
 */

package dsa;

public interface ComplBinTree extends BinTree {
//生成并返回一个存放e的外部节点，该节点成为新的末节点
	public BinTreePosition addLast(Object e);

//删除末节点，并返回其中存放的内容
	public Object delLast();

//返回按照层次遍历编号为i的节点的位置，0 <= i < size()
	public BinTreePosition posOfNode(int i);
}