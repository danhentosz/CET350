
/*
 * Program2.java
 * Daniel Hentosz and Nathaniel Dehart
 * Technical Computing Using Java || CET 350 
 * hen3883@calu.edu || deh5850@calu.edu
 * GROUP 7
 */
import java.io.*;
import java.util.StringTokenizer;

class G7P2 {
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
		if (o1.getnames(args, ionames)) {
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
									i = Word.findWord(words, temp2, count);
									if (i == -1) {
										words[count] = new Word(temp2);
										count++;
									} else {
										words[i].addOne();
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
				say = new PrintWriter(new File(ionames[1]));
				o1.print(say, count, words, sum);
				say.close();
				System.out.println("\nSuccessfully wrote data to: " + ionames[1]);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}
}

class Word {
	private String name;
	private int count;

	// Constructor
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
	static int findWord(Word[] list, String w, int n) {
		for (int i = 0; i < n; i++) {
			if (list[i].name.equals(w)) {
				return i;
			}
		}
		return -1;
	}
}

class IOFile {
	// Returns false if the user chose not to enter file names
	boolean getnames(String args[], String[] ioFilePaths) {
		BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
		File in = new File("");
		File out = null;
		boolean quitting = false;
		boolean invalidOut = true;
		String temp = new String(" ");
		// Seeing how many command line arguments they entered, if any at all
		switch (args.length) {
		case 1:
			in = new File(args[0]);
			ioFilePaths[0] = args[0];
			break;
		case 2:
			in = new File(args[0]);
			out = new File(args[1]);
			ioFilePaths[0] = args[0];
			ioFilePaths[1] = args[1];
			break;
		default:
			break;
		}
		// While the input file path is incorrect
		while (!in.exists() && !quitting) {
			System.out.print("\nEnter a valid input file path:\n> ");
			try {
				temp = get.readLine();
				// update our array
				ioFilePaths[0] = new String(temp);
				in = new File(temp);
				if (!in.exists()) {
					System.out.println("\nThe entered file path does not exist."
							+ "\nEnter 1 to try again"
							+ "\nEnter anything else to exit");
					System.out.print("> ");
					temp = get.readLine();
					if (!temp.equals("1")) {
						quitting = true;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// While the output file path is invalid
		while (invalidOut && !quitting) {
			try {
				if (out == null) {
					System.out.print("\nEnter an output file path:\n> ");
					temp = get.readLine();
					// update our array
					ioFilePaths[1] = new String(temp);
					out = new File(temp);
				} else {
					// Checks if a file with the output file name already exists
					if (out.exists()) {
						System.out.print("\nAn output file with that name already exists."
								+ "\nEnter 1 to overwrite it"
								+ "\nEnter 2 to back it up, then write to the file"
								+ "\nEnter 3 to enter a different name"
								+ "\nEnter anything else to exit\n> ");
						temp = get.readLine();
						if (temp.equals("1")) {
							out.delete();
							out.createNewFile();
							invalidOut = false;
						}
						else if (temp.equals("2")) {
							backupFile(out);
							out.createNewFile();
							invalidOut = false;
						}
						else if (temp.equals("3")) {
							out = null;
						}
						else {
							quitting = true;
						}
					} else if (out.createNewFile()) {
							invalidOut = false;	
					}
				}
			} catch (IOException e) {
				e.printStackTrace();		
			}
		}
		return !quitting;
	}
	
	void print(PrintWriter out, int n, Word words[], int sum) {
		for (int i = 0; i < n; i++) {
			out.print("Word: " + words[i].getName());
			out.print("\nCount: " + words[i].getCount() + "\n");
		}
		out.print("Total unique words: " + n);
		out.print("\nSum: " + sum);
	}
	
	// Changes the name of "theFile" to it's current name with "BACKUP" appended to the end
	// Keeps the directory and extension of "theFile" intact
	void backupFile(File theFile) {
		File newFile = new File(getFileDirectory(theFile).concat(getFileNameNoExtension(theFile)).concat("BACKUP").concat(getFileExtension(theFile)));
		// Checks if a file matching this backup name already exists
		if (newFile.exists())
			newFile.delete();
		theFile.renameTo(newFile);
	}
	
	// Returns a String containing the extension of "theFile"
	String getFileExtension(File theFile) {
		return theFile.getName().substring(theFile.getName().indexOf('.'), theFile.getName().length());
	}
	
	// Returns a String containing the name without the extension of "theFile"
	String getFileNameNoExtension(File theFile) {
		return theFile.getName().substring(0, theFile.getName().indexOf('.'));
	}
	
	// Returns a String containing the directory of "theFile"
	String getFileDirectory(File theFile) {
		return theFile.getPath().substring(0, theFile.getPath().length() - theFile.getName().length());
	}
	
	
    // Check if the string is just - or '
	// We need this because we would get an ArrayIndexOutfBoundsException if we
	// didn't have it
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