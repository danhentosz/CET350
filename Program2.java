
/*
 * Program2.java
 * Daniel Hentosz and Nathaniel Dehart
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu
 * GROUP 7
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.io.IOException;

class Program2 {
	public static void main(String[] args) {
		IOFile o1 = new IOFile();
		BufferedReader get;
		StringTokenizer tk;
		String ionames[] = new String[3];
		String temp, temp2, temp3;
		int count = 0;
		int sum = 0;
		int i;
		PrintWriter say;
		Word words[] = new Word[100];
		// If they enter a valid input and output file;
		if (o1.get(args, ionames)) {
			try {
				get = new BufferedReader(new FileReader(ionames[0]));
				// Keep reading each line until the end of the input file.
				while ((temp = get.readLine()) != null) {
					// Creating a StringTokenizer object for each line
					tk = new StringTokenizer(temp, "`~!@#$%^&*()_=+[]{};:\",<.>/?\n\t\r ");
					// Read every token and assign a string to the token each time.
					while (tk.hasMoreTokens()) {
						temp3 = "0";
						temp2 = tk.nextToken().toLowerCase().toLowerCase();
						// Check rather the string has any valid characters("-----" and "'''''").
						if (o1.good(temp2)) {
							;// Making sure we can get the second character index.
							if (temp2.length() > 1) {
								// Getting rid of excessive leading '-' and "'"
								while (temp2.charAt(1) == '-' | temp2.charAt(0) == '\'') {
									temp2 = temp2.substring(1);
								}
							}
							try {
								// If its not a number, we will take care of it in our catch block.
								sum = sum + Integer.parseInt(temp2);
							} catch (NumberFormatException e) {
								// Now its either a word, or a number followed by a word(-123cat or 123cat).
								char c = temp2.charAt(0);
								// Seeing if its a negitive number followed by a word(-123cat).
								if (c == '-') {
									temp3 = "-0";
								}
								// Removing all excessive leading signs(' and -)
								while (temp2.charAt(0) == '-' | temp2.charAt(0) == '\'' | Character.isDigit(c)) {
									// If there are numbers before the word.
									if (Character.isDigit(c)) {
										temp3 = temp3 + Character.toString(c);
									}
									temp2 = temp2.substring(1);
									c = temp2.charAt(0);
								}
								// Adding the potential number before the word.
								sum = sum + Integer.parseInt(temp3);
								// If its the first element in are obhect array of Word.
								if (count == 0) {
									words[0] = new Word(temp2);
									count++;
								} else {
									// Its not the first element in the array.
									// i will be negative 1 if the word does not exist, otherwise, it will be the
									// index of which the word exists.
									i = words[0].find(temp2, count, words);
									if (i == -1) {
										words[count] = new Word(temp2);
										count++;
									} else {
										words[i].add();
									}
								}
							}
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			// If they entered 1 or 2 for their choice of overwriting the file or not.
			if (o1.choice(ionames)) {
				// Index 2 will be null if they decided to simply overwrite the output file.
				if (ionames[2] == null) {
					try {
						say = new PrintWriter(new File(ionames[1]));
						o1.print(say, count, words, sum);
						say.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				} else {
					try {
						say = new PrintWriter(new File(ionames[2]));
						o1.print(say, count, words, sum);
						say.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}

//Contains operations for everything related to the files.
class IOFile {
	// Gets valid file paths and returns them to the ionames array.
	boolean get(String args[], String ionames[]) {
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		File f1 = new File("");
		File f2 = new File("");
		// Checking the command line argument length.
		switch (args.length) {
		case 1:
			ionames[0] = args[0];
			f1 = new File(ionames[0]);
			break;
		case 2:
			ionames[0] = args[0];
			ionames[1] = args[1];
			f1 = new File(ionames[0]);
			f2 = new File(ionames[1]);
			break;
		default:
			break;
		}
		String choice = new String(" ");
		if (f1.exists() && f2.exists())
			return true;
		// Keep looping until they enter nothing or a valid file path.
		while (!f1.exists() && !choice.isEmpty()) {
			System.out.println("Enter a valid input file name:");
			try {
				choice = get.readLine();
				// Updating our ionames array.
				ionames[0] = choice;
				f1 = new File(choice);
			} catch (IOException e) {

			}
		}
		// Keep looping until they enter nothing or a valid file path.
		while (!f2.exists() && !choice.isEmpty()) {
			System.out.println("Enter a valid output file path:");
			try {
				choice = get.readLine();
				// Updating our ionames array.
				ionames[1] = choice;
				f2 = new File(choice);
			} catch (IOException e) {

			}
		}
		if (f1.exists() && f2.exists()) {
			ionames[0].replaceAll(" ", "");
			ionames[1].replaceAll(" ", "");
			return true;
		}
		return false;
	}

	// Checks if a string ha any valid characters("----" and "''''''").
	boolean good(String s) {
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Character.isDigit(c) | Character.isLetter(c))
				return true;
		}
		return false;
	}

	// Returns true if they enter 1 or 2, and a file path in ionames[2] if they wish
	// to make a backup file.
	boolean choice(String ionames[]) {
		String temp = " ";
		BufferedReader get;
		String choice = " ";
		String name1;
		while (!choice.isEmpty()) {
			System.out.println(
					"Enter 1 to overwrite the output file.\nEnter 2 to back up the original output file.\nEnter 3 to exit.");
			get = new BufferedReader(new InputStreamReader(System.in));
			try {
				choice = get.readLine();
			} catch (IOException e) {

			}
			if (choice.equals("1"))
				return true;
			else if (choice.equals("2")) {
				// Name1 will be the new file name for the backup file.
				name1 = sep(ionames[1]);
				ionames[2] = name1;
				return true;
			} else if (choice.equals("3"))
				return false;
		}
		return false;
	}

	// Used to get the file name, and add "BACKUP" to the name.
	String sep(String s) {
		int i = s.lastIndexOf('\\');
		i++;
		int j = s.lastIndexOf('.');
		String nn = s.substring(0, i);
		nn = nn + s.substring(i, j);
		nn = nn + "BACKUP";
		nn = nn + s.substring(j);
		return nn;
	}

	void print(PrintWriter out, int n, Word words[], int sum) {
		for (int i = 0; i < n; i++) {
			out.print("Word: " + words[i].getname());
			out.print("\nCount: " + words[i].getcount() + "\n");
		}
		out.print("Total unique words: " + n);
		out.print("\nSum: " + sum);
	}
}

//Our word class to make object which will contain the word and count.
class Word {
	private String name;
	private int count;

	// Constructor
	Word(String s) {
		name = s;
		count = 1;
	}

	// Increment count by one.
	void add() {
		count++;
	}

	// get the count.
	int getcount() {
		return count;
	}

	// Get the name.
	String getname() {
		return name;
	}

	// Returns -1 if the string is not in the object array of type Word, otherwise
	// it returns the index of which the string exists.
	int find(String s, int n, Word w[]) {
		for (int i = 0; i < n; i++) {
			if (w[i].getname().equals(s))
				return i;
		}
		return -1;
	}
}