/*  MicroJava Parser (HM 06-12-28)
    ================
*/
package MJ;

import java.util.*;
//import MJ.SymTab.*;
//import MJ.CodeGen.*;

public class Parser {
	private static final int  // token codes
		none      = 0,
		ident     = 1,
		number    = 2,
		charCon   = 3,
		plus      = 4,
		minus     = 5,
		times     = 6,
		slash     = 7,
		rem       = 8,
		eql       = 9,
		neq       = 10,
		lss       = 11,
		leq       = 12,
		gtr       = 13,
		geq       = 14,
		assign    = 15,
		semicolon = 16,
		comma     = 17,
		period    = 18,
		lpar      = 19,
		rpar      = 20,
		lbrack    = 21,
		rbrack    = 22,
		lbrace    = 23,
		rbrace    = 24,
		class_    = 25,
		else_     = 26,
		final_    = 27,
		if_       = 28,
		new_      = 29,
		print_    = 30,
		program_  = 31,
		read_     = 32,
		return_   = 33,
		void_     = 34,
		while_    = 35,
		eof       = 36;
	private static final String[] name = { // token names for error messages
		"none", "identifier", "number", "char constant", "+", "-", "*", "/", "%",
		"==", "!=", "<", "<=", ">", ">=", "=", ";", ",", ".", "(", ")",
		"[", "]", "{", "}", "class", "else", "final", "if", "new", "print",
		"program", "read", "return", "void", "while", "eof"
		};

	private static Token t;			// current token (recently recognized)
	private static Token la;		// lookahead token
	private static int sym;			// always contains la.kind
	public  static int errors;  // error counter
	private static int errDist;	// no. of correctly recognized tokens since last error

	private static BitSet exprStart, statStart, statSeqFollow, declStart, declFollow;

	//------------------- auxiliary methods ----------------------
	private static void scan() {
		t = la;
		la = Scanner.next();
		sym = la.kind;
		errDist++;
		/*
		System.out.print("line " + la.line + ", col " + la.col + ": " + name[sym]);
		if (sym == ident) System.out.print(" (" + la.string + ")");
		if (sym == number || sym == charCon) System.out.print(" (" + la.val + ")");
		System.out.println();*/
	}

	private static void check(int expected) {
		if (sym == expected) scan();
		else error(name[expected] + " expected");
	}

	public static void error(String msg) { // syntactic error at token la
		if (errDist >= 3) {
			System.out.println("-- line " + la.line + " col " + la.col + ": " + msg);
			errors++;
		}
		errDist = 0;
		//System.exit(1);
	}

	//-------------- parsing methods (in alphabetical order) -----------------

	// Program = "program" ident {ConstDecl | ClassDecl | VarDecl} '{' {MethodDecl} '}'.
	private static void Program() {
		check(program_);
		check(ident);
		
		for (;;) {
			if (sym == final_) 
				ConstDecl();
			else if (sym == ident)
				VarDecl();
			else if (sym == class_) 
				ClassDecl();
			else 
				break;
		}
		
		check(lbrace);
		while (sym == void_ || sym == ident)
			MethodDecl();
		
		check(rbrace);
	}
	
	// ConstDecl = ConstDecl = "final" Type ident "=" (number | charConst) ";".
	private static void ConstDecl()
	{
		check(final_);
		//Need to add Type here
		Type();
		check(ident);
		check(assign);
		//Add (number | charConst)
		if(sym == number)
			check(number);
		else if(sym == charCon)
			check(charCon);
		else
			error("Contant malformed");
		check(semicolon);
	}
	
	// VarDecl = Type ident {"," ident } ";".
	// Type = ident ["[" "]"].
	private static void VarDecl()
	{
		Type();
		for(;;)
		{
			check(ident);
			if(sym == comma)scan();
			else
				break;
		}
		check(semicolon);
	}
	
	// ClassDecl = "class" ident "{" {VarDecl} "}".
	private static void ClassDecl()
	{
		check(class_);
		check(ident);
		check(lbrace);
		while (sym == class_)
			VarDecl();
		check(rbrace);
	}
	
	// MethodDecl = (Type | "void") ident "(" [FormPars] ")" {VarDecl} Block.
	// Type = ident ["[" "]"].
	private static void MethodDecl()
	{
		if(sym == ident) Type();
		else if(sym == void_) check(void_);
		else error("Not a valid method declaration!");
		check(ident);
		check(lpar);
		if(sym == ident)
			FormPars();
		check(rpar);
		
	}
	
	// Type = ident [ "["  "]" ].
	private static void Type()
	{
		check(ident);
		if(sym == lbrack)
		{
			scan();
			check(rbrack);
		}
	}

	//FormPars = Type ident  {"," Type ident}.
	private static void FormPars()
	{
		for(;;)
		{
			Type();
			check(ident);
			if (sym == comma)
				scan();
			else
				break;
		}
	}
	
	// Mulop = "*" | "/" | "%"
	private static void Mulop()
	{
		if(sym == times)scan();
		else if(sym == slash)scan();
		else if(sym == rem)scan();
		else error("Not a valid multiplication");
	}
	
	// Addop = "+" | "-".
	private static void Addop()
	{
		if(sym == plus)scan();
		else if(sym == minus)scan();
		else error("Not a valid addition/subtraction");
	}
	
	// Designator = ident {("." ident) | ("[" Expr "]")}.
	private static void Designator()
	{
		check(ident);
		for(;;){
			if(sym == period)
				check(ident);
			else if(sym == lbrack){
				Expr();
				check(rbrack);
			}
			else
				error("Malformed Designation");
			break;
		}
	}
	
	public static void Term()
	{
		
	}
	
	// Expr = ["-"] Term {Addop Term}.
	public static void Expr()
	{
		if(sym == minus)scan();
		Term();
		
	}
	
	// Factor = Designator [ActPars]
	// | number
	// | charConst
	// | "new" ident ["[" Expr "]"]
	// | "(" Expr ")".
	public static void Factor()
	{
		if(sym == number)scan();
		else if(sym == charCon)scan();
		else if(sym == new_){
			check(ident);
			if(sym == lbrack){
				Expr();
				check(rbrack);
			}
		}
		else if(sym == lpar){
			Expr();
			check(rpar);
		}
		else if(sym == ident){
			if(sym == lpar)ActPars();
		}
		else error("Illegal character where factor should be");
	}
	
	public static void ActPars()
	{
		 	
	}
	
	public static void parse() {
		// initialize symbol sets
		BitSet s;
		s = new BitSet(64); exprStart = s;
		s.set(ident); s.set(number); s.set(charCon); s.set(new_); s.set(lpar); s.set(minus);

		s = new BitSet(64); statStart = s;
		s.set(ident); s.set(if_); s.set(while_); s.set(read_);
		s.set(return_); s.set(print_); s.set(lbrace); s.set(semicolon);

		s = new BitSet(64); statSeqFollow = s;
		s.set(rbrace); s.set(eof);

		s = new BitSet(64); declStart = s;
		s.set(final_); s.set(ident); s.set(class_);

		s = new BitSet(64); declFollow = s;
		s.set(lbrace); s.set(void_); s.set(eof);

		// start parsing
		errors = 0; errDist = 3;
		scan();
		Program();
		if (sym != eof) error("end of file found before end of program");
	}

}








