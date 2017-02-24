/*
 * ÆğÅİÅÅĞòËã·¨
 */

package dsa;

public class Sorter_Bubblesort implements Sorter {
	private Comparator C;

	public Sorter_Bubblesort()
	{	this(new ComparatorDefault()); }

	public Sorter_Bubblesort(Comparator comp)
	{	C = comp; }

	public void sort(Sequence S) {
		int n = S.getSize();
		for (int i=0; i<n; i++)
		for (int j=0; j<n-i-1; j++)
			if (0 < C.compare(S.getAtRank(j), S.getAtRank(j+1))) {
				Object temp = S.getAtRank(j);
				S.replaceAtRank(j, S.getAtRank(j+1));
				S.replaceAtRank(j+1, temp);
			}
	}
}