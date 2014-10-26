/**
 * The GTreeNode object represents a single node in the grammar tree. Its value
 * may be a nonTerminal or a terminal word
 * 
 * @author Dhruv
 *
 */
public class GTreeNode {

	GTreeNode left;
	GTreeNode right;
	String value;

	public GTreeNode(String value) {
		this.value = value;
	}
}
