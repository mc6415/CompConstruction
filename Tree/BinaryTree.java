
public class BinaryTree {

	 public String data;
	 public BinaryTree left;
	 public BinaryTree right;

	 public BinaryTree(String tree) {
	        data = tree;
	        left = null;
	        right = null;
	 }

	public String preOrderTraversal() {
	      String s = data+" ";
	      if (left!=null)
	         s = s + left.preOrderTraversal();
	      if (right!=null)
	         s = s + right.preOrderTraversal();
	      return s;
	}
	
}
