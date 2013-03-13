import java.io.FileNotFoundException;


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
		System.out.println(parser.la.kind);
				
	}

}
