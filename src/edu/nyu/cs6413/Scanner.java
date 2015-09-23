package edu.nyu.cs6413;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PushbackReader;

/**
 * @author lxp19
 *
 */
/**
 * @author lxp19
 *
 */
public class Scanner {
	
	private static final char EndOfFile = '\0';
		
	private int lineNumber;
	
	private final String fileName;
	
	//lookAhead is always the next character of the current one
	private char lookAhead;
	
	private PushbackReader inFile;
	
	private boolean eof;
	
	private enum tokenType {
		tokword, tokop, toknumber
	}
	
	private String getUserInput() {
		try (java.util.Scanner input  = new java.util.Scanner(System.in)) {
			return input.nextLine();
		}
	}
	
	public Scanner() {
		lineNumber = 1;
		System.out.println("Please enter file name:");
		fileName = getUserInput();
		try {
			inFile = new PushbackReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.out.println("Cannot find file " + fileName);
		}
		lookAhead = firstChar();
		while (!eof) 
			gettoken();
		System.out.println("Completed");
	}
	
	private char firstChar() {
		char c = '0';
		boolean goodChar = false;
				
		if (eof)
			return EndOfFile;
		
		while (!goodChar) {
			//Skip the white space until we find one or reach the eof
			while (!eof && isSpace(c = getChar()))
				;
			if (c != '{')
				goodChar = true;
			else 
				//Skip the comment
				while (!eof && (c = getChar()) != '}')
					;	
		}
		
		if (eof)
			return EndOfFile;
		else 
			return c;
	}
	
	
	//Fetch a character from a file and adjust the line number when necessary
	private char getChar() {
		//Must declare as int, because -1 in char is 65535
		int c = '\0';
		try {
			c = inFile.read();
		} catch (IOException e) {
			System.out.println("Error occurred when trying to read characters");
			e.printStackTrace();
		}
		if (c == -1) {
			eof = true;
			return EndOfFile;
		} else if (c == '\n' || c == '\r') {
			lineNumber++;
		}
		return (char) c;	
	}
	private void unGetChar(char c) {
		try {
			inFile.unread(c);
		} catch (IOException e) {
			System.out.println("Error occurred when trying to push characters back to stream");
			e.printStackTrace();
		}
		if (c == '\n' || c == '\r')
			lineNumber--;
	}
	
	private boolean isSpace(int i) {
		return i == ' ' || i == '\t' || i == '\r' || i == '\n';
	}
	
	private tokenType gettoken() {
		char c;
		if ((c = lookAhead) == EndOfFile)
			return null;
		
		
		lookAhead = getChar();
		if (isAlpha(c))
			return scanWord(c);
		else if (isDigit(c))
			return scanNum(c);
		else 
			return scanOp(c);
			
	}
	
	private tokenType scanOp(char c) {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(c);
		unGetChar(lookAhead);
		lookAhead = firstChar();
		System.out.printf("%-15s %s\n", lexeme, tokenType.tokop);
		return tokenType.tokop;
	}
	
	private tokenType scanNum(char c) {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(c);
		while ((c = lookAhead) != EndOfFile && isDigit(c)) {
			lexeme.append(c);
			lookAhead = getChar();
		}
		
		//fraction part
		if (c == '.') {
			lookAhead = getChar();
			lexeme.append(c);
			while ((c = lookAhead) != EndOfFile && isDigit(c)) {
				lexeme.append(c);
				lookAhead = getChar();
			}
		}
		
		//exponent part
		if (c == 'E') {
			lookAhead = getChar();
			lexeme.append(c);
			while ((c = lookAhead) != EndOfFile && (isDigit(c) || isSign(c))) {
				lexeme.append(c);
				lookAhead = getChar();
			}
		}
		
		unGetChar(lookAhead);
		lookAhead = firstChar();
		System.out.printf("%-15s %s\n", lexeme, tokenType.toknumber);
		return tokenType.toknumber;
	}
	
	private tokenType scanWord(char c) {
		StringBuilder lexeme = new StringBuilder();
		lexeme.append(c);
		while ((c = lookAhead) != EndOfFile && (isDigit(c) || isAlpha(c))){
			lexeme.append(c);
			lookAhead = getChar();
		}
		
		unGetChar(lookAhead);
		lookAhead = firstChar();
		System.out.printf("%-15s %s\n", lexeme, tokenType.tokword);
		return tokenType.tokword;
		
	}
	
	private boolean isSign(char c) {
		return c == '+' || c == '-';
	}

	private boolean isDigit(char c) {
		return Character.isDigit(c);
	}
	
	private boolean isAlpha(char c) {
		return Character.isAlphabetic(c);
	}
	
	public static void main(String[] args) throws IOException {
		Scanner scan = new Scanner();
	}
}
