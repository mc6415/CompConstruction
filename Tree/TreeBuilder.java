import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;


public class TreeBuilder {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String inFileName = args[0];
		String outfilename = args[1];
		
		Scanner scanner = new Scanner(inFileName);
		Parser parser = new Parser(scanner);
		
		parser.Parse();
		String[] countries = new String[parser.countries.size()];
		countries = parser.countries.toArray(countries);
		printtree(countries);
	}
	
	public static void printtree(String[] tree){
		BinaryTree t = new BinaryTree(tree[0]);
		for(int i=1;i<tree.length;i++)
		{
			if (t.left == null){
				BinaryTree leftsub = new BinaryTree(tree[i]);
				t.left = leftsub;
			}
			else{
				BinaryTree rightsub = new BinaryTree(tree[i]);
				t.right = rightsub;
			}
					
		}
	}
	
	
class node
{
	String key;
	node left;
	node right;
	node(){
		key = null;
		left = null;
		right = null;
	}
	
	node(String key)
	{
		this.key = key;
		left = null;
		right = null;
	}
}

class Tree
{
	node root;
	Tree(){
		root = null;
	}
	
	void put(String key){
		node current = root;
		node prev = current;
		if(root == null){
			root = new node(key);
		}
		else{
			boolean insert = false;
			while(insert == false)
			{
				if(current.left == null)
				{
					current.left = new node(key);
				}
				else{
					if(current.right == null){
						current.right = new node(key);
						insert = true;
					}
					current = current.right;
				}
			}
		}
	}
	
}
	

}


