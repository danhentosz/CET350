
/*
 * Program2.java
 * Daniel Hentosz and Nathaniel Dehart
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu
 * GROUP 7
 */
import java.io.*;
import java.util.StringTokenizer;

class Program2 {
	public static void main(String[] args) {
		int i = 0;
		int count = 0;
		int sum = 0;
		String word = null;
		String[] ionames = new String[3];
		Word words[] = new Word[100];
		StringTokenizer inline;
		BufferedReader infile;
		PrintWriter outfile;
		File in;
		IOFile f1 = new IOFile();
		String temp = new String("");
		if (f1.getnames(args, ionames)) {
			// They entered a valid input and output file path
			in = new File(ionames[0]);
			ionames[2] = null;
			if (f1.choice(ionames)) {
				// They selected 1 or 2(backup the file or overwrite the file)
				try {
					infile = new BufferedReader(new FileReader(in));
					while ((word = infile.readLine()) != null) {
						// read every line
						inline = new StringTokenizer(word, "~`!@#$%^&*()_+=[{]}\\|\n\t\r,<>./?;:\" ");
						// each line beacomes several tokens
						while (inline.hasMoreTokens()) {
							word = inline.nextToken().toString().toLowerCase();
							// each token is made a string, so we can use our String methods
							temp = "0";
							if (f1.good(word)) {
								// Checks if the string is just - or '("----" or "''''''")
								if (word.length() > 1) {
									// So we dont get a index out of bounds exception when trying see see slot 2
									while (word.charAt(1) == '-') {
										word = word.substring(1);
									}
								}
								try {
									sum = sum + Integer.parseInt(word);
								} catch (NumberFormatException e) {
									// We know its either a word or numbers followed by a word(123cat)
									char c = word.charAt(0);
									while (word.charAt(0) == '-' | word.charAt(0) == '\'' | Character.isDigit(c)) {
										if (Character.isDigit(c)) {
											// If its number followed by a word(123cat)
											temp = temp + Character.toString(c);
										}
										word = word.substring(1);
										c = word.charAt(0);
									}
									sum = sum + Integer.parseInt(temp);
									// Creating our new Word object(if its needed)
									if (count == 0) {
										words[0] = new Word(word);
										count++;
									} else {
										// i will be -1 if the word is not already in the array
										i = words[0].findWord(words, word, count);
										if (i == -1) {
											words[count] = new Word(word);
											count++;
										} else {
											words[i].addOne();
											;
										}
									}
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				try {
					// Writing to the file
					// if ionames[2] is null then the user wants to overwrite the exiting output
					// file
					if (ionames[2] != null)
						outfile = new PrintWriter(new File(ionames[2]));
					else
						outfile = new PrintWriter(new File(ionames[1]));
					for (int j = 0; j < count; j++) {
						outfile.write("WORD:    " + words[j].getName());
						outfile.write("\nCOUNT:    ");
						outfile.print(words[j].getCount());
						outfile.write("\n");
					}
					outfile.write("TOTAL UNIQUE WORDS:    ");
					outfile.print(count);
					outfile.write("\nSUM OF INTEGERS:    ");
					outfile.print(sum);
					outfile.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}

class Word {
	private String name;
	private int count;

	// Contructor
	Word(String w) {
		name = w;
		count = 1;
	}

	// To display the count
	int getCount() {
		return count;
	}

	// To increment the count
	void addOne() {
		count++;
	}

	// To display the unique word
	String getName() {
		return name;
	}

	// To see if the word is already in the array
	// Returns -1 if not, otherwise returns the index of which the array exists
	int findWord(Word[] list, String w, int n) {
		for (int i = 0; i < n; i++) {
			if (list[i].name.equals(w)) {
				return i;
			}
		}
		return -1;
	}
}

class IOFile {
	// Returns true if both input and output file names are present
	boolean getnames(String args[], String[] ionames) {
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		File in = new File("");
		File out = new File("");
		String temp = new String(" ");
		// Seeing how many command line arguments they entered, if any at all
		switch (args.length) {
		case 1:
			in = new File(args[0]);
			ionames[0] = args[0];
			break;
		case 2:
			in = new File(args[0]);
			out = new File(args[1]);
			ionames[0] = args[0];
			ionames[1] = args[1];
			break;
		default:
			break;
		}
		// While the input file path is incorrect or the user didnt enter anything
		while (!in.exists() && !temp.isEmpty()) {
			System.out.println("Enter a valid input file path:");
			try {
				temp = get.readLine();
				// update our array
				ionames[0] = temp;
				in = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// While the output file path is incorrect or the user didnt enter anything
		while (!out.exists() && !temp.isEmpty()) {
			System.out.println("Enter a valid output file path:");
			try {
				temp = get.readLine();
				// update our array
				ionames[1] = temp;
				out = new File(temp);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (in.exists() && out.exists()) {
			return true;
		}
		return false;
	}

	// Returns true if they enter 1 and a valid new output file path
	// Returns true if they enter 2(To overwrite the output file
	// Returns false otherwise
	boolean choice(String ionames[]) {
		File f = new File("");
		String choice = " ";
		String choice2 = " ";
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		while (!choice.isEmpty()) {
			// prompt the user
			System.out.println("PRESS 1: To backup the exitsing output file");
			System.out.println("PRESS 2: To overwrite the exitsing output file");
			System.out.println("PRESS 3: To exit");
			try {
				choice = get.readLine();
				if (choice.equals("1")) {
					while (!choice2.isEmpty() && !f.exists()) {
						System.out.println("Enter a new valid output file name:");
						choice2 = get.readLine();
						if (choice2.isEmpty())
							return false;
						f = new File(choice2);
					}
					if (f.exists()) {
						// putting data here, we will know their choice later in the program by
						// comparing ionames[2] to null
						ionames[2] = choice2;
						return true;
					}
				} else if (choice.equals("2")) {
					return true;
				} else if (choice.equals("3")) {
					return false;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	// Check if the string is just - or '
	// We need this because we would get an ArrayIndexOutfBoundsException if we
	// didnt have it
	boolean good(String s) {
		char c;
		for (int i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Character.isLetter(c) | Character.isDigit(c)) {
				return true;
			}
		}
		return false;
	}
}